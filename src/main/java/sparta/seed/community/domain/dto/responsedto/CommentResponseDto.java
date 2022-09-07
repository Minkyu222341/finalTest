package sparta.seed.community.domain.dto.responsedto;

import lombok.Builder;
import lombok.Getter;
import sparta.seed.img.domain.Img;

@Getter
public class CommentResponseDto {
	private Long commentId;
	private Long proofId;
	private String nickname;
	private String content;
	private Img img;
	private boolean writer;


	@Builder
	public CommentResponseDto(Long commentId, Long proofId, String nickname, String content, Img img, Boolean writer) {
		this.commentId = commentId;
		this.proofId = proofId;
		this.nickname = nickname;
		this.content = content;
		this.img = img;
		this.writer = writer;
	}
}
