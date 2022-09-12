package sparta.seed.login.domain.dto.requestdto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefreshTokenRequestDto {
  private String refreshToken;
  private Long accessTokenExpiresIn;
}
