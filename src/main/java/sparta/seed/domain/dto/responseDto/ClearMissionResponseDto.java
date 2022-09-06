package sparta.seed.domain.dto.responseDto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import sparta.seed.domain.ClearMission;

import java.util.List;

@Getter
public class ClearMissionResponseDto {
	private String selcetedDate;
	private List<ClearMission> clearMissionList;
	private int clearMissionCnt;

	@Builder
	public ClearMissionResponseDto(String selcetedDate, List<ClearMission> clearMissionList, int clearMissionCnt) {
		this.selcetedDate = selcetedDate;
		this.clearMissionList = clearMissionList;
		this.clearMissionCnt = clearMissionCnt;
	}
	@QueryProjection
	public ClearMissionResponseDto(String date,int clearMissionCnt) {
		this.selcetedDate = date;
		this.clearMissionCnt = clearMissionCnt;
	}
}
