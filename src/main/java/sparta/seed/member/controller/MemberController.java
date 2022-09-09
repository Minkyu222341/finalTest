package sparta.seed.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sparta.seed.community.domain.dto.responsedto.CommunityMyJoinResponseDto;
import sparta.seed.member.domain.dto.responsedto.UserInfoResponseDto;
import sparta.seed.member.domain.dto.requestdto.NicknameRequestDto;
import sparta.seed.member.service.MemberService;
import sparta.seed.mission.domain.dto.requestdto.MissionSearchCondition;
import sparta.seed.mission.domain.dto.responsedto.ClearMissionResponseDto;
import sparta.seed.sercurity.UserDetailsImpl;

import java.text.ParseException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;

  /**
   * 유저 정보보기
   */
  @GetMapping("/api/userinfo/{memberId}")
  public ResponseEntity<UserInfoResponseDto> getUserinfo(@PathVariable Long memberId) {

    return memberService.getUserinfo(memberId);
  }

  /**
   * 마이페이지
   */
  @GetMapping("/api/mypage")
  public ResponseEntity<UserInfoResponseDto> getMyPage(@AuthenticationPrincipal UserDetailsImpl userDetails) {

    return memberService.getMyPage(userDetails);
  }

  /**
   * 닉네임 중복체크
   */
  @PostMapping("/api/mypage/nickname")
  public ResponseEntity<Boolean> checkNickname(@RequestBody NicknameRequestDto requestDto) {
    return memberService.checkNickname(requestDto);
  }

  /**
   * 닉네임 변경
   */
  @PatchMapping("/api/mypage/nickname")
  public ResponseEntity<Boolean> updateNickname(@AuthenticationPrincipal UserDetailsImpl userDetails,@RequestBody NicknameRequestDto requestDto) {
    return memberService.updateNickname(userDetails,requestDto);
  }

  /**
   * 그룹미션 확인
   */
  @GetMapping("/api/mypage/groupmission")
  public ResponseEntity<List<CommunityMyJoinResponseDto>> showGroupMissionList(@AuthenticationPrincipal UserDetailsImpl userDetails) throws ParseException {
    return memberService.showGroupMissionList(userDetails);
  }

  /**
   * 미션 통계 - 주간 , 월간
   */
  @GetMapping("/api/mypage/stats")
  public List<ClearMissionResponseDto> getDailyMissionStats(MissionSearchCondition condition, @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return memberService.getDailyMissionStats(condition,userDetails);
  }

  /**
   * 일일 미션 달성 현황 확인
   */
  @GetMapping("/api/mypage/statistics")
  public ResponseEntity<ClearMissionResponseDto> targetDayMission(@RequestParam String targetDay, @AuthenticationPrincipal UserDetailsImpl userDetails){
    return memberService.targetDayMission(targetDay, userDetails);
  }

  /**
   * 유저정보 비공개 / 공개 설정
   */
  @PatchMapping("/api/mypage/status")
  public ResponseEntity<Boolean> isSecret(@AuthenticationPrincipal UserDetailsImpl userDetails) {
    return memberService.isSceret(userDetails);
  }
}
