package sparta.seed.mission.domain.dto.responsedto;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MissionResponseDto {
	private final Long memberId;
	private List<MissionDetailResponseDto> dailyMission = new ArrayList<>();

	@Builder
	public MissionResponseDto(Long memberId) {
		this.memberId = memberId;
	}

	public void addMisson(MissionDetailResponseDto missionListDto){
		this.dailyMission.add(missionListDto);
	}
}
