package sparta.seed.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sparta.seed.domain.dto.requestDto.CommentRequestDto;
import sparta.seed.domain.dto.responseDto.CommentResponseDto;
import sparta.seed.sercurity.UserDetailsImpl;
import sparta.seed.service.CommentService;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {
	private final CommentService commentService;

  /**
   * 댓글 조회
   */
	@GetMapping("/api/comments/{proofId}")
	public List<CommentResponseDto> getAllComment(@PathVariable Long proofId, @AuthenticationPrincipal UserDetailsImpl userDetails){
		return commentService.getAllComment(proofId, userDetails);
	}

  /**
   * 댓글작성
   */
	@PostMapping(value = "/api/comments/{proofId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	public CommentResponseDto createComment(@PathVariable Long proofId,
	                                        @Valid @RequestPart(value = "dto") CommentRequestDto commentRequestDto,
	                                        @RequestPart(required = false) MultipartFile multipartFile,
	                                        @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
		return commentService.createComment(proofId, commentRequestDto, multipartFile, userDetails);
	}

	/**
	 * 댓글 수정
	 */
	@PatchMapping(value = "/api/comments/{commentId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	public CommentResponseDto updateComment(@PathVariable Long commentId,
	                                        @Valid @RequestPart(value = "dto") CommentRequestDto commentRequestDto,
	                                        @RequestPart(required = false) MultipartFile multipartFile,
	                                        @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
		return commentService.updateComment(commentId, commentRequestDto, multipartFile, userDetails);
	}

  /**
   * 댓글 삭제
   */
	@DeleteMapping("/api/comments/{commentId}")
	public Boolean deleteComment(@PathVariable Long commentId,  @AuthenticationPrincipal UserDetailsImpl userDetails){
		return commentService.deleteComment(commentId, userDetails);
	}

}
