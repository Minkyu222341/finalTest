package sparta.seed.domain.dto.responseDto;

import lombok.Builder;
import lombok.Getter;
import sparta.seed.domain.ClearMission;

import java.time.LocalDate;
import java.util.List;

@Getter
public class ClearMissionResponseDto {
	private LocalDate date;
	private List<ClearMission> clearMissionList;
	private int clearMissionCnt;

	@Builder
	public ClearMissionResponseDto(LocalDate date, List<ClearMission> clearMissionList, int clearMissionCnt) {
		this.date = date;
		this.clearMissionList = clearMissionList;
		this.clearMissionCnt = clearMissionCnt;
	}
}
