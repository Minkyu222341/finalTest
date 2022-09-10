package sparta.seed.community.domain.dto.responsedto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CommunityMyJoinResponseDto {
	private Long communityId;
	private String title;
	private String img;

	@Builder
	public CommunityMyJoinResponseDto(Long communityId, String title, String img) {
		this.communityId = communityId;
		this.title = title;
		this.img = img;
	}
}
