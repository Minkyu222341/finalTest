package sparta.seed.member.domain.dto.responsedto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class NicknameResponseDto {
	private String nickname;
	private boolean success;

	@Builder
	public NicknameResponseDto(String nickname, boolean success) {
		this.nickname = nickname;
		this.success = success;
	}
}
