package sparta.seed.campaign.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sparta.seed.campaign.crawling.CrawlingV1;
import sparta.seed.campaign.domain.Campaign;
import sparta.seed.campaign.domain.dto.requestdto.CampaignRequestDto;
import sparta.seed.campaign.domain.dto.responsedto.CampaignResponseDto;
import sparta.seed.campaign.repository.CampaignRepository;
import sparta.seed.img.domain.Img;
import sparta.seed.img.repository.ImgRepository;
import sparta.seed.s3.S3Dto;
import sparta.seed.s3.S3Uploader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignService {
  private final CampaignRepository campaignRepository;
	private final ImgRepository imgRepository;
	private final S3Uploader s3Uploader;
	private final CrawlingV1 crawlingV1;

	/**
	 * 캠페인 리스트
	 */
	public List<CampaignResponseDto> getAllCampaign(int page, int size) {
		Sort.Direction direction = Sort.Direction.DESC;
		Sort sort = Sort.by(direction, "createdAt");
		Pageable pageable = PageRequest.of(page, size, sort);
		Page<Campaign> campaignPage = campaignRepository.findAll(pageable);
		List<CampaignResponseDto> campaignResponseDtoList = new ArrayList<>();
		for(Campaign campaign : campaignPage){
			campaignResponseDtoList.add(CampaignResponseDto.builder()
					.campaignId(campaign.getId())
					.title(campaign.getTitle())
					.thumbnail(campaign.getThumbnail())
					.build());
		}
		return campaignResponseDtoList;
	}

	/**
	 * 캠페인 상세페이지
	 */
	public CampaignResponseDto getCampaign(Long campaignId) {
		Campaign campaign = campaignRepository.findById(campaignId)
				.orElseThrow(()-> new IllegalArgumentException ("없는 캠페인입니다."));

		return CampaignResponseDto.builder()
				.campaignId(campaign.getId())
				.title(campaign.getTitle())
				.imgList(campaign.getImgList())
				.build();
	}

	/**
	 * 캠페인 작성 (관리자용)
	 */
	public CampaignResponseDto createCampaign(CampaignRequestDto campaignRequestDto, List<MultipartFile> multipartFile) throws IOException {

		Campaign campaign = Campaign.builder()
				.title(campaignRequestDto.getTitle())
				.build();

		for (MultipartFile file : multipartFile) {
			S3Dto upload = s3Uploader.upload(file);
			Img findImage = Img.builder()
					.imgUrl(upload.getUploadImageUrl())
					.campaign(campaign)
					.build();

			if(findImage.getImgUrl().contains("[Thumbnail]")){
				campaign.setThumbnail(findImage.getImgUrl());
				imgRepository.save(findImage);
			}else {
				campaign.addImg(findImage);
				imgRepository.save(findImage);
			}
		}

		campaignRepository.save(campaign);

		return CampaignResponseDto.builder()
				.campaignId(campaign.getId())
				.title(campaign.getTitle())
				.thumbnail(campaign.getThumbnail())
				.imgList(campaign.getImgList())
				.build();
	}

	/**
	 * 캠페인작성
	 */

	public Boolean insertCampaign() throws InterruptedException {
			crawlingV1.process();
			return true;
	}


}
