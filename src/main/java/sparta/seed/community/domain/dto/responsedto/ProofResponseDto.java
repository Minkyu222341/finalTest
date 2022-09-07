package sparta.seed.community.domain.dto.responsedto;

import lombok.Builder;
import lombok.Getter;
import sparta.seed.img.domain.Img;

import java.util.List;

@Getter
public class ProofResponseDto {

	private Long proofId;
	private String title;
	private String content;
	private List<Img> img;
	private int commentCnt;
	private int heartCnt;
	private boolean isWriter;
	private boolean isHeart;

	@Builder
	public ProofResponseDto(Long proofId, String title, String content, List<Img> img, int commentCnt, int heartCnt, boolean isWriter, boolean isHeart) {
		this.proofId = proofId;
		this.title = title;
		this.content = content;
		this.img = img;
		this.commentCnt = commentCnt;
		this.heartCnt = heartCnt;
		this.isWriter = isWriter;
		this.isHeart = isHeart;
	}
}
