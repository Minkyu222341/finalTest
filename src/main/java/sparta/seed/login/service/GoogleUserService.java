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
import sparta.seed.member.repository.MemberRepository;
import sparta.seed.member.repository.RefreshTokenRepository;
import sparta.seed.sercurity.UserDetailsImpl;

import javax.servlet.http.HttpServletResponse;
import java.util.Random;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class GoogleUserService {

  @Value("${spring.security.oauth2.client.registration.google.client-id}")
  String googleClientId;
  @Value("${spring.security.oauth2.client.registration.google.client-secret}")
  String googleClientSecret;
  @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
  String googleRedirectUri;

  private final TokenProvider tokenProvider;
  private final MemberRepository memberRepository;
  private final RefreshTokenRepository refreshTokenRepository;

  public MemberResponseDto googleLogin(String code, HttpServletResponse response) throws JsonProcessingException {

    String accessToken = getAccessToken(code);
    SocialMemberRequestDto googleUserInfo = getGoogleUserInfo(accessToken);
    Member foundUser = getUser(googleUserInfo);
    Authentication authentication = securityLogin(foundUser);

    return jwtToken(authentication, response);
  }

  private String getAccessToken(String code) throws JsonProcessingException {

    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("client_id", googleClientId);
    body.add("client_secret", googleClientSecret);
    body.add("code", code);
    body.add("redirect_uri", googleRedirectUri);
    body.add("grant_type", "authorization_code");

    HttpEntity<MultiValueMap<String, String>> googleToken = new HttpEntity<>(body, headers);
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> response = restTemplate.exchange(
            "https://oauth2.googleapis.com/token",
            HttpMethod.POST,
            googleToken,
            String.class
    );

    String responseBody = response.getBody();
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode responseToken = objectMapper.readTree(responseBody);
    return responseToken.get("access_token").asText();
  }

  private SocialMemberRequestDto getGoogleUserInfo(String accessToken) throws JsonProcessingException {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + accessToken);
    headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

    HttpEntity<MultiValueMap<String, String>> googleUser = new HttpEntity<>(headers);
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> response = restTemplate.exchange(
            "https://openidconnect.googleapis.com/v1/userinfo",
            HttpMethod.POST, googleUser,
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

    String socialId = jsonNode.get("sub").asText();
    String userEmail = jsonNode.get("email").asText();
    String nickname = "G" + "_" + rdNick;

    String profileImage = jsonNode.get("picture").asText();
    String defaultImage = "https://hanghae99-8d-tm.s3.ap-northeast-2.amazonaws.com/defaultImage.png";
    if (profileImage == null) {
      profileImage = defaultImage;
    }
    return SocialMemberRequestDto.builder()
            .socialId(socialId)
            .username(userEmail)
            .nickname(nickname)
            .profileImage(profileImage)
            .build();
  }

  private Member getUser(SocialMemberRequestDto requestDto) {
    String googleSocialID = requestDto.getSocialId();
    Member googleUser = memberRepository.findBySocialId(googleSocialID).orElse(null);

    if (googleUser == null) {
      String username = requestDto.getUsername();
      String nickname = requestDto.getNickname();
      String socialId = requestDto.getSocialId();
      String password = UUID.randomUUID().toString();
      String profileImage = requestDto.getProfileImage();
      Member signUpMember = Member.builder()
              .username(username)
              .nickname(nickname)
              .password(password)
              .profileImage(profileImage)
              .socialId(socialId)
              .authority(Authority.ROLE_USER)
              .build();
      memberRepository.save(signUpMember);
      return signUpMember;
    }
    return googleUser;
  }

  private Authentication securityLogin(Member findUser) {
    UserDetails userDetails = new UserDetailsImpl(findUser);
    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authentication);
    return authentication;
  }

  private MemberResponseDto jwtToken(Authentication authentication, HttpServletResponse response) {
    UserDetailsImpl member = ((UserDetailsImpl) authentication.getPrincipal());
    String accessToken = tokenProvider.generateAccessToken(String.valueOf(member.getId()));
    String refreshToken = tokenProvider.generateRefreshToken(String.valueOf(member.getId()));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    response.addHeader("Authorization", "Bearer " + accessToken);

    RefreshToken saveRefreshToken = RefreshToken.builder()
            .refreshKey(String.valueOf(member.getId()))
            .refreshValue(refreshToken)
            .build();
    refreshTokenRepository.save(saveRefreshToken);

    return MemberResponseDto.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
  }

}
