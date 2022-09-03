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
  @GetMapping("/api/replay/{communityId}")
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
	@GetMapping("/api/replay/detail/{replayId}")
	public ProofResponseDto getReplay(@PathVariable Long replayId, @AuthenticationPrincipal UserDetailsImpl userDetails){
		return proofService.getReplay(replayId, userDetails);
	}

  /**
   * 인증글 작성
   */
	@PostMapping(value = "/api/replay/{communityId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
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
	@DeleteMapping("/api/replay/{replayId}")
	public Boolean deleteReplay(@PathVariable Long replayId, @AuthenticationPrincipal UserDetailsImpl userDetails){
		return proofService.deleteReplay(replayId, userDetails);
	}

	/**
	 * 전체 인증글 댓글 , 좋아요 갯수 조회
	 */
	@GetMapping("/api/replay/count/{communityId}")
	public List<ProofResponseDto> countAllReplay(@PathVariable Long communityId){
		return proofService.countAllReplay(communityId);
	}

	/**
	 * 인증글 댓글 , 좋아요 갯수 조회
	 */
	@GetMapping("/api/replay/count/detail/{replayId}")
	public ProofResponseDto countReplay(@PathVariable Long replayId){
		return proofService.countReplay(replayId);
	}

	/**
	 * 인증글 좋아요
	 */
	@PatchMapping("/api/replay/heart/{replayId}")
	public ProofResponseDto heartReplay(@PathVariable Long replayId, @AuthenticationPrincipal UserDetailsImpl userDetails){
		return proofService.heartReplay(replayId, userDetails);
	}
}
