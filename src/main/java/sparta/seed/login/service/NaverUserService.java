package sparta.seed.login.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

@Slf4j
@RequiredArgsConstructor
@Service
public class NaverUserService {

  @Value("${spring.security.oauth2.client.registration.naver.client-id}")
  String naverClientId;
  @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
  String naverClientSecret;

  private final BCryptPasswordEncoder passwordEncoder;
  private final TokenProvider tokenProvider;
  private final MemberRepository memberRepository;
  private final RefreshTokenRepository refreshTokenRepository;

  // 네이버 로그인
  public TokenResponseDto naverLogin(String code, String state, HttpServletResponse response) throws JsonProcessingException {

    String accessToken = getAccessToken(code, state);

    SocialMemberRequestDto naverUserInfo = getNaverUserInfo(accessToken);

    Member naverUser = getUser(naverUserInfo);

    Authentication authentication = securityLogin(naverUser);

    return jwtToken(authentication, response);
  }

  private String getAccessToken(String code, String state) throws JsonProcessingException {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("grant_type", "authorization_code");
    body.add("client_id", naverClientId);
    body.add("client_secret", naverClientSecret);
    body.add("code", code);
    body.add("state", state);

    HttpEntity<MultiValueMap<String, String>> naverToken = new HttpEntity<>(body, headers);
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> response = restTemplate.exchange(
            "https://nid.naver.com/oauth2.0/token",
            HttpMethod.POST,
            naverToken,
            String.class
    );

    String responseBody = response.getBody();
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode responseToken = objectMapper.readTree(responseBody);
    return responseToken.get("access_token").asText();
  }

  private SocialMemberRequestDto getNaverUserInfo(String accessToken) throws JsonProcessingException {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + accessToken);
    headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

    HttpEntity<MultiValueMap<String, String>> naverUser = new HttpEntity<>(headers);
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> response = restTemplate.exchange(
            "https://openapi.naver.com/v1/nid/me",
            HttpMethod.POST, naverUser,
            String.class
    );

    String responseBody = response.getBody();
    ObjectMapper objectMapper = new ObjectMapper();

    JsonNode jsonNode = objectMapper.readTree(responseBody);

    Random rnd = new Random();
    String rdNick = "";
    for (int i = 0; i < 8; i++) {
      rdNick += String.valueOf(rnd.nextInt(10));
    }

    String socialId = String.valueOf(jsonNode.get("response").get("id").asText());
    String username = jsonNode.get("response").get("email").asText();
    String nickname = "N" + "_" + rdNick;


    String profileImage = jsonNode.get("response").get("profile_image").asText();
    String defaultImage = "https://mytest-coffick.s3.ap-northeast-2.amazonaws.com/coffindBasicImage.png";
    if (profileImage == null)
      profileImage = defaultImage;

    return SocialMemberRequestDto.builder()
            .socialId(socialId)
            .username(username)
            .nickname(nickname)
            .profileImage(profileImage)
            .build();
  }

  private Member getUser(SocialMemberRequestDto naverUserInfo) {
    // 다른 소셜로그인이랑 이메일이 겹쳐서 잘못 로그인 될까봐. 다른 사용자인줄 알고 로그인이 된다. 그래서 소셜아이디로 구분해보자
    String naverSocialID = naverUserInfo.getSocialId();
    Member naverUser = memberRepository.findBySocialId(naverSocialID).orElse(null);

    if (naverUser == null) {  // 회원가입
      String username = naverUserInfo.getUsername();
      String socialId = naverUserInfo.getSocialId();
      String nickname = naverUserInfo.getNickname();
      String password = passwordEncoder.encode(UUID.randomUUID().toString()); // 비밀번호 암호화
      String profileImage = naverUserInfo.getProfileImage();

      Member signUpMember = Member.builder()
              .socialId(socialId)
              .username(username)
              .password(password)
              .profileImage(profileImage)
              .nickname(nickname)
              .authority(Authority.ROLE_USER)
              .build();

      memberRepository.save(signUpMember);
      return signUpMember;
    }

    return naverUser;
  }

  private Authentication securityLogin(Member foundUser) {
    UserDetails userDetails = new UserDetailsImpl(foundUser);
    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authentication);
    return authentication;
  }

  private TokenResponseDto jwtToken(Authentication authentication, HttpServletResponse response) {
    UserDetailsImpl member = ((UserDetailsImpl) authentication.getPrincipal());
    String accessToken = tokenProvider.generateAccessToken(String.valueOf(member.getId()),member.getNickname());
    String refreshToken = tokenProvider.generateRefreshToken(String.valueOf(member.getId()));
    SecurityContextHolder.getContext().setAuthentication(authentication);

    response.addHeader("Authorization", "Bearer " + accessToken);
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