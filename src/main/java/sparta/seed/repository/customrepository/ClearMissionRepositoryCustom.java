package sparta.seed.repository.customrepository;

import sparta.seed.domain.dto.requestDto.MissionSearchCondition;

import java.util.List;

public interface ClearMissionRepositoryCustom {
  List<Long> dailyMissionStats(MissionSearchCondition condition,Long memberId);
}
