package sparta.seed.domain.dto.requestDto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefreshTokenRequestDto {
  private String grantType;
  private String accessToken;
  private String refreshToken;
  private Long accessTokenExpiresIn;
}
