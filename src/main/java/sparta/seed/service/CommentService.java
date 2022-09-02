package sparta.seed.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sparta.seed.domain.Comment;
import sparta.seed.domain.Img;
import sparta.seed.domain.Replay;
import sparta.seed.domain.dto.requestDto.CommentRequestDto;
import sparta.seed.domain.dto.responseDto.CommentResponseDto;
import sparta.seed.repository.CommentRepository;
import sparta.seed.repository.ImgRepository;
import sparta.seed.repository.ReplayRepository;
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
	private final ReplayRepository replayRepository;
	private final CommentRepository commentRepository;
	private final ImgRepository imgRepository;
	private final S3Uploader s3Uploader;

	/**
	 * 댓글 조회
	 */
	public List<CommentResponseDto> getAllComment(Long replayId, UserDetailsImpl userDetails) {
		List<Comment> commentList = commentRepository.findAllByReplay_Id(replayId);
		List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

		for (Comment comment : commentList){
			commentResponseDtoList.add(CommentResponseDto.builder()
					.commentId(comment.getId())
					.replayId(comment.getReplay().getId())
					.nickname(comment.getNickname())
					.content(comment.getContent())
					.isWriter(userDetails !=null && comment.getMemberId().equals(userDetails.getId()))
					.build());
		}

		return commentResponseDtoList;
	}

	/**
	 * 댓글작성
	 */
	public CommentResponseDto createComment(Long replayId, CommentRequestDto commentRequestDto,
	                                        MultipartFile multipartFile, UserDetailsImpl userDetails) throws IOException {
		Replay replay = replayRepository.findById(replayId)
				.orElseThrow(() -> new IllegalArgumentException("해당 인증글이 존재하지 않습니다."));

		if(multipartFile != null){
			Comment comment = Comment.builder()
					.memberId(userDetails.getId())
					.nickname(userDetails.getNickname())
					.content(commentRequestDto.getContent())
					.replay(replay)
					.build();
			replay.addComment(comment);

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
					.replayId(comment.getReplay().getId())
					.nickname(comment.getNickname())
					.content(comment.getContent())
					.img(comment.getImg())
					.isWriter(true)
					.build();

		}else {
			Comment comment = Comment.builder()
					.memberId(userDetails.getId())
					.nickname(userDetails.getNickname())
					.content(commentRequestDto.getContent())
					.replay(replay)
					.build();
			replay.addComment(comment);
			commentRepository.save(comment);

			return CommentResponseDto.builder()
					.commentId(comment.getId())
					.replayId(comment.getReplay().getId())
					.nickname(comment.getNickname())
					.content(comment.getContent())
					.isWriter(true)
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

		if(comment.getMemberId().equals(userDetails.getId())){
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
				.replayId(comment.getReplay().getId())
				.nickname(comment.getNickname())
				.content(comment.getContent())
				.img(comment.getImg())
				.isWriter(true)
				.build();
	}

	/**
	 * 댓글 삭제
	 */
	public Boolean deleteComment(Long commentId, UserDetailsImpl userDetails) {
		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
		Replay replay = replayRepository.findById(comment.getReplay().getId())
				.orElseThrow(() -> new IllegalArgumentException("해당 인증글이 존재하지 않습니다."));

		if(userDetails !=null && comment.getMemberId().equals(userDetails.getId())){
			replay.removeComment(comment);
			commentRepository.delete(comment);
			return true;
		}else return false;
	}

}
