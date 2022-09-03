package sparta.seed.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sparta.seed.domain.dto.requestDto.SocialMemberRequestDto;
import sparta.seed.domain.dto.responseDto.CommunityResponseDto;
import sparta.seed.domain.dto.responseDto.MemberResponseDto;
import sparta.seed.sercurity.UserDetailsImpl;
import sparta.seed.service.MemberService;

import java.text.ParseException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;

  /**
   * 유저 정보보기
   */
//  @GetMapping("/api/userinfo/{memberId}")
//  public ResponseEntity<MemberResponseDto> getMemberInfo(@PathVariable Long memberId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
//    return memberService.getMemberInfo(memberId,userDetails);
//  }
  /**
   * 마이페이지
   */
  @GetMapping("/api/mypage")
  public ResponseEntity<MemberResponseDto> getMyPage(@AuthenticationPrincipal UserDetailsImpl userDetails) {

    return memberService.getMyPage(userDetails);
  }

  /**
   * 닉네임 변경
   */
  @PatchMapping("/api/mypage/nickname")
  public ResponseEntity<Boolean> updateNickname(@AuthenticationPrincipal UserDetailsImpl userDetails,@RequestBody SocialMemberRequestDto requestDto) {
    return memberService.updateNickname(userDetails,requestDto);
  }

  /**
   * 그룹미션 확인
   */
  @GetMapping("/api/mypage/groupmission")
  public ResponseEntity<List<CommunityResponseDto>> showGroupMissionList(@AuthenticationPrincipal UserDetailsImpl userDetails) throws ParseException {
    return memberService.showGroupMissionList(userDetails);
  }

  /**
   * 미션 통계 - 주간 , 월간
   */

  /**
   * 일일 미션 달성 현황 확인
   */

  /**
   * 유저정보 비공개 / 공개 설정
   */
  @PatchMapping("/api/mypage/status")
  public ResponseEntity<Boolean> isSceret(@AuthenticationPrincipal UserDetailsImpl userDetails) {
    return memberService.isSceret(userDetails);
  }
}
