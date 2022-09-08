package sparta.seed.member.domain.requestdto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class NicknameRequestDto {
	private String nickname;

	@Builder
	public NicknameRequestDto(String nickname) {
		this.nickname = nickname;
	}
}
