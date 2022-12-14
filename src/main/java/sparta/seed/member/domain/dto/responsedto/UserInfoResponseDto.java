package sparta.seed.member.domain.dto.responsedto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserInfoResponseDto {
	private Long id;
	private String nickname;
	private String username;
	private String profileImage;
	private int level;
	private int totalClear;
	private int nextLevelExp;
	private boolean isSecret;

	@Builder
	public UserInfoResponseDto(Long id, String nickname, String username, String profileImage, int level, int totalClear, int nextLevelExp, boolean isSecret) {
		this.id = id;
		this.nickname = nickname;
		this.username = username;
		this.profileImage = profileImage;
		this.level = level;
		this.totalClear = totalClear;
		this.nextLevelExp = nextLevelExp;
		this.isSecret = isSecret;
	}
}