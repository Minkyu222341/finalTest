package sparta.seed.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import sparta.seed.member.domain.Authority;
import sparta.seed.member.domain.Member;
import sparta.seed.login.domain.dto.responsedto.TokenResponseDto;
import sparta.seed.sercurity.UserDetailsImpl;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider {

  private static final String AUTHORITIES_KEY = "auth";
  private static final String BEARER_TYPE = "bearer";
  private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30 * 24;            // 30분
  private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;  // 7일
  private static final String MEMBER_USERNAME = "memberUsername";
  private static final String MEMBER_NICKNAME = "memberNickname";
  private static final String MEMBER_ID = "memberId";
  private static final String MEMBER = "member";
  private Authority authority;


  private final Key key;

  public TokenProvider(@Value("${jwt.secret}") String secretKey) {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    this.key = Keys.hmacShaKeyFor(keyBytes);
  }

  public TokenResponseDto generateTokenDto(Authentication authentication, UserDetailsImpl member) {
    String authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

    long now = (new Date()).getTime();

    Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
    String accessToken = Jwts.builder()
            .setSubject(String.valueOf(member.getId()))
            .claim(AUTHORITIES_KEY, authorities)
            .claim(MEMBER_USERNAME, member.getUsername())
            .claim(MEMBER_NICKNAME, member.getNickname())
            .setExpiration(accessTokenExpiresIn)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();

    String refreshToken = Jwts.builder()
            .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
            .signWith(key, SignatureAlgorithm.HS512)
            .setHeaderParam("JWT_HEADER_PARAM_TYPE", "headerType")
            .compact();

    return TokenResponseDto.builder()
            .accessToken(accessToken)
            .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
            .refreshToken(refreshToken)
            .username(authentication.getName())
            .build();
  }

  public String generateAccessToken(String memberId,String memberNickname) {
    long now = (new Date()).getTime();
    Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
    String accessToken = Jwts.builder()
            .setSubject(String.valueOf(memberId))
            .claim(MEMBER_NICKNAME,memberNickname)
            .setExpiration(accessTokenExpiresIn)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();
    return accessToken;
  }

  public String generateRefreshToken(String memberId) {
    long now = (new Date()).getTime();
    String refreshToken = Jwts.builder()
            .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
            .claim(MEMBER_ID,memberId)
            .signWith(key, SignatureAlgorithm.HS512)
            .setHeaderParam("JWT_HEADER_PARAM_TYPE", "headerType")
            .compact();
    return refreshToken;
  }

  public Authentication getAuthentication(String accessToken) {

    Claims claims = parseClaims(accessToken);
    if (Authority.ROLE_USER.toString().equals(claims.get(AUTHORITIES_KEY))) {
      authority = Authority.ROLE_USER;
    } else {
      authority = Authority.ROLE_ADMIN;
    }

    Member member = Member.builder()
            .username((String) claims.get(MEMBER_USERNAME))
            .nickname((String) claims.get(MEMBER_NICKNAME))
            .authority(authority)
            .id(Long.valueOf(claims.getSubject()))
            .build();

    UserDetails principal = new UserDetailsImpl(member);
    return new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
  }


  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
      throw new MalformedJwtException("잘못된 JWT 서명입니다.");
    } catch (UnsupportedJwtException e) {
      throw new UnsupportedJwtException("지원되지 않는 JWT 토큰입니다.");
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("JWT 토큰이 잘못되었습니다.");
    } catch (ExpiredJwtException e) {
      throw new ExpiredJwtException(Jwts.header(), Jwts.claims(), "만료된 토큰입니다");
    }
  }

  private Claims parseClaims(String accessToken) {
    try {
      return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
    } catch (ExpiredJwtException e) {
      return e.getClaims();
    }
  }
}