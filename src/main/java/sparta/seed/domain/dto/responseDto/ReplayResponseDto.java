package sparta.seed.domain.dto.responseDto;

import lombok.Builder;
import lombok.Getter;
import sparta.seed.domain.Img;

import java.util.List;

@Getter
public class ReplayResponseDto {

	private Long replayId;
	private String title;
	private String content;
	private List<Img> img;
	private int commentCnt;
	private int heartCnt;
	private boolean isWriter;
	private boolean isHeart;

	@Builder
	public ReplayResponseDto(Long replayId, String title, String content, List<Img> img, int commentCnt, int heartCnt, boolean isWriter, boolean isHeart) {
		this.replayId = replayId;
		this.title = title;
		this.content = content;
		this.img = img;
		this.commentCnt = commentCnt;
		this.heartCnt = heartCnt;
		this.isWriter = isWriter;
		this.isHeart = isHeart;
	}
}
