package sparta.seed.community.domain.dto.requestdto;

import lombok.Getter;

@Getter
public class CommentRequestDto {
	private String content;
	private boolean delete;
}
