package sparta.seed.repository.customrepository;

import sparta.seed.domain.dto.requestDto.MissionSearchCondition;
import sparta.seed.domain.dto.responseDto.ClearMissionResponseDto;

import java.util.List;

public interface ClearMissionRepositoryCustom {
  List<ClearMissionResponseDto> dailyMissionStats(MissionSearchCondition condition, Long memberId);

  List<Long> WeekMissionStats(MissionSearchCondition condition, Long memberId);
}
