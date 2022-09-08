package sparta.seed.community.domain.dto.responsedto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ProofHeartResponseDto {
	private Long proofId;
	private boolean heart;
	private int heartCnt;

	@Builder
	public ProofHeartResponseDto(Long proofId, boolean heart, int heartCnt) {
		this.proofId = proofId;
		this.heart = heart;
		this.heartCnt = heartCnt;
	}
}
