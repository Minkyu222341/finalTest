package sparta.seed.community.domain.dto.responsedto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class CommentResponseListDto {
	private Long proofId;
	private List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

	public CommentResponseListDto(Long proofId) {
		this.proofId = proofId;
	}
	public void addCommentResponseDto(CommentResponseDto commentResponseDto){
		this.commentResponseDtoList.add(commentResponseDto);
	}
}
