package sparta.seed.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sparta.seed.domain.dto.responseDto.MemberResponseDto;
import sparta.seed.service.GoogleUserService;
import sparta.seed.service.KakaoUserService;
import sparta.seed.service.NaverUserService;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "*")
public class LoginController {
  private final GoogleUserService googleUserService;
  private final KakaoUserService kakaoUserService;
  private final NaverUserService naverUserService;

  /**
   * 카카오 로그인
   */
  @GetMapping("/user/kakao/callback")
  public MemberResponseDto kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
    return kakaoUserService.kakaoLogin(code, response);
  }

  /**
   * 구글 로그인
   */
  @GetMapping("/user/google/callback")
  public MemberResponseDto googleLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
    return googleUserService.googleLogin(code, response);
  }

  /**
   * 네이버 로그인
   */
  @GetMapping("/user/naver/callback")
  public MemberResponseDto naverLogin(@RequestParam String code, @RequestParam String state, HttpServletResponse response) throws JsonProcessingException {
    return naverUserService.naverLogin(code, state, response);
  }

}
