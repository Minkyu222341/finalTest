package sparta.seed.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sparta.seed.community.domain.Community;
import sparta.seed.community.domain.dto.responsedto.CommunityMyJoinResponseDto;
import sparta.seed.community.repository.CommunityRepository;
import sparta.seed.exception.CustomException;
import sparta.seed.exception.ErrorCode;
import sparta.seed.jwt.TokenProvider;
import sparta.seed.login.domain.RefreshToken;
import sparta.seed.login.domain.dto.requestdto.RefreshTokenRequestDto;
import sparta.seed.login.domain.dto.responsedto.TokenResponseDto;
import sparta.seed.member.domain.Member;
import sparta.seed.member.domain.dto.responsedto.NicknameResponseDto;
import sparta.seed.member.domain.dto.responsedto.UserInfoResponseDto;
import sparta.seed.member.domain.dto.requestdto.NicknameRequestDto;
import sparta.seed.member.repository.MemberRepository;
import sparta.seed.member.repository.RefreshTokenRepository;
import sparta.seed.mission.domain.ClearMission;
import sparta.seed.mission.domain.dto.requestdto.MissionSearchCondition;
import sparta.seed.mission.domain.dto.responsedto.ClearMissionResponseDto;
import sparta.seed.mission.repository.ClearMissionRepository;
import sparta.seed.sercurity.UserDetailsImpl;
import sparta.seed.util.DateUtil;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
  private final MemberRepository memberRepository;
  private final CommunityRepository communityRepository;
  private final ClearMissionRepository clearMissionRepository;
  private final DateUtil dateUtil;
  private final TokenProvider tokenProvider;
  private final RefreshTokenRepository refreshTokenRepository;
  /**
   * 마이페이지
   */
  public ResponseEntity<UserInfoResponseDto> getMyPage(UserDetailsImpl userDetails) {
    Member member = memberRepository.findById(userDetails.getId())
        .orElseThrow(()-> new CustomException(ErrorCode.UNKNOWN_USER));

    return getUserInfo(member);
  }

  /**
   * 닉네임 중복체크
   */
  public ResponseEntity<Boolean> checkNickname(NicknameRequestDto requestDto) {
    if(memberRepository.existsByNickname(requestDto.getNickname())){
      return ResponseEntity.ok().body(false);
    }else return ResponseEntity.ok().body(true);
  }

  /**
   * 닉네임 변경
   */
  @Transactional
  public ResponseEntity<NicknameResponseDto> updateNickname(UserDetailsImpl userDetails, NicknameRequestDto requestDto) {
    Member member = memberRepository.findById(userDetails.getId())
        .orElseThrow(()-> new CustomException(ErrorCode.UNKNOWN_USER));
    if (!(member.getNickname().equals(requestDto.getNickname()) && memberRepository.existsByNickname(requestDto.getNickname()))) {
      member.updateNickname(requestDto);
      return ResponseEntity.badRequest().body(NicknameResponseDto.builder()
          .nickname(member.getNickname())
          .success(true)
          .build());
    }
    throw new CustomException(ErrorCode.ACCESS_DENIED);
  }

  /**
   * 그룹미션 확인
   */
  public ResponseEntity<List<CommunityMyJoinResponseDto>> showGroupMissionList(UserDetailsImpl userDetails) {
    try {
      List<Community> communityList = communityRepository.findByMemberId(userDetails.getId());
      List<CommunityMyJoinResponseDto> responseDtoList = new ArrayList<>();
      for (Community community : communityList) {
        responseDtoList.add(CommunityMyJoinResponseDto.builder()
            .communityId(community.getId())
            .title(community.getTitle())
            .img(community.getImg())
            .build());
      }
      return ResponseEntity.ok().body(responseDtoList);
    }catch (Exception e) {throw new CustomException(ErrorCode.UNKNOWN_USER);}
  }

  /**
   * 미션 통계 - 주간 , 월간
   */

  /**
   * 일일 미션 달성 현황 확인
   */
  public ResponseEntity<ClearMissionResponseDto> targetDayMission(String selectedDate, UserDetailsImpl userDetails) {
    try {
      List<ClearMission> clearMissionList = clearMissionRepository.findAllByMemberIdAndCreatedAt(userDetails.getId(), LocalDate.parse(selectedDate));
      return ResponseEntity.ok(ClearMissionResponseDto.builder()
          .selectedDate(selectedDate)
          .clearMissionList(clearMissionList)
          .clearMissionCnt(clearMissionList.size())
          .build());
    }catch (Exception e) {throw new CustomException(ErrorCode.UNKNOWN_USER);}
  }

  private String getDateStatus(Community community) throws ParseException {
    return dateUtil.dateStatus(community.getStartDate(), community.getEndDate());
  }

  /**
   * 유저정보 공개 / 비공개 설정
   */
  @Transactional
  public ResponseEntity<Boolean> isSceret(UserDetailsImpl userDetails) {
    Optional<Member> member = memberRepository.findById(userDetails.getId());
    if (!member.get().isSecret()) {
      member.get().updateIsSecret(true);
      return ResponseEntity.ok().body(true);
    }
    member.get().updateIsSecret(false);
    return ResponseEntity.ok().body(false);
  }

  /**
   * 다른유저 정보 확인
   */
  public ResponseEntity<UserInfoResponseDto> getUserinfo(Long memberId) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(()-> new CustomException(ErrorCode.UNKNOWN_USER));
    if(!member.isSecret()){
      return getUserInfo(member);

    }throw new CustomException(ErrorCode.CLOSED_USER);
  }

  /**
   * 리프레쉬토큰
   */
  @Transactional
  public ResponseEntity<String> reissue(RefreshTokenRequestDto tokenRequestDto) {
    if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
      throw new CustomException(ErrorCode.BE_NOT_VALID_TOKEN);
    }

    RefreshToken refreshToken = refreshTokenRepository.findByRefreshValue(tokenRequestDto.getRefreshToken())
            .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_MISMATCH));

    Member member = memberRepository.findById(Long.valueOf(refreshToken.getRefreshKey()))
            .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_MISMATCH));

    String accessToken = tokenProvider.generateAccessToken(String.valueOf(member.getId()),member.getNickname());

    return ResponseEntity.ok().body(accessToken);
  }

  public List<ClearMissionResponseDto> getDailyMissionStats(MissionSearchCondition condition, UserDetailsImpl userDetails) {
    Long memberId = userDetails.getId();

    return clearMissionRepository.dailyMissionStats(condition,memberId);
  }

  // 유저 정보 뽑기
  private ResponseEntity<UserInfoResponseDto> getUserInfo(Member member) {
    double clearMission = clearMissionRepository.countAllByMemberId(member.getId());
    double missionDiv = clearMission / 5;
    String stringDiv = missionDiv +"";
    String[] split = stringDiv.split("\\.");

    UserInfoResponseDto userInfoResponseDto = UserInfoResponseDto.builder()
        .id(member.getId())
        .nickname(member.getNickname())
        .username(member.getUsername())
        .profileImage(member.getProfileImage())
        .level((int) (missionDiv + 1))
        .totalClear((int) clearMission)
        .nextLevelExp(5 - (Integer.parseInt(split[1]) / 2))
        .isSecret(member.isSecret())
        .build();
    return ResponseEntity.ok().body(userInfoResponseDto);
  }
}