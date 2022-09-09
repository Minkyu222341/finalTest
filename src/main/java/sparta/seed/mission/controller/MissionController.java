package sparta.seed.mission.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sparta.seed.mission.domain.Mission;
import sparta.seed.mission.domain.dto.requestdto.MissionRequestDto;
import sparta.seed.mission.domain.dto.responsedto.MissionDetailResponseDto;
import sparta.seed.mission.domain.dto.responsedto.MissionResponseDto;
import sparta.seed.mission.service.MissionService;
import sparta.seed.sercurity.UserDetailsImpl;

import java.text.ParseException;

@RestController
@RequiredArgsConstructor
public class MissionController {
	private final MissionService missionService;

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
	@GetMapping("/api/missions")
	public MissionResponseDto injectMission(@AuthenticationPrincipal UserDetailsImpl userDetails){
		return missionService.injectMission(userDetails);
	}

	/**
	 * 미션 완료
	 */
	@PatchMapping("/api/missions")
	public MissionDetailResponseDto completeMission(@AuthenticationPrincipal UserDetailsImpl userDetails,
	                                                @RequestBody MissionRequestDto missionRequestDto) throws ParseException {
		return missionService.completeMission(userDetails, missionRequestDto);
	}
}
