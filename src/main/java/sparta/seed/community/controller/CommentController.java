package sparta.seed.community.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sparta.seed.community.domain.dto.requestdto.CommentRequestDto;
import sparta.seed.community.domain.dto.responsedto.CommentResponseDto;
import sparta.seed.community.service.CommentService;
import sparta.seed.sercurity.UserDetailsImpl;

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
	public ResponseEntity<String> createComment(@PathVariable Long proofId,
	                                        @Valid @RequestPart(value = "dto") CommentRequestDto commentRequestDto,
	                                        @RequestPart(required = false) MultipartFile multipartFile,
	                                        @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
		return commentService.createComment(proofId, commentRequestDto, multipartFile, userDetails);
	}

	/**
	 * 댓글 수정
	 */
	@PatchMapping(value = "/api/comments/{commentId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<String> updateComment(@PathVariable Long commentId,
	                                                        @Valid @RequestPart(value = "dto") CommentRequestDto commentRequestDto,
	                                                        @RequestPart(required = false) MultipartFile multipartFile,
	                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
		return commentService.updateComment(commentId, commentRequestDto, multipartFile, userDetails);
	}

  /**
   * 댓글 삭제
   */
	@DeleteMapping("/api/comments/{commentId}")
	public ResponseEntity<Boolean> deleteComment(@PathVariable Long commentId,  @AuthenticationPrincipal UserDetailsImpl userDetails){
		return commentService.deleteComment(commentId, userDetails);
	}

}
