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
import sparta.seed.community.domain.dto.responsedto.CommunityAllResponseDto;
import sparta.seed.community.domain.dto.responsedto.CommunityResponseDto;
import sparta.seed.community.repository.CommunityRepository;
import sparta.seed.community.repository.ParticipantsRepository;
import sparta.seed.community.repository.ProofRepository;
import sparta.seed.exception.CustomException;
import sparta.seed.exception.ErrorCode;
import sparta.seed.msg.ResponseMsg;
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
  private final S3Uploader s3Uploader;
  private final ParticipantsRepository participantsRepository;
  private final DateUtil dateUtil;
  private final ProofRepository proofRepository;


  public ResponseEntity<Slice<CommunityAllResponseDto>> getAllCommunity(Pageable pageable, CommunitySearchCondition condition, UserDetailsImpl userDetails) throws ParseException {
    QueryResults<Community> allCommunity = communityRepository.getAllCommunity(pageable, condition);
    List<CommunityAllResponseDto> allCommunityList = getAllCommunityList(allCommunity, userDetails);
    boolean hasNext = hasNextPage(pageable, allCommunityList);
    SliceImpl<CommunityAllResponseDto> communityResponseDtos = new SliceImpl<>(allCommunityList, pageable, hasNext);
    return ResponseEntity.ok().body(communityResponseDtos);
  }

  public ResponseEntity<String> createCommunity(CommunityRequestDto requestDto, MultipartFile multipartFile, UserDetailsImpl userDetails) {
    try {
      Long loginUserId = userDetails.getId();
      String nickname = userDetails.getNickname();

        Community community = createCommunity(requestDto, multipartFile, loginUserId, nickname);
        Participants groupLeader = getGroupLeader(loginUserId, nickname, community);

        communityRepository.save(community);
        participantsRepository.save(groupLeader);

        return ResponseEntity.ok().body(ResponseMsg.WRITE_SUCCESS.getMsg());

    }catch (Exception e) {throw new CustomException(ErrorCode.UNKNOWN_USER);}
  }

  public ResponseEntity<CommunityResponseDto> getDetailCommunity(Long id, UserDetailsImpl userDetails) throws ParseException {
    Community community = findTheCommunityByMemberId(id);
    Long certifiedProof = getCertifiedProof(community);
    CommunityResponseDto communityResponseDto = CommunityResponseDto.builder()
        .communityId(community.getId())
        .createAt(String.valueOf(community.getCreatedAt()))
        .nickname(community.getNickname())
        .title(community.getTitle())
        .content(community.getContent())
        .img(community.getImg())
        .participantsList(community.getParticipantsList())
        .participantsCnt(community.getParticipantsList().size())
        .limitParticipants(community.getLimitParticipants())
        .currentPercent(((double) community.getParticipantsList().size() / (double) community.getLimitParticipants()) * 100)
        .participant(userDetails != null && participant(userDetails, community))
        .limitScore(community.getLimitScore())
        .successPercent((Double.valueOf(certifiedProof) / (double) community.getParticipantsList().size()) * 100)
        .startDate(community.getStartDate())
        .endDate(community.getEndDate())
        .dateStatus(getDateStatus(community))
        .secret(community.isPasswordFlag())
        .password(community.getPassword())
        .writer(userDetails != null && community.getMemberId().equals(userDetails.getId()))
        .build();
    return ResponseEntity.ok().body(communityResponseDto);
  }

 @Transactional
