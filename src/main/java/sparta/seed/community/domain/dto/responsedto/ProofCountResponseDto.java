package sparta.seed.community.domain.dto.responsedto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ProofCountResponseDto {
	private Long proofId;
	private int commentCnt;
	private int heartCnt;

	@Builder
	public ProofCountResponseDto(Long proofId, int commentCnt, int heartCnt) {
		this.proofId = proofId;
		this.commentCnt = commentCnt;
		this.heartCnt = heartCnt;
	}
}
