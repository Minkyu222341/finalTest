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
import sparta.seed.img.domain.Img;
import sparta.seed.img.repository.ImgRepository;
import sparta.seed.s3.S3Dto;
import sparta.seed.s3.S3Uploader;
import sparta.seed.sercurity.UserDetailsImpl;
import sparta.seed.util.DateUtil;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    final SliceImpl<CommunityResponseDto> communityResponseDtos = new SliceImpl<>(allCommunityList, pageable, hasNext);
    return ResponseEntity.ok().body(communityResponseDtos);
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
      communityList.add(
          CommunityResponseDto.builder()
              .communityId(community.getId())
              .img(community.getImg())
              .title(community.getTitle())
              .startDate(community.getStartDate())
              .endDate(community.getEndDate())
              .limitScore(community.getLimitScore())
              .limitParticipants(community.getLimitParticipants())
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

  /**
   * 게시글 작성
   */
  public ResponseEntity<Community> createCommunity(CommunityRequestDto requestDto, MultipartFile multipartFile, UserDetailsImpl userDetails) throws IOException {
    Long loginUserId = userDetails.getId();
    String nickname = userDetails.getNickname();
    if (multipartFile != null) {
      Community community = createCommunity(requestDto, loginUserId, nickname);
      Participants groupLeader = getGroupLeader(loginUserId, nickname, community);

        S3Dto upload = s3Uploader.upload(multipartFile);
        Img findImage = Img.builder()
                .imgUrl(upload.getUploadImageUrl())
                .fileName(upload.getFileName())
                .community(community)
                .build();
      community.setImg(findImage);
      imgRepository.save(findImage);

      communityRepository.save(community);
      participantsRepository.save(groupLeader);
      return ResponseEntity.ok().body(community);
    }

    Community community = createCommunity(requestDto, loginUserId, nickname);
    Participants groupLeader = getGroupLeader(loginUserId, nickname, community);
    communityRepository.save(community);
    participantsRepository.save(groupLeader);
    return ResponseEntity.ok().body(community);

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


  /**
   * 게시글 상세조회
   */
  public ResponseEntity<CommunityResponseDto> getDetailCommunity(Long id, UserDetailsImpl userDetails) throws ParseException {
    Optional<Community> community = communityRepository.findById(id);
    Long certifiedProof = getCertifiedProof(community.get());
    CommunityResponseDto communityResponseDto = CommunityResponseDto.builder()
            .communityId(community.get().getId())
            .createAt(String.valueOf(community.get().getCreatedAt()))
            .nickname(community.get().getNickname())
            .img(community.get().getImg())
            .startDate(community.get().getStartDate())
            .endDate(community.get().getEndDate())
            .limitScore(community.get().getLimitScore())
            .limitParticipants(community.get().getLimitParticipants())
            .secret(community.get().isPasswordFlag())
            .password(community.get().getPassword())
            .title(community.get().getTitle())
            .content(community.get().getContent())
            .currentPercent(((double) community.get().getParticipantsList().size() / (double) community.get().getLimitParticipants()) * 100)
            .successPercent((Double.valueOf(certifiedProof) / (double) community.get().getLimitScore()) * 100) // 인증글좋아요 갯수가 참가인원 절반이상인 글만 적용
            .writer(userDetails != null && community.get().getMemberId().equals(userDetails.getId()))
            .dateStatus(getDateStatus(community.get()))
            .participant(userDetails!=null && participant(userDetails, community.get()))
            .build();
    return ResponseEntity.ok().body(communityResponseDto);
  }

  private Long getCertifiedProof(Community community) {
    Long certifiedProof = proofRepository.getCertifiedProof(community);
    return certifiedProof;
  }

  /**
   * 게시글 수정
   */
  @Transactional
  public ResponseEntity<CommunityResponseDto> updateCommunity(Long id, CommunityRequestDto communityRequestDto,
                                                 MultipartFile multipartFile, UserDetailsImpl userDetails) throws IOException, ParseException {

    Community community = communityRepository.findById(id)
    				.orElseThrow(() -> new IllegalArgumentException("해당 인증글이 존재하지 않습니다."));
    Long certifiedProof = getCertifiedProof(community);

    if(userDetails != null && community.getMemberId().equals(userDetails.getId())){
      community.update(communityRequestDto);

      if(multipartFile != null){

        if(imgRepository.findByCommunity(community) != null){
          imgRepository.delete(community.getImg());
        }

        S3Dto upload = s3Uploader.upload(multipartFile);

        Img findImage = Img.builder()
            .imgUrl(upload.getUploadImageUrl())
            .fileName(upload.getFileName())
            .community(community)
            .build();

        community.setImg(findImage);

        imgRepository.save(findImage);
      }
      return ResponseEntity.ok().body(
          CommunityResponseDto.builder()
              .communityId(community.getId())
              .createAt(String.valueOf(community.getCreatedAt()))
              .nickname(community.getNickname())
              .img(community.getImg())
              .startDate(community.getStartDate())
              .endDate(community.getEndDate())
              .limitScore(community.getLimitScore())
              .limitParticipants(community.getLimitParticipants())
              .secret(community.isPasswordFlag())
              .password(community.getPassword())
              .title(community.getTitle())
              .content(community.getContent())
              .currentPercent(((double) community.getParticipantsList().size() / (double) community.getLimitParticipants()) * 100)
              .successPercent((Double.valueOf(certifiedProof) / (double) community.getLimitScore()) * 100) // 인증글좋아요 갯수가 참가인원 절반이상인 글만 적용
              .writer(community.getMemberId().equals(userDetails.getId()))
              .dateStatus(getDateStatus(community))
              .participant(participant(userDetails, community))
              .build());

    }throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
  }

  /**
   * 게시글 삭제
   */
  public ResponseEntity<Boolean> deleteCommunity(Long id, UserDetailsImpl userDetails) {
    Optional<Community> Community = communityRepository.findById(id);
    if (Community.get().getMemberId().equals(userDetails.getId())) {
      communityRepository.deleteById(id);
      return ResponseEntity.ok().body(true);
    }
    return ResponseEntity.badRequest().body(false);
  }

  /**
   * 그룹미션 참여 , 취소 하기
   */
  @Transactional
  public ResponseEntity<Boolean> joinMission(Long id, UserDetailsImpl userDetails) throws Exception {
    Optional<Community> community = communityRepository.findById(id);
    Long loginUserId = userDetails.getId();
    String nickname = userDetails.getNickname();
    long limitParticipantCount = community.get().getLimitParticipants();
    int participantSize = community.get().getParticipantsList().size();
    if (community.get().getMemberId().equals(userDetails.getId())) {
      throw new Exception("작성자는 누를수 없습니다");
    }

    if (participantsRepository.existsByCommunityAndMemberId(community.get(), loginUserId) || participantSize >= limitParticipantCount) {
      participantsRepository.deleteByMemberId(loginUserId);
      return ResponseEntity.ok().body(false);
    }
    Participants participants = Participants.builder()
            .community(community.get())
            .memberId(loginUserId)
            .nickname(nickname)
            .build();
    community.get().addParticipant(participants);
    participantsRepository.save(participants);
    return ResponseEntity.ok().body(true);
  }

  /**
   * 참여현황
   */
  public ResponseEntity<List<Participants>> getParticipantsList(Long id) {
    Optional<Community> community = communityRepository.findById(id);
    List<Participants> participantsList = community.get().getParticipantsList();
    return ResponseEntity.ok().body(participantsList);
  }


  private String getDateStatus(Community community) throws ParseException {
    return dateUtil.dateStatus(community.getStartDate(), community.getEndDate());
  }
}
