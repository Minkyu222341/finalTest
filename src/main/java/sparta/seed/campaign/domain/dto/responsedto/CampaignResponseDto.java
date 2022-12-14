package sparta.seed.campaign.domain.dto.responsedto;

import lombok.Builder;
import lombok.Getter;
import sparta.seed.img.domain.Img;

import java.util.List;

@Getter
public class CampaignResponseDto {
	private Long campaignId;
	private String title;
	private String thumbnail;
	private List<Img> imgList;

	@Builder
	public CampaignResponseDto(Long campaignId, String thumbnail, String title, List<Img> imgList) {
		this.campaignId = campaignId;
		this.title = title;
		this.thumbnail = thumbnail;
		this.imgList = imgList;
	}
}
