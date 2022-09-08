package sparta.seed.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sparta.seed.community.domain.Community;
import sparta.seed.community.domain.dto.responsedto.CommunityResponseDto;
import sparta.seed.community.repository.CommunityRepository;
import sparta.seed.jwt.TokenProvider;
import sparta.seed.login.domain.RefreshToken;
import sparta.seed.login.domain.dto.requestdto.RefreshTokenRequestDto;
import sparta.seed.login.domain.dto.requestdto.SocialMemberRequestDto;
import sparta.seed.member.domain.Member;
import sparta.seed.member.domain.dto.responsedto.MemberResponseDto;
import sparta.seed.member.domain.dto.responsedto.UserInfoResponseDto;
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
        .orElseThrow(()-> new IllegalArgumentException("알 수 없는 사용자입니다."));

    return getUserInfo(member);
  }

  /**
   * 닉네임 변경
   */
  @Transactional
  public ResponseEntity<Boolean> updateNickname(UserDetailsImpl userDetails, SocialMemberRequestDto requestDto) {
    Member member = memberRepository.findById(userDetails.getId())
        .orElseThrow(()-> new IllegalArgumentException("알 수 없는 사용자입니다."));
    if (member.getNickname().equals(requestDto.getNickname())) {
      return ResponseEntity.badRequest().body(false);
    }
    member.updateNickname(requestDto);
    return ResponseEntity.ok().body(true);
  }

  /**
   * 그룹미션 확인
   */
  public ResponseEntity<List<CommunityResponseDto>> showGroupMissionList(UserDetailsImpl userDetails) throws ParseException {
    try {
      List<Community> communityList = communityRepository.findByMemberId(userDetails.getId());
      List<CommunityResponseDto> responseDtoList = new ArrayList<>();
      for (Community community : communityList) {
        responseDtoList.add(CommunityResponseDto.builder()
            .communityId(community.getId())
            .createAt(String.valueOf(community.getCreatedAt()))
            .title(community.getTitle())
            .successPercent(community.getProofList().size() / community.getLimitScore() * 100) // 인증글 갯수에 비례한 달성도
            .writer(community.getMemberId().equals(userDetails.getId())) // 내가 이 모임글의 작성자인지
            .dateStatus(getDateStatus(community)) // 모임이 시작전인지 시작했는지 종료되었는지
            .build());
      }
      return ResponseEntity.ok().body(responseDtoList);
    }catch (Exception e) {throw new IllegalArgumentException("알 수 없는 사용자입니다.");}
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
    }catch (Exception e) {throw new IllegalArgumentException("알 수 없는 사용자입니다.");}
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
        .orElseThrow(()-> new IllegalArgumentException("알 수 없는 사용자입니다."));
    if(!member.isSecret()){
      return getUserInfo(member);

    }throw new IllegalArgumentException("비공개 처리된 유저입니다.");
  }

  /**
   * 리프레쉬토큰
   */
  @Transactional
  public ResponseEntity<MemberResponseDto> reissue(RefreshTokenRequestDto tokenRequestDto,UserDetailsImpl userDetails) {
    // 1. Refresh Token 검증
    if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
      throw new RuntimeException("Refresh Token 이 유효하지 않습니다.");
    }

    // 2. Access Token 에서 Member ID 가져오기
    Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

    // 3. 저장소에서 Member ID 를 기반으로 Refresh Token 값 가져옴
    RefreshToken refreshToken = refreshTokenRepository.findByRefreshKey(authentication.getName())
            .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));

    // 4. Refresh Token 일치하는지 검사
    if (!refreshToken.getRefreshValue().equals(tokenRequestDto.getRefreshToken())) {
      throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
    }

    // 5. 새로운 토큰 생성
    MemberResponseDto memberResponseDto = tokenProvider.generateTokenDto(authentication,userDetails);

    // 6. 저장소 정보 업데이트
    RefreshToken newRefreshToken = refreshToken.updateValue(memberResponseDto.getRefreshToken());
    refreshTokenRepository.save(newRefreshToken);

    try {
      Member member = userDetails.getMember();

      // 토큰 발급
      return ResponseEntity.ok().body(MemberResponseDto.builder()
          .id(member.getId())
          .username(member.getUsername())
          .nickname(member.getNickname())
          .accessToken(memberResponseDto.getAccessToken())
          .accessTokenExpiresIn(memberResponseDto.getAccessTokenExpiresIn())
          .grantType(memberResponseDto.getGrantType())
          .refreshToken(memberResponseDto.getRefreshToken()).build());
    }catch (Exception e) {throw new IllegalArgumentException("알 수 없는 사용자입니다.");}
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
//          .isFriend()
        .build();
    return ResponseEntity.ok().body(userInfoResponseDto);
  }
}