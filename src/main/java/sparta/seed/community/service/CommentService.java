package sparta.seed.community.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sparta.seed.community.domain.Comment;
import sparta.seed.community.domain.Proof;
import sparta.seed.community.domain.dto.requestdto.CommentRequestDto;
import sparta.seed.community.domain.dto.responsedto.CommentResponseDto;
import sparta.seed.community.repository.CommentRepository;
import sparta.seed.community.repository.ProofRepository;
import sparta.seed.img.domain.Img;
import sparta.seed.img.repository.ImgRepository;
import sparta.seed.s3.S3Dto;
import sparta.seed.s3.S3Uploader;
import sparta.seed.sercurity.UserDetailsImpl;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
	private final ProofRepository proofRepository;
	private final CommentRepository commentRepository;
	private final ImgRepository imgRepository;
	private final S3Uploader s3Uploader;

	/**
	 * 댓글 조회
	 */
	public List<CommentResponseDto> getAllComment(Long proofId, UserDetailsImpl userDetails) {
		List<Comment> commentList = commentRepository.findAllByProof_Id(proofId);
		List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

		for (Comment comment : commentList){
			commentResponseDtoList.add(CommentResponseDto.builder()
					.commentId(comment.getId())
					.proofId(comment.getProof().getId())
					.nickname(comment.getNickname())
					.content(comment.getContent())
					.writer(userDetails !=null && comment.getMemberId().equals(userDetails.getId()))
					.build());
		}

		return commentResponseDtoList;
	}

	/**
	 * 댓글작성
	 */
	public CommentResponseDto createComment(Long proofId, CommentRequestDto commentRequestDto,
	                                        MultipartFile multipartFile, UserDetailsImpl userDetails) throws IOException {
		Proof proof = proofRepository.findById(proofId)
				.orElseThrow(() -> new IllegalArgumentException("해당 인증글이 존재하지 않습니다."));

		if(multipartFile != null){
			Comment comment = Comment.builder()
					.memberId(userDetails.getId())
					.nickname(userDetails.getNickname())
					.content(commentRequestDto.getContent())
					.proof(proof)
					.build();
			proof.addComment(comment);

			S3Dto upload = s3Uploader.upload(multipartFile);
			Img findImage = Img.builder()
					.imgUrl(upload.getUploadImageUrl())
					.fileName(upload.getFileName())
					.comment(comment)
					.build();

			comment.setImg(findImage);
			imgRepository.save(findImage);

			commentRepository.save(comment);

			return CommentResponseDto.builder()
					.commentId(comment.getId())
					.proofId(comment.getProof().getId())
					.nickname(comment.getNickname())
					.content(comment.getContent())
					.img(comment.getImg())
					.writer(true)
					.build();

		}else {
			Comment comment = Comment.builder()
					.memberId(userDetails.getId())
					.nickname(userDetails.getNickname())
					.content(commentRequestDto.getContent())
					.proof(proof)
					.build();
			proof.addComment(comment);
			commentRepository.save(comment);

			return CommentResponseDto.builder()
					.commentId(comment.getId())
					.proofId(comment.getProof().getId())
					.nickname(comment.getNickname())
					.content(comment.getContent())
					.writer(true)
					.build();
		}
	}

	/**
	 * 댓글 수정
	 */
	@Transactional
	public CommentResponseDto updateComment(Long commentId, CommentRequestDto commentRequestDto,
	                                        MultipartFile multipartFile, UserDetailsImpl userDetails) throws IOException {

		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

		if(userDetails != null && comment.getMemberId().equals(userDetails.getId())){
			comment.update(commentRequestDto.getContent());

			if(multipartFile != null){

				if(imgRepository.findByComment(comment) != null){
					imgRepository.delete(comment.getImg());
				}

				S3Dto upload = s3Uploader.upload(multipartFile);

				Img findImage = Img.builder()
						.imgUrl(upload.getUploadImageUrl())
						.fileName(upload.getFileName())
						.comment(comment)
						.build();

				comment.setImg(findImage);

				imgRepository.save(findImage);
			}
		}
		return CommentResponseDto.builder()
				.commentId(comment.getId())
				.proofId(comment.getProof().getId())
				.nickname(comment.getNickname())
				.content(comment.getContent())
				.img(comment.getImg())
				.writer(true)
				.build();
	}

	/**
	 * 댓글 삭제
	 */
	public Boolean deleteComment(Long commentId, UserDetailsImpl userDetails) {
		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
		Proof proof = proofRepository.findById(comment.getProof().getId())
				.orElseThrow(() -> new IllegalArgumentException("해당 인증글이 존재하지 않습니다."));

		if(userDetails !=null && comment.getMemberId().equals(userDetails.getId())){
			proof.removeComment(comment);
			commentRepository.delete(comment);
			return true;
		}else return false;
	}

}
