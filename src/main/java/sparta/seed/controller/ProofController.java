package sparta.seed.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sparta.seed.domain.dto.requestDto.ProofRequestDto;
import sparta.seed.domain.dto.responseDto.ProofResponseDto;
import sparta.seed.sercurity.UserDetailsImpl;
import sparta.seed.service.ProofService;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProofController {
	private final ProofService proofService;

  /**
   * 글에 달린 인증글 전체 조회
   */
  @GetMapping("/api/community/{communityId}/proof")
	public List<ProofResponseDto> getAllReplay(@PathVariable Long communityId,
                                             @RequestParam("page") int page,
                                             @RequestParam("size") int size,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails){

	  page = page-1;
			return proofService.getAllReplay(communityId, page, size, userDetails);
	}

	/**
	 * 글에 달린 인증글 상세 조회
	 */
	@GetMapping("/api/proof/{proofId}")
	public ProofResponseDto getReplay(@PathVariable Long proofId, @AuthenticationPrincipal UserDetailsImpl userDetails){
		return proofService.getReplay(proofId, userDetails);
	}

  /**
   * 인증글 작성
   */
	@PostMapping(value = "/api/community/{communityId}/proof", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	public ProofResponseDto createReplay(@PathVariable Long communityId,
	                                     @Valid @RequestPart(value = "dto") ProofRequestDto proofRequestDto,
	                                     @RequestPart List<MultipartFile> multipartFile,
	                                     @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {

		return proofService.createReplay(communityId, proofRequestDto, multipartFile, userDetails);
	}

  /**
   * 인증글 수정
   */

  /**
   * 인증글 삭제
   */
	@DeleteMapping("/api/proof/{proofId}")
	public Boolean deleteReplay(@PathVariable Long proofId, @AuthenticationPrincipal UserDetailsImpl userDetails){
		return proofService.deleteReplay(proofId, userDetails);
	}

	/**
	 * 전체 인증글 댓글 , 좋아요 갯수 조회
	 */
	@GetMapping("/api/community/count/{communityId}")
	public List<ProofResponseDto> countAllReplay(@PathVariable Long communityId){
		return proofService.countAllReplay(communityId);
	}

	/**
	 * 인증글 댓글 , 좋아요 갯수 조회
	 */
	@GetMapping("/api/proof/count//{proofId}")
	public ProofResponseDto countReplay(@PathVariable Long proofId){
		return proofService.countReplay(proofId);
	}

	/**
	 * 인증글 좋아요
	 */
	@PatchMapping("/api/proof/heart/{proofId}")
	public ProofResponseDto heartReplay(@PathVariable Long proofId, @AuthenticationPrincipal UserDetailsImpl userDetails){
		return proofService.heartReplay(proofId, userDetails);
	}
}
