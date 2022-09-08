package sparta.seed.login.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import sparta.seed.jwt.TokenProvider;
import sparta.seed.login.domain.RefreshToken;
import sparta.seed.login.domain.dto.requestdto.SocialMemberRequestDto;
import sparta.seed.member.domain.Authority;
import sparta.seed.member.domain.Member;
import sparta.seed.member.domain.dto.responsedto.MemberResponseDto;
import sparta.seed.member.domain.dto.responsedto.TokenResponseDto;
import sparta.seed.member.repository.MemberRepository;
import sparta.seed.member.repository.RefreshTokenRepository;
import sparta.seed.sercurity.UserDetailsImpl;

import javax.servlet.http.HttpServletResponse;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoUserService extends DefaultOAuth2UserService {
  private final PasswordEncoder passwordEncoder;
  private final TokenProvider tokenProvider;
  private final MemberRepository memberRepository;
  private final RefreshTokenRepository refreshTokenRepository;




  public TokenResponseDto kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {

    String accessToken = getAccessToken(code);

    SocialMemberRequestDto kakaoUserInfo = getKakaoUserInfo(accessToken);

    Member kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);

    return forceLogin(kakaoUser,response);
  }

  private String getAccessToken(String code) throws JsonProcessingException {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("grant_type", "authorization_code");
    body.add("client_id", "f072c106f2f26c3921bee727b2df0ccd");
    body.add("redirect_uri", "http://localhost:8080/user/kakao/callback");
//    body.add("redirect_uri", "http://localhost:3000/user/kakao/callback");
    body.add("code", code);

    HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
    RestTemplate rt = new RestTemplate();
    ResponseEntity<String> response = rt.exchange(
            "https://kauth.kakao.com/oauth/token",
            HttpMethod.POST,
            kakaoTokenRequest,
            String.class
    );

    String responseBody = response.getBody();
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(responseBody);
    return jsonNode.get("access_token").asText();
  }

  private SocialMemberRequestDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + accessToken);
    headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
    HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
    RestTemplate rt = new RestTemplate();
    ResponseEntity<String> response = rt.exchange(
            "https://kapi.kakao.com/v2/user/me",
            HttpMethod.POST,
            kakaoUserInfoRequest,
            String.class
    );
    String responseBody = response.getBody();
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(responseBody);

    Random rnd = new Random();
    String rdNick="";
    for (int i = 0; i < 8; i++) {
      rdNick += String.valueOf(rnd.nextInt(10));
    }

    String id = jsonNode.get("id").toString();
    String nickname = "K" + "_" + rdNick;
    String username = jsonNode.get("kakao_account").get("email").asText();
    String profileImage = jsonNode.get("kakao_account").get("profile").get("profile_image_url").asText();

    System.out.println("jsonNode = " + jsonNode);

    return SocialMemberRequestDto.builder()
            .socialId(id)
            .username(username)
            .nickname(nickname)
            .profileImage(profileImage)
            .build();
  }

  private Member registerKakaoUserIfNeeded(SocialMemberRequestDto kakaoUserInfo) {
    String socialId = kakaoUserInfo.getSocialId();
    Member member = memberRepository.findBySocialId(socialId).orElse(null);
    if (member==null) {
      // 회원가입
      String username = kakaoUserInfo.getUsername();
      String nickname = kakaoUserInfo.getNickname();
      String password = passwordEncoder.encode(UUID.randomUUID().toString());
      String profileImage = kakaoUserInfo.getProfileImage();

      Member signUp = Member.builder()
              .socialId(socialId)
              .username(username)
              .nickname(nickname)
              .password(password)
              .profileImage(profileImage)
              .authority(Authority.ROLE_USER)
              .build();
      return memberRepository.save(signUp);
    }
    return member;
  }

  private TokenResponseDto forceLogin(Member kakaoUser,HttpServletResponse response) {
    UserDetailsImpl member = new UserDetailsImpl(kakaoUser);
    String accessToken = tokenProvider.generateAccessToken(String.valueOf(member.getId()),member.getNickname());
    String refreshToken = tokenProvider.generateRefreshToken(String.valueOf(member.getId()));
    Authentication authentication = new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authentication);
    response.setHeader("Authorization", "Bearer " + accessToken);

    RefreshToken saveRefreshToken = RefreshToken.builder()
            .refreshKey(String.valueOf(member.getId()))
            .refreshValue(refreshToken)
            .build();
    refreshTokenRepository.save(saveRefreshToken);

    return TokenResponseDto.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
  }
}