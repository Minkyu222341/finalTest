package sparta.seed.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sparta.seed.domain.Mission;
import sparta.seed.domain.dto.requestDto.MissionRequestDto;
import sparta.seed.domain.dto.responseDto.MissionResponseDto;
import sparta.seed.sercurity.UserDetailsImpl;
import sparta.seed.service.MissionService;

@RestController
@RequiredArgsConstructor
public class MissionController {
	private final MissionService missionService;

	/**
	 * 데일리미션 확인
	 */
	@GetMapping("/api/missions")
	public MissionResponseDto getMissionAll(@AuthenticationPrincipal UserDetailsImpl userDetails){
		return missionService.getMissionAll(userDetails);
	}

	/**
	 * 미션 생성 - 관리자
	 */
	@PostMapping("/api/missions")
  public Mission crateMission(@RequestBody MissionRequestDto missionRequestDto){
		return missionService.crateMission(missionRequestDto);
	}

	/**
	 * 유저한테 랜덤 미션 5개 넣어주기 (스케줄러 연동)
	 */
	@PatchMapping("/api/missions")
	public MissionResponseDto injectMission(@AuthenticationPrincipal UserDetailsImpl userDetails){
		return missionService.injectMission(userDetails);
	}

	/**
	 * 미션 완료
	 */
	@PatchMapping("/api/complete")
	public Boolean completeMission(@AuthenticationPrincipal UserDetailsImpl userDetails,
	                               @RequestBody MissionRequestDto missionRequestDto){
		return missionService.completeMission(userDetails, missionRequestDto);
	}

}
