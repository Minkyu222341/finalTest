package sparta.seed.mission.repository.customrepository;

import sparta.seed.mission.domain.dto.requestdto.MissionSearchCondition;
import sparta.seed.mission.domain.dto.responsedto.ClearMissionResponseDto;

import java.util.List;

public interface ClearMissionRepositoryCustom {
  List<ClearMissionResponseDto> dailyMissionStats(MissionSearchCondition condition, Long memberId);

  List<Long> WeekMissionStats(MissionSearchCondition condition, Long memberId);
}
