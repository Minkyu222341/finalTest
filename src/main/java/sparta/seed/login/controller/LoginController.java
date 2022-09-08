package sparta.seed.login.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sparta.seed.login.domain.dto.requestdto.RefreshTokenRequestDto;
import sparta.seed.login.service.GoogleUserService;
import sparta.seed.login.service.KakaoUserService;
import sparta.seed.login.service.NaverUserService;
import sparta.seed.member.domain.dto.responsedto.MemberResponseDto;
import sparta.seed.member.domain.dto.responsedto.TokenResponseDto;
import sparta.seed.member.service.MemberService;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class LoginController {
  private final GoogleUserService googleUserService;
  private final KakaoUserService kakaoUserService;
  private final NaverUserService naverUserService;
  private final MemberService memberService;

  /**
   * 카카오 로그인
   */
  @GetMapping("/user/kakao/callback")
  public TokenResponseDto kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
    return kakaoUserService.kakaoLogin(code, response);
  }

  /**
   * 구글 로그인
   */
  @GetMapping("/user/google/callback")
  public TokenResponseDto googleLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
    return googleUserService.googleLogin(code, response);
  }

  /**
   * 네이버 로그인
   */
  @GetMapping("/user/naver/callback")
  public TokenResponseDto naverLogin(@RequestParam String code, @RequestParam String state, HttpServletResponse response) throws JsonProcessingException {
    return naverUserService.naverLogin(code, state, response);
  }
  /**
   * 리프레쉬토큰
   */

  @PostMapping("/reissue")  //재발급을 위한 로직
  public ResponseEntity<String> reissue(@RequestBody RefreshTokenRequestDto requestDto) {
    return memberService.reissue(requestDto);
  }

}
