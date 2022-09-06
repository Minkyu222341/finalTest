package sparta.seed.service;

import com.querydsl.core.QueryResults;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sparta.seed.domain.Community;
import sparta.seed.domain.Img;
import sparta.seed.domain.Participants;
import sparta.seed.domain.dto.requestDto.CommunityRequestDto;
import sparta.seed.domain.dto.responseDto.CommunityResponseDto;
import sparta.seed.domain.dto.requestDto.CommunitySearchCondition;
import sparta.seed.repository.CommunityRepository;
import sparta.seed.repository.ImgRepository;
import sparta.seed.repository.ParticipantsRepository;
import sparta.seed.repository.ProofRepository;
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
      communityList.add(CommunityResponseDto.builder()
              .communityId(community.getId())
              .imgList(community.getImgList())
              .title(community.getTitle())
              .isParticipant(userDetails != null && isParticipant(userDetails, community))
              .participantsCnt(community.getParticipantsList().size())
              .currentPercent(((double) community.getParticipantsList().size() / (double) community.getLimitParticipants()) * 100)
              .successPercent((Double.valueOf(certifiedProof) / (double) community.getLimitScore()) * 100)
              .isWriter(userDetails != null && community.getMemberId().equals(userDetails.getId()))
              .dateStatus(getDateStatus(community))
              .build());
    }
    return communityList;
  }

  private Boolean isParticipant(UserDetailsImpl userDetails, Community community) {
    Boolean isParticipant = participantsRepository.existsByCommunityAndMemberId(community, userDetails.getId());
    return isParticipant;
  }

  /**
   * 게시글 작성
   */
  public ResponseEntity<Community> createCommunity(CommunityRequestDto requestDto, List<MultipartFile> multipartFile, UserDetailsImpl userDetails) throws IOException {
    Long loginUserId = userDetails.getId();
    String nickname = userDetails.getNickname();
    if (multipartFile != null) {
      Community community = getCommunity(requestDto, loginUserId, nickname);
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
      return ResponseEntity.ok().body(community);
    }
    Community community = getCommunity(requestDto, loginUserId, nickname);
    Participants groupLeader = getGroupLeader(loginUserId, nickname, community);
    communityRepository.save(community);
    participantsRepository.save(groupLeader);
    return ResponseEntity.ok().body(community);

  }

  private Community getCommunity(CommunityRequestDto requestDto, Long loginUserId, String nickname) {
    return Community.builder()
            .title(requestDto.getTitle())
            .content(requestDto.getContent())
            .isSecret(requestDto.isSecret())
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
    System.out.println(userDetails.getId() + "  디테일 서비스" );
    Optional<Community> community = communityRepository.findById(id);
    Long certifiedProof = getCertifiedProof(community.get());
    CommunityResponseDto communityResponseDto = CommunityResponseDto.builder()
            .communityId(community.get().getId())
            .createAt(String.valueOf(community.get().getCreatedAt()))
            .nickname(community.get().getNickname())
            .imgList(community.get().getImgList())
            .startDate(community.get().getStartDate())
            .endDate(community.get().getEndDate())
            .isSecret(community.get().isSecret())
            .password(community.get().getPassword())
            .title(community.get().getTitle())
            .content(community.get().getContent())
            .currentPercent(((double) community.get().getParticipantsList().size() / (double) community.get().getLimitParticipants()) * 100)
            .successPercent((Double.valueOf(certifiedProof) / (double) community.get().getLimitScore()) * 100) // 인증글좋아요 갯수가 참가인원 절반이상인 글만 적용
            .isWriter(userDetails != null && community.get().getMemberId().equals(userDetails.getId()))
            .dateStatus(getDateStatus(community.get()))
            .isParticipant(userDetails!=null && isParticipant(userDetails, community.get()))
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
  public ResponseEntity<Boolean> updateCommunity(Long id, CommunityRequestDto communityRequestDto, UserDetailsImpl userDetails) {
    Optional<Community> Community = communityRepository.findById(id);
    if (Community.get().getMemberId().equals(userDetails.getId())) {
      Community.get().update(communityRequestDto);
      return ResponseEntity.ok().body(true);
    }
    return ResponseEntity.badRequest().body(false);
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
