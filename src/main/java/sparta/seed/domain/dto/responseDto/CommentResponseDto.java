package sparta.seed.domain.dto.responseDto;

import lombok.Builder;
import lombok.Getter;
import sparta.seed.domain.Img;

@Getter
public class CommentResponseDto {
	private Long commentId;
	private Long replayId;
	private String nickname;
	private String content;
	private Img img;
	private boolean isWriter;


	@Builder
	public CommentResponseDto(Long commentId, Long replayId, String nickname, String content, Img img, Boolean isWriter) {
		this.commentId = commentId;
		this.replayId = replayId;
		this.nickname = nickname;
		this.content = content;
		this.img = img;
		this.isWriter = isWriter;
	}
}
