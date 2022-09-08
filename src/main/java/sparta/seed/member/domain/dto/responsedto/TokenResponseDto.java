package sparta.seed.member.domain.dto.responsedto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenResponseDto {
	private String accessToken;
	private String refreshToken;
	private Long accessTokenExpiresIn;
	private String username;

	@Builder
	public TokenResponseDto(String accessToken, String refreshToken, Long accessTokenExpiresIn, String username) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.accessTokenExpiresIn = accessTokenExpiresIn;
		this.username = username;
	}
}
