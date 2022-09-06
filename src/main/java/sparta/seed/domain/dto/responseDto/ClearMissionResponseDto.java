package sparta.seed.domain.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import sparta.seed.domain.ClearMission;

import java.time.LocalDate;
import java.util.List;

@Getter
public class ClearMissionResponseDto {
	private String selectedDate;
	private List<ClearMission> clearMissionList;
	private int clearMissionCnt;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private LocalDate createdAt;

	private long count;
	@Builder
	public ClearMissionResponseDto(String selectedDate, List<ClearMission> clearMissionList, int clearMissionCnt) {
		this.selectedDate = selectedDate;
		this.clearMissionList = clearMissionList;
		this.clearMissionCnt = clearMissionCnt;
	}
	@QueryProjection
	public ClearMissionResponseDto(LocalDate createdAt ,long count) {
		this.createdAt = createdAt;
		this.count = count;
	}
}
