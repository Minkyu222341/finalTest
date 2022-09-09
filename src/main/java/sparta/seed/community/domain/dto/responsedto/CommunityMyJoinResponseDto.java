package sparta.seed.community.domain.dto.responsedto;

import lombok.Builder;
import lombok.Getter;
import sparta.seed.img.domain.Img;

@Getter
public class CommunityMyJoinResponseDto {
	private Long communityId;
	private String title;
	private Img img;

	@Builder
	public CommunityMyJoinResponseDto(Long communityId, String title, Img img) {
		this.communityId = communityId;
		this.title = title;
		this.img = img;
	}
}
