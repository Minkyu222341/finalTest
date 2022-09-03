package sparta.seed.service;

import com.querydsl.core.QueryResults;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
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

import java.io.IOException;
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

  /**
   * 게시글 전체조회 , 검색 , 스크롤
   */
  public Slice<CommunityResponseDto> getAllCommunity(Pageable pageable, CommunitySearchCondition condition) {
    QueryResults<Community> allCommunity = communityRepository.getAllCommunity(pageable, condition);
    List<CommunityResponseDto> allCommunityList = getAllCommunityList(allCommunity);
    boolean hasNext = hasNextPage(pageable, allCommunityList);
    return new SliceImpl<>(allCommunityList, pageable, hasNext);
  }

  private List<CommunityResponseDto> getAllCommunityList(QueryResults<Community> allCommunity) {
    List<CommunityResponseDto> communityList = new ArrayList<>();
    for (Community community : allCommunity.getResults()) {
      communityList.add(CommunityResponseDto.builder()
              .communityId(community.getId())
              .imgList(community.getImgList())
              .title(community.getTitle())
              .isRecruitment(community.isRecruitment())
              .participantsCnt(community.getParticipantsList().size())
              .participantsPer(community.getParticipantsList().size() / community.getLimitParticipants())
              .build());
    }
    return communityList;
  }

  private boolean hasNextPage(Pageable pageable, List<CommunityResponseDto> CommunityList) {
    boolean hasNext = false;
    if (CommunityList.size() > pageable.getPageSize()) {
      CommunityList.remove(pageable.getPageSize());
      hasNext = true;
    }
    return hasNext;
  }

  /**
   * 게시글 작성
   */
  public Community createCommunity(CommunityRequestDto requestDto, List<MultipartFile> multipartFile, UserDetailsImpl userDetails) throws IOException {
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
      return community;
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
    return community;
  }


  /**
   * 게시글 상세조회
   */
  public CommunityResponseDto getDetailCommunity(Long id) {
    Optional<Community> detailCommunity = communityRepository.findById(id);

    return CommunityResponseDto.builder()
            .communityId(detailCommunity.get().getId())
            .createAt(String.valueOf(detailCommunity.get().getCreatedAt()))
            .nickname(detailCommunity.get().getNickname())
            .imgList(detailCommunity.get().getImgList())
            .startDate(detailCommunity.get().getStartDate())
            .endDate(detailCommunity.get().getEndDate())
            .isSecret(detailCommunity.get().isSecret())
            .password(detailCommunity.get().getPassword())
            .title(detailCommunity.get().getTitle())
            .content(detailCommunity.get().getContent())
            .build();
  }


  /**
   * 게시글 수정
   */
  @Transactional
  public Boolean updateCommunity(Long id, CommunityRequestDto communityRequestDto, UserDetailsImpl userDetails) {
    Optional<Community> Community = communityRepository.findById(id);
    if (Community.get().getMemberId().equals(userDetails.getId())) {
      Community.get().update(communityRequestDto);
      return true;
    }
    return false;
  }

  /**
   * 게시글 삭제
   */
  public Boolean deleteCommunity(Long id, UserDetailsImpl userDetails) {
    Optional<Community> Community = communityRepository.findById(id);
    if (Community.get().getMemberId().equals(userDetails.getId())) {
      communityRepository.deleteById(id);
      return true;
    }
    return false;
  }

  /**
   * 그룹미션 참여 , 취소 하기
   */
  @Transactional
  public Boolean joinMission(Long id, UserDetailsImpl userDetails) {
    Optional<Community> community = communityRepository.findById(id);
    Long loginUserId = userDetails.getId();
    String nickname = userDetails.getNickname();
    long limitParticipantCount = community.get().getLimitParticipants();
    int participantSize = community.get().getParticipantsList().size();
    if (participantsRepository.existsByCommunityAndMemberId(community.get(), loginUserId) || participantSize >= limitParticipantCount) {
      participantsRepository.deleteByMemberId(loginUserId);
      return false;
    }
    Participants participants = Participants.builder()
            .community(community.get())
            .memberId(loginUserId)
            .nickname(nickname)
            .build();
    community.get().addParticipant(participants);
    participantsRepository.save(participants);
    return true;
  }

  /**
   * 참여현황
   */
  public List<Participants> getParticipantsList(Long id) {
    Optional<Community> community = communityRepository.findById(id);
    return community.get().getParticipantsList();
  }
}
