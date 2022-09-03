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
import sparta.seed.domain.dto.responseDto.CommunitySearchCondition;
import sparta.seed.repository.CommunityRepository;
import sparta.seed.repository.ImgRepository;
import sparta.seed.repository.ParticipantsRepository;
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
      String dateStatus = dateUtil.dateStatus(community.getStartDate(), community.getEndDate());
      if (!dateStatus.equals("before")) {
        communityList.add(CommunityResponseDto.builder()
                .communityId(community.getId())
                .imgList(community.getImgList())
                .title(community.getTitle())
                .isRecruitment(dateStatus.equals("before"))
                .participantsCnt(community.getParticipantsList().size())
                .successPercent((Double.valueOf(community.getProofList().size()) / Double.valueOf(community.getLimitScore())) * 100)
                .isWriter(userDetails != null && community.getMemberId().equals(userDetails.getId()))
                .build());
      } else {
        communityList.add(CommunityResponseDto.builder()
                .communityId(community.getId())
                .imgList(community.getImgList())
                .title(community.getTitle())
                .isRecruitment(dateStatus.equals("before"))
                .participantsCnt(community.getParticipantsList().size())
                .currentPercent((Double.valueOf(community.getParticipantsList().size()) / Double.valueOf(community.getLimitParticipants())) * 100)
                .isWriter(userDetails != null && community.getMemberId().equals(userDetails.getId()))
                .build());
      }
    }
    return communityList;
  }


  /**
   * 게시글 작성
   */
  public ResponseEntity<Community> createCommunity(CommunityRequestDto requestDto, List<MultipartFile> multipartFile, UserDetailsImpl userDetails) throws IOException {
    Long loginUserId = userDetails.getId();
    String nickname = userDetails.getNickname();
    List<Img> imgList = new ArrayList<>();
    if (multipartFile != null) {
      Community community = Community.builder()
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
      return ResponseEntity.ok().body(community);
    }
    Community community = Community.builder()
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
    communityRepository.save(community);
    return ResponseEntity.ok().body(community);

  }


  /**
   * 게시글 상세조회
   */
  public ResponseEntity<CommunityResponseDto> getDetailCommunity(Long id, UserDetailsImpl userDetails) throws ParseException {
    Optional<Community> community = communityRepository.findById(id);
    String dateStatus = dateUtil.dateStatus(community.get().getStartDate(), community.get().getEndDate());
    if (!dateStatus.equals("before")) {
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
              .successPercent((Double.valueOf(community.get().getProofList().size()) / Double.valueOf(community.get().getLimitScore())) * 100)
              .isWriter(userDetails != null && community.get().getMemberId().equals(userDetails.getId()))
              .build();

      return ResponseEntity.ok().body(communityResponseDto);
    }
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
            .currentPercent((Double.valueOf(community.get().getParticipantsList().size()) / Double.valueOf(community.get().getLimitParticipants())) * 100)
            .isWriter(userDetails != null && community.get().getMemberId().equals(userDetails.getId()))
            .build();
    System.out.println(community.get().getParticipantsList().size() / community.get().getLimitParticipants() * 100 + "계산");
    return ResponseEntity.ok().body(communityResponseDto);
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
  public ResponseEntity<Boolean> joinMission(Long id, UserDetailsImpl userDetails) {
    Optional<Community> community = communityRepository.findById(id);
    Long loginUserId = userDetails.getId();
    String nickname = userDetails.getNickname();
    long limitParticipantCount = community.get().getLimitParticipants();
    int participantSize = community.get().getParticipantsList().size();
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
}
