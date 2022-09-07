package sparta.seed.mission.domain.dto.responsedto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class MissionResponseDto {
	private final Long memberId;
	private final Map<String,Boolean> dailyMission;

	@Builder
	public MissionResponseDto(Long memberId, Map<String, Boolean> dailyMission) {
		this.memberId = memberId;
		this.dailyMission = dailyMission;
	}
}
