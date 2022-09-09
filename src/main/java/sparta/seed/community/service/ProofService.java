package sparta.seed.community.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sparta.seed.community.repository.HeartRepository;
import sparta.seed.community.domain.Community;
import sparta.seed.community.domain.Heart;
import sparta.seed.community.domain.Proof;
import sparta.seed.community.domain.dto.requestdto.ProofRequestDto;
import sparta.seed.community.domain.dto.responsedto.ProofCountResponseDto;
import sparta.seed.community.domain.dto.responsedto.ProofHeartResponseDto;
import sparta.seed.community.domain.dto.responsedto.ProofResponseDto;
import sparta.seed.community.repository.CommunityRepository;
import sparta.seed.community.repository.ParticipantsRepository;
import sparta.seed.community.repository.ProofRepository;
import sparta.seed.exception.CustomException;
import sparta.seed.exception.ErrorCode;
import sparta.seed.img.domain.Img;
import sparta.seed.img.repository.ImgRepository;
import sparta.seed.msg.ResponseMsg;
import sparta.seed.s3.S3Dto;
import sparta.seed.s3.S3Uploader;
import sparta.seed.sercurity.UserDetailsImpl;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProofService {

	private final ProofRepository proofRepository;
	private final CommunityRepository communityRepository;

	private final ParticipantsRepository participantsRepository;
	private final HeartRepository heartRepository;
	private final ImgRepository imgRepository;
	private final S3Uploader s3Uploader;

	/**
	  글에 달린 인증글 조회
	 */
	public List<ProofResponseDto> getAllProof(Long communityId, int page, int size, UserDetailsImpl userDetails) {

		Sort.Direction direction = Sort.Direction.DESC;
		Sort sort = Sort.by(direction, "createdAt");
		Pageable pageable = PageRequest.of(page, size, sort);
		try {
		Page<Proof> replayList = proofRepository.findAllByCommunity_Id(communityId, pageable);
		List<ProofResponseDto> proofResponseDtoList = new ArrayList<>();
		for(Proof proof : replayList){
			proofResponseDtoList.add(buildProofResponseDto(userDetails, proof));
		}
		return proofResponseDtoList;
		}catch (Exception e) {throw new CustomException(ErrorCode.NOT_FOUND_PROOF);}
	}

	/**
	 * 글에 달린 인증글 상세 조회
	 */
	public ProofResponseDto getProof(Long proofId, UserDetailsImpl userDetails) {
		Proof proof = findTheProofById(proofId);
		return buildProofResponseDto(userDetails, proof);
	}

	/**
	 * 인증글 작성
	 */
	public ResponseEntity<String> createProof(Long communityId, ProofRequestDto proofRequestDto,
	                                                     List<MultipartFile> multipartFile, UserDetailsImpl userDetails) throws IOException {
		if(userDetails != null) {
			Community community = communityRepository.findById(communityId)
					.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COMMUNITY));

			Proof proof = Proof.builder()
					.memberId(userDetails.getId())
					.nickname(userDetails.getNickname())
					.title(proofRequestDto.getTitle())
					.content(proofRequestDto.getContent())
					.community(community)
					.build();

			if (participantsRepository.existsByCommunityAndMemberId(community, userDetails.getId())) {
				List<Img> imgList = new ArrayList<>();
				buildImgList(multipartFile, proof, imgList);
				proofRepository.save(proof);
				return ResponseEntity.ok().body(ResponseMsg.WRITE_SUCCESS.getMsg());

			} else throw new CustomException(ErrorCode.NOT_PARTICIPATED);
		}else throw new CustomException(ErrorCode.UNKNOWN_USER);
	}

	/**
	 * 인증글 수정
	 */
	@Transactional
	public ResponseEntity<String> updateProof(Long proofId, ProofRequestDto proofRequestDto,
	                                                    List<MultipartFile> multipartFile, UserDetailsImpl userDetails) throws IOException {
		Proof proof = findTheProofById(proofId);
		if(userDetails !=null && proof.getMemberId().equals(userDetails.getId())){
			proof.updateProof(proofRequestDto);

			if(proofRequestDto.getImgIdList().length > 0){
				for (int i=0; i<proofRequestDto.getImgIdList().length; i++){
					imgRepository.deleteById(proofRequestDto.getImgIdList()[i]);
				}
			}

			List<Img> imgList = new ArrayList<>();

			if(multipartFile!=null) {
				buildImgList(multipartFile, proof, imgList);
			}
			return ResponseEntity.ok().body(ResponseMsg.UPDATE_SUCCESS.getMsg());

		}throw new CustomException(ErrorCode.INCORRECT_USERID);
	}

	/**
	 * 인증글 삭제
	 */
	public ResponseEntity<Boolean> deleteProof(Long proofId, UserDetailsImpl userDetails) {
		Proof proof = findTheProofById(proofId);

		if(userDetails !=null && proof.getMemberId().equals(userDetails.getId())){
			proofRepository.delete(proof);
			return ResponseEntity.ok().body(true);
		}throw new CustomException(ErrorCode.INCORRECT_USERID);
	}

	/**
	 * 전체 인증글 댓글 , 좋아요 갯수 조회
	 */
	public List<ProofCountResponseDto> countAllProof(Long communityId) {
		try {
			List<Proof> proofList = proofRepository.findAllByCommunity_Id(communityId);
			List<ProofCountResponseDto> proofResponseDtoList = new ArrayList<>();
			for (Proof proof : proofList) {
				proofResponseDtoList.add(ProofCountResponseDto.builder()
						.proofId(proof.getId())
						.commentCnt(proof.getCommentList().size())
						.heartCnt(proof.getHeartList().size())
						.build());
			}
			return proofResponseDtoList;
		}catch (Exception e) {throw new CustomException(ErrorCode.NOT_FOUND_PROOF);}
	}

	/**
	 * 인증글 댓글 , 좋아요 갯수 조회
	 */
	public ProofCountResponseDto countProof(Long proofId) {
		Proof proof = findTheProofById(proofId);
		return ProofCountResponseDto.builder()
				.proofId(proof.getId())
				.commentCnt(proof.getCommentList().size())
				.heartCnt(proof.getHeartList().size())
				.build();
	}

	/**
	 * 인증글 좋아요
	 */
	public ProofHeartResponseDto heartProof(Long proofId, UserDetailsImpl userDetails) {
		Proof proof = findTheProofById(proofId);
		try {
			Long loginUserId = userDetails.getId();

			if (!heartRepository.existsByProofAndMemberId(proof, loginUserId)) {
				Heart heart = Heart.builder()
						.proof(proof)
						.memberId(loginUserId)
						.build();
				proof.addHeart(heart);
				heartRepository.save(heart);
				return ProofHeartResponseDto.builder()
						.proofId(proof.getId())
						.heart(true)
						.heartCnt(proof.getHeartList().size()).build();
			} else {
				Heart heart = heartRepository.findByProofAndMemberId(proof, loginUserId);
				proof.removeHeart(heart);
				heartRepository.delete(heart);
				return ProofHeartResponseDto.builder()
						.proofId(proof.getId())
						.heart(false)
						.heartCnt(proof.getHeartList().size()).build();
			}
		}catch (Exception e) {throw new CustomException(ErrorCode.UNKNOWN_USER);}
	}

	private Proof findTheProofById(Long proofId) {
		return proofRepository.findById(proofId)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PROOF));
	}

	private ProofResponseDto buildProofResponseDto(UserDetailsImpl userDetails, Proof proof) {
		return ProofResponseDto.builder()
				.proofId(proof.getId())
				.creatAt(proof.getCreatedAt())
				.nickname(proof.getNickname())
				.title(proof.getTitle())
				.content(proof.getContent())
				.img(proof.getImgList())
				.commentCnt(proof.getCommentList().size())
				.heartCnt(proof.getHeartList().size())
				.writer(userDetails != null && proof.getMemberId().equals(userDetails.getId()))
				.heart(userDetails != null && heartRepository.existsByProofAndMemberId(proof, userDetails.getId()))
				.build();
	}

	private void buildImgList(List<MultipartFile> multipartFile, Proof proof, List<Img> imgList) throws IOException {
		for (MultipartFile file : multipartFile) {
			if(multipartFile.size() < 6 && proof.getImgList().size() < 11){
				S3Dto upload = s3Uploader.upload(file);
				Img findImage = Img.builder()
						.imgUrl(upload.getUploadImageUrl())
						.fileName(upload.getFileName())
						.proof(proof)
						.build();
				proof.addImg(findImage);
				imgList.add(findImage);
			}else throw new CustomException(ErrorCode.EXCEED_IMG_CNT);
		}
		imgRepository.saveAll(imgList);
	}
}