public ResponseEntity<String> updateCommunity(Long id, CommunityRequestDto communityRequestDto,
                                               MultipartFile multipartFile, UserDetailsImpl userDetails) throws IOException {

   Community community = findTheCommunityByMemberId(id);

   if (userDetails != null && community.getMemberId().equals(userDetails.getId())) {
     community.update(communityRequestDto);

     if (communityRequestDto.isDelete() | multipartFile != null) {
       community.setImg(returnImageUrl(multipartFile));
     }

     return ResponseEntity.ok().body(ResponseMsg.UPDATE_SUCCESS.getMsg());
   }throw new CustomException(ErrorCode.INCORRECT_USERID);
 }

  public ResponseEntity<String> deleteCommunity(Long id, UserDetailsImpl userDetails) {
    Community community = findTheCommunityByMemberId(id);
    if(validateWriter(userDetails, community)){
      communityRepository.deleteById(id);
    }
    return ResponseEntity.ok().body(ResponseMsg.DELETED_SUCCESS.getMsg());
  }

  @Transactional
  public ResponseEntity<String> joinMission(Long id, UserDetailsImpl userDetails) {
    Community community = findTheCommunityByMemberId(id);
    if(userDetails != null) {
      if (community.getMemberId().equals(userDetails.getId()) || participantsRepository.existsByCommunityAndMemberId(community, userDetails.getId())) {
        throw new CustomException(ErrorCode.ALREADY_PARTICIPATED);
      }
      if (community.getParticipantsList().size() >= community.getLimitParticipants()) {
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
    }throw new CustomException(ErrorCode.UNKNOWN_USER);
  }

  public ResponseEntity<List<Participants>> getParticipantsList(Long id) {
    Community community = findTheCommunityByMemberId(id);
    List<Participants> participantsList = community.getParticipantsList();
    return ResponseEntity.ok().body(participantsList);
  }

  private boolean hasNextPage(Pageable pageable, List<CommunityAllResponseDto> CommunityList) {
    boolean hasNext = false;
    if (CommunityList.size() > pageable.getPageSize()) {
      CommunityList.remove(pageable.getPageSize());
      hasNext = true;
    }
    return hasNext;
  }

  private List<CommunityAllResponseDto> getAllCommunityList(QueryResults<Community> allCommunity, UserDetailsImpl
          userDetails) throws ParseException {
    List<CommunityAllResponseDto> communityList = new ArrayList<>();
    for (Community community : allCommunity.getResults()) {
      Long certifiedProof = getCertifiedProof(community);
      communityList.add(CommunityAllResponseDto.builder()
          .communityId(community.getId())
          .nickname(community.getNickname())
          .title(community.getTitle())
          .img(community.getImg())
          .currentPercent(((double) community.getParticipantsList().size() / (double) community.getLimitParticipants()) * 100)
          .successPercent((Double.valueOf(certifiedProof) / community.getParticipantsList().size()) * 100)
          .dateStatus(getDateStatus(community))
          .secret(community.isPasswordFlag())
          .password(community.getPassword())
          .writer(userDetails != null && community.getMemberId().equals(userDetails.getId()))
          .build());
    }
    return communityList;
  }

  private Boolean participant(UserDetailsImpl userDetails, Community community) {
    return participantsRepository.existsByCommunityAndMemberId(community, userDetails.getId());
  }

  private Community createCommunity(CommunityRequestDto requestDto, MultipartFile multipartFile, Long loginUserId, String nickname) throws IOException {
    return Community.builder()
            .title(requestDto.getTitle())
            .content(requestDto.getContent())
            .img(returnImageUrl(multipartFile))
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
        .community(community)
        .memberId(loginUserId)
        .nickname(nickname)
        .build();
  }

  private Community findTheCommunityByMemberId(Long id) {
    return communityRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COMMUNITY));
  }

  private Long getCertifiedProof(Community community) {
    return proofRepository.getCertifiedProof(community);
  }

  private Boolean validateWriter(UserDetailsImpl userDetails, Community community) {
    if (userDetails!=null && community.getMemberId().equals(userDetails.getId())) {
      return true;
    } else throw new CustomException(ErrorCode.INCORRECT_USERID);
  }

  private String getDateStatus(Community community) throws ParseException {
    return dateUtil.dateStatus(community.getStartDate(), community.getEndDate());
  }

  private String returnImageUrl(MultipartFile multipartFile) throws IOException {
    if (multipartFile != null) {
      return s3Uploader.upload(multipartFile).getUploadImageUrl();
    }else return null;
  }
}
