package sparta.seed.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sparta.seed.domain.dto.requestDto.CampaignRequestDto;
import sparta.seed.domain.dto.responseDto.CampaignResponseDto;
import sparta.seed.service.CampaignService;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CampaignController {
	private final CampaignService campaignService;

  /**
   * 캠페인 리스트
   */
	@GetMapping("/api/campaigns")
	public List<CampaignResponseDto> getAllCampaign(@RequestParam("page") int page, @RequestParam("size") int size){
		return campaignService.getAllCampaign(page, size);
	}

  /**
   * 캠페인 상세페이지
   */
  @GetMapping("/api/campaigns/{campaignId}")
  public CampaignResponseDto getCampaign(@PathVariable Long campaignId) {
	  return campaignService.getCampaign(campaignId);
  }

	/**
	 * 캠페인 작성 (관리자용)
	 */
	@PostMapping(value = "/api/campaigns", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	public CampaignResponseDto createCampaign(@Valid @RequestPart(value = "dto") CampaignRequestDto campaignRequestDto,
	                                          @RequestPart List<MultipartFile> multipartFile) throws IOException {
		return campaignService.createCampaign(campaignRequestDto, multipartFile);
	}

	/**
	 * 캠페인 작성
	 */
	@GetMapping("/campaigns")
	public Boolean insertCampaign() throws InterruptedException {
		return campaignService.insertCampaign();
	}
}
