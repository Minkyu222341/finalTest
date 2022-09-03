package sparta.seed.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sparta.seed.domain.Community;
import sparta.seed.domain.Heart;
import sparta.seed.domain.Img;
import sparta.seed.domain.Replay;
import sparta.seed.domain.dto.requestDto.ReplayRequestDto;
import sparta.seed.domain.dto.responseDto.ReplayResponseDto;
import sparta.seed.repository.CommunityRepository;
import sparta.seed.repository.HeartRepository;
import sparta.seed.repository.ImgRepository;
import sparta.seed.repository.ReplayRepository;
import sparta.seed.s3.S3Dto;
import sparta.seed.s3.S3Uploader;
import sparta.seed.sercurity.UserDetailsImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplayService {

	private final ReplayRepository replayRepository;
	private final CommunityRepository communityRepository;
	private final HeartRepository heartRepository;
	private final ImgRepository imgRepository;
	private final S3Uploader s3Uploader;

	/**
	  글에 달린 인증글 조회
	 */
	public List<ReplayResponseDto> getAllReplay(Long CommunityId, int page, int size, UserDetailsImpl userDetails) {

		Sort.Direction direction = Sort.Direction.DESC;
		Sort sort = Sort.by(direction, "createdAt");
		Pageable pageable = PageRequest.of(page, size, sort);
		Page<Replay> replayList = replayRepository.findAllByCommunity_Id(CommunityId, pageable);
		List<ReplayResponseDto> replayResponseDtoList = new ArrayList<>();
		for(Replay replay : replayList){
			replayResponseDtoList.add(ReplayResponseDto.builder()
					.replayId(replay.getId())
					.title(replay.getTitle())
					.content(replay.getContent())
					.img(replay.getImgList())
					.commentCnt(replay.getCommentList().size())
					.heartCnt(replay.getHeartList().size())
					.isWriter(userDetails !=null && replay.getMemberId().equals(userDetails.getId()))
					.isHeart(userDetails !=null && heartRepository.existsByReplayAndMemberId(replay, userDetails.getId()))
					.build());
		}
		return replayResponseDtoList;
	}

	/**
	 * 글에 달린 인증글 상세 조회
	 */
	public ReplayResponseDto getReplay(Long replayId, UserDetailsImpl userDetails) {
		Replay replay = replayRepository.findById(replayId)
				.orElseThrow(() -> new IllegalArgumentException("해당 인증글이 존재하지 않습니다."));
		return ReplayResponseDto.builder()
				.replayId(replay.getId())
				.title(replay.getTitle())
				.content(replay.getContent())
				.img(replay.getImgList())
				.commentCnt(replay.getCommentList().size())
				.heartCnt(replay.getHeartList().size())
				.isWriter(userDetails !=null && replay.getMemberId().equals(userDetails.getId()))
				.isHeart(userDetails !=null && heartRepository.existsByReplayAndMemberId(replay, userDetails.getId()))
				.build();
	}

	/**
	 * 인증글 작성
	 */
	public ReplayResponseDto createReplay(Long CommunityId, ReplayRequestDto replayRequestDto,
	                                      List<MultipartFile> multipartFile, UserDetailsImpl userDetails) throws IOException {

		Long loginUserId = userDetails.getId();
		String nickname = userDetails.getNickname();
		Community community = communityRepository.findById(CommunityId)
				.orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));

		Replay replay = Replay.builder()
				.memberId(loginUserId)
				.nickname(nickname)
				.title(replayRequestDto.getTitle())
				.content(replayRequestDto.getContent())
				.community(community)
				.build();

		for (MultipartFile file : multipartFile) {
			S3Dto upload = s3Uploader.upload(file);
			Img findImage = Img.builder()
					.imgUrl(upload.getUploadImageUrl())
					.fileName(upload.getFileName())
					.replay(replay)
					.build();
			replay.addImg(findImage);
			imgRepository.save(findImage);
		}

		replayRepository.save(replay);

			return ReplayResponseDto.builder()
					.replayId(replay.getId())
					.title(replay.getTitle())
					.content(replay.getContent())
					.img(replay.getImgList())
					.commentCnt(replay.getCommentList().size())
					.heartCnt(replay.getHeartList().size())
					.isWriter(true)
					.isHeart(false)
					.build();
	}

	/**
	 * 인증글 수정
	 */

	/**
	 * 인증글 삭제
	 */
	public Boolean deleteReplay(Long replayId, UserDetailsImpl userDetails) {
		Replay replay = replayRepository.findById(replayId)
				.orElseThrow(() -> new IllegalArgumentException("해당 인증글이 존재하지 않습니다."));

		if(userDetails !=null && replay.getMemberId().equals(userDetails.getId())){
			replayRepository.delete(replay);
			return true;
		}else return false;
	}

	/**
	 * 전체 인증글 댓글 , 좋아요 갯수 조회
	 */
	public List<ReplayResponseDto> countAllReplay(Long CommunityId) {
		List<Replay> replayList = replayRepository.findAllByCommunity_Id(CommunityId);
		List<ReplayResponseDto> replayResponseDtoList = new ArrayList<>();
		for (Replay replay : replayList){
			replayResponseDtoList.add(ReplayResponseDto.builder()
					.replayId(replay.getId())
					.commentCnt(replay.getCommentList().size())
					.heartCnt(replay.getHeartList().size())
					.build());
		}
		return replayResponseDtoList;
	}

	/**
	 * 인증글 댓글 , 좋아요 갯수 조회
	 */
	public ReplayResponseDto countReplay(Long replayId) {
		Replay replay = replayRepository.findById(replayId)
				.orElseThrow(() -> new IllegalArgumentException("해당 인증글이 존재하지 않습니다."));
		return ReplayResponseDto.builder()
				.replayId(replay.getId())
				.commentCnt(replay.getCommentList().size())
				.heartCnt(replay.getHeartList().size())
				.build();
	}

	/**
	 * 인증글 좋아요
	 */
	public ReplayResponseDto heartReplay(Long replayId, UserDetailsImpl userDetails) {
		Replay replay = replayRepository.findById(replayId)
				.orElseThrow(() -> new IllegalArgumentException("해당 인증글이 존재하지 않습니다."));
		Long loginUserId = userDetails.getId();

		if(!heartRepository.existsByReplayAndMemberId(replay, loginUserId)){
			Heart heart = Heart.builder()
					.replay(replay)
					.memberId(loginUserId)
					.build();
			replay.addHeart(heart);
			heartRepository.save(heart);
			return ReplayResponseDto.builder()
					.replayId(replay.getId())
					.isHeart(true)
					.heartCnt(replay.getHeartList().size()).build();
		}else {
			Heart heart = heartRepository.findByReplayAndMemberId(replay, loginUserId);
			replay.removeHeart(heart);
			heartRepository.delete(heart);
			return ReplayResponseDto.builder()
					.replayId(replay.getId())
					.isHeart(false)
					.heartCnt(replay.getHeartList().size()).build();
		}
	}

}
