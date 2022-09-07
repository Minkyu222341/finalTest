package sparta.seed.community.service;

import com.querydsl.core.QueryResults;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sparta.seed.community.domain.Community;
import sparta.seed.community.domain.Participants;
import sparta.seed.community.domain.dto.requestdto.CommunityRequestDto;
import sparta.seed.community.domain.dto.requestdto.CommunitySearchCondition;
import sparta.seed.community.domain.dto.responsedto.CommunityResponseDto;
import sparta.seed.community.repository.CommunityRepository;
import sparta.seed.community.repository.ParticipantsRepository;
import sparta.seed.community.repository.ProofRepository;
import sparta.seed.exception.CustomException;
import sparta.seed.exception.ErrorCode;
import sparta.seed.img.domain.Img;
import sparta.seed.img.repository.ImgRepository;
import sparta.seed.msg.ResponseMsg;
import sparta.seed.s3.S3Dto;
import sparta.seed.s3.S3Uploader;
import sparta.seed.sercurity.UserDetailsImpl;
import sparta.seed.util.DateUtil;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityService {

  private final CommunityRepository communityRepository;
  private final ImgRepository imgRepository;
  private final S3Uploader s3Uploader;
  private final ParticipantsRepository participantsRepository;
  private final DateUtil dateUtil;
  private final ProofRepository proofRepository;

  /**
   * 게시글 전체조회 , 검색 , 스크롤
   */
  public ResponseEntity<Slice<CommunityResponseDto>> getAllCommunity(Pageable pageable, CommunitySearchCondition condition, UserDetailsImpl userDetails) throws ParseException {
    QueryResults<Community> allCommunity = communityRepository.getAllCommunity(pageable, condition);
    List<CommunityResponseDto> allCommunityList = getAllCommunityList(allCommunity, userDetails);
    boolean hasNext = hasNextPage(pageable, allCommunityList);
    SliceImpl<CommunityResponseDto> communityResponseDtos = new SliceImpl<>(allCommunityList, pageable, hasNext);
    return ResponseEntity.ok().body(communityResponseDtos);
  }
  /**
   * 게시글 작성
   */
  public ResponseEntity<String> createCommunity(CommunityRequestDto requestDto, List<MultipartFile> multipartFile, UserDetailsImpl userDetails) throws IOException {
    Long loginUserId = userDetails.getId();
    String nickname = userDetails.getNickname();
    if (multipartFile != null) {
      Community community = createCommunity(requestDto, loginUserId, nickname);
      Participants groupLeader = getGroupLeader(loginUserId, nickname, community);
      List<Img> imgList = new ArrayList<>();
      for (MultipartFile file : multipartFile) {
        S3Dto upload = s3Uploader.upload(file);
        Img findImage = Img.builder()
                .imgUrl(upload.getUploadImageUrl())
                .fileName(upload.getFileName())
                .community(community)
                .build();
        imgList.add(findImage);
        imgRepository.save(findImage);

      }
      communityRepository.save(community);
      participantsRepository.save(groupLeader);
      return ResponseEntity.ok().body(ResponseMsg.WRITE_SUCCESS.getMsg());
    }
    Community community = createCommunity(requestDto, loginUserId, nickname);
    Participants groupLeader = getGroupLeader(loginUserId, nickname, community);
    communityRepository.save(community);
    participantsRepository.save(groupLeader);
    return ResponseEntity.ok().body(ResponseMsg.WRITE_SUCCESS.getMsg());

  }
  /**
   * 게시글 상세조회
   */
  public ResponseEntity<CommunityResponseDto> getDetailCommunity(Long id, UserDetailsImpl userDetails) throws ParseException {
    Community community = findTheCommunityByMemberId(id);
    Long certifiedProof = getCertifiedProof(community);
    CommunityResponseDto communityResponseDto = CommunityResponseDto.builder()
            .communityId(community.getId())
            .createAt(String.valueOf(community.getCreatedAt()))
            .nickname(community.getNickname())
            .imgList(community.getImgList())
            .startDate(community.getStartDate())
            .endDate(community.getEndDate())
            .secret(community.isSecret())
            .password(community.getPassword())
            .title(community.getTitle())
            .content(community.getContent())
            .currentPercent(((double) community.getParticipantsList().size() / (double) community.getLimitParticipants()) * 100)
            .successPercent((Double.valueOf(certifiedProof) / (double) community.getLimitScore()) * 100) // 인증글좋아요 갯수가 참가인원 절반이상인 글만 적용
            .writer(userDetails != null && community.getMemberId().equals(userDetails.getId()))
            .dateStatus(getDateStatus(community))
            .participant(userDetails != null && participant(userDetails, community))
            .build();
    return ResponseEntity.ok().body(communityResponseDto);
  }
  /**
   * 게시글 수정
   */
  @Transactional
  public ResponseEntity<String> updateCommunity(Long id, CommunityRequestDto communityRequestDto, UserDetailsImpl userDetails) {
    Community community = findTheCommunityByMemberId(id);
    validateWriter(userDetails, community);
    community.update(communityRequestDto);
    return ResponseEntity.ok().body(ResponseMsg.UPDATE_SUCCESS.getMsg());
  }
  /**
   * 게시글 삭제
   */
  public ResponseEntity<String> deleteCommunity(Long id, UserDetailsImpl userDetails) {
    Community community = findTheCommunityByMemberId(id);
    validateWriter(userDetails, community);
    communityRepository.deleteById(id);
    return ResponseEntity.ok().body(ResponseMsg.DELETED_SUCCESS.getMsg());
  }
  /**
   * 그룹미션 참여 , 취소 하기
   */
  @Transactional
  public ResponseEntity<String> joinMission(Long id, UserDetailsImpl userDetails){
    Community community = findTheCommunityByMemberId(id);
    long limitParticipantCount = community.getLimitParticipants();
    int participantSize = community.getParticipantsList().size();
    if (community.getMemberId().equals(userDetails.getId()) || participantsRepository.existsByCommunityAndMemberId(community, userDetails.getId())) {
      throw new CustomException(ErrorCode.ALREADY_PARTICIPATED);
    }
    if (participantSize >= limitParticipantCount) {
      throw new CustomException(ErrorCode.EXCESS_PARTICIPANT);
    }
    Participants participants = Participants.builder()
            .community(community)
            .memberId(userDetails.getId())
            .nickname(userDetails.getNickname())
            .build();
    community.addParticipant(participants);
    participantsRepository.save(participants);
    return ResponseEntity.ok().body(ResponseMsg.JOIN_SUCCESS.getMsg());
  }
  /**
   * 참여현황
   */
  public ResponseEntity<List<Participants>> getParticipantsList(Long id) {
    Community community = findTheCommunityByMemberId(id);
    List<Participants> participantsList = community.getParticipantsList();
    return ResponseEntity.ok().body(participantsList);
  }

  private boolean hasNextPage(Pageable pageable, List<CommunityResponseDto> CommunityList) {
    boolean hasNext = false;
    if (CommunityList.size() > pageable.getPageSize()) {
      CommunityList.remove(pageable.getPageSize());
      hasNext = true;
    }
    return hasNext;
  }
  private List<CommunityResponseDto> getAllCommunityList(QueryResults<Community> allCommunity, UserDetailsImpl userDetails) throws ParseException {
    List<CommunityResponseDto> communityList = new ArrayList<>();
    for (Community community : allCommunity.getResults()) {
      Long certifiedProof = getCertifiedProof(community);
      communityList.add(CommunityResponseDto.builder()
              .communityId(community.getId())
              .imgList(community.getImgList())
              .title(community.getTitle())
              .participant(userDetails != null && participant(userDetails, community))
              .participantsCnt(community.getParticipantsList().size())
              .currentPercent(((double) community.getParticipantsList().size() / (double) community.getLimitParticipants()) * 100)
              .successPercent((Double.valueOf(certifiedProof) / (double) community.getLimitScore()) * 100)
              .writer(userDetails != null && community.getMemberId().equals(userDetails.getId()))
              .dateStatus(getDateStatus(community))
              .build());
    }
    return communityList;
  }
  private Boolean participant(UserDetailsImpl userDetails, Community community) {
    Boolean participant = participantsRepository.existsByCommunityAndMemberId(community, userDetails.getId());
    return participant;
  }
  private Community createCommunity(CommunityRequestDto requestDto, Long loginUserId, String nickname) {
    return Community.builder()
            .title(requestDto.getTitle())
            .content(requestDto.getContent())
            .secret(requestDto.isSecret())
            .password(requestDto.getPassword())
            .memberId(loginUserId)
            .nickname(nickname)
            .startDate(requestDto.getStartDate())
            .endDate(requestDto.getEndDate())
            .limitParticipants(requestDto.getLimitParticipants())
            .limitScore(requestDto.getLimitScore())
            .build();
  }
  private Participants getGroupLeader(Long loginUserId, String nickname, Community community) {
    return Participants.builder()
            .nickname(nickname)
            .memberId(loginUserId)
            .community(community)
            .build();
  }
  private Community findTheCommunityByMemberId(Long id) {
    return communityRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COMMUNITY));
  }
  private Long getCertifiedProof(Community community) {
    Long certifiedProof = proofRepository.getCertifiedProof(community);
    return certifiedProof;
  }
  private void validateWriter(UserDetailsImpl userDetails, Community community) {
    if (community.getMemberId().equals(userDetails.getId())) {
      throw new CustomException(ErrorCode.INCORRECT_USERID);
    }
  }
  private String getDateStatus(Community community) throws ParseException {
    return dateUtil.dateStatus(community.getStartDate(), community.getEndDate());
  }
}
