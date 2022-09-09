package sparta.seed.mission.domain.dto.responsedto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MissionDetailResponseDto {
	private String missionName;
	private boolean complete;

	public MissionDetailResponseDto(String missionName, boolean complete) {
		this.missionName = missionName;
		this.complete = complete;
	}
}
