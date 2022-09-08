package sparta.seed.community.domain.dto.responsedto;

import lombok.Builder;
import lombok.Getter;
import sparta.seed.img.domain.Img;

import java.time.LocalDate;

@Getter
public class CommentResponseDto {
	private Long commentId;
	private Long proofId;
	private LocalDate creatAt;
	private String nickname;
	private String content;
	private Img img;
	private boolean writer;


	@Builder
	public CommentResponseDto(Long commentId, Long proofId, LocalDate creatAt, String nickname, String content, Img img, Boolean writer) {
		this.commentId = commentId;
		this.proofId = proofId;
		this.creatAt = creatAt;
		this.nickname = nickname;
		this.content = content;
		this.img = img;
		this.writer = writer;
	}
}