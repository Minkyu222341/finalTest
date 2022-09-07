package sparta.seed.community.domain.dto.requestdto;

import lombok.Getter;

@Getter
public class ProofRequestDto {
	private String title;
	private String content;
	private Long[] imgIdList;
}
