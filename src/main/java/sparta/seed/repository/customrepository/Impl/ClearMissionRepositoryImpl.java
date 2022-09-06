package sparta.seed.repository.customrepository.Impl;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import sparta.seed.domain.dto.requestDto.MissionSearchCondition;
import sparta.seed.domain.dto.responseDto.ClearMissionResponseDto;
import sparta.seed.repository.customrepository.ClearMissionRepositoryCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static sparta.seed.domain.QClearMission.clearMission;

public class ClearMissionRepositoryImpl implements ClearMissionRepositoryCustom {

  @PersistenceContext
  EntityManager em;

  @Override
  public List<ClearMissionResponseDto> dailyMissionStats(MissionSearchCondition condition,Long memberId) {
    JPAQueryFactory queryFactory = new JPAQueryFactory(em);
    String startDate = condition.getStartDate();
    String endDate = condition.getEndDate();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate start = LocalDate.parse(startDate, formatter);
    LocalDate end = LocalDate.parse(endDate, formatter);

    List<Tuple> fetch = queryFactory.select(clearMission.createdAt, clearMission.count())
            .from(clearMission)
            .where(clearMission.createdAt.between(start, end), clearMission.memberId.eq(memberId))
            .groupBy(clearMission.createdAt)
            .fetch();
    List<ClearMissionResponseDto> result = new ArrayList<>();

    for (Tuple tuple : fetch) {
      LocalDate clearDate = tuple.get(clearMission.createdAt);
      int clearCount = tuple.get(clearMission.count()).intValue();
      result.add(ClearMissionResponseDto.builder()
              .date(clearDate)
              .clearMissionCnt(clearCount)
              .build());
    }
    return result;
  }

  @Override
  public List<Long> WeekMissionStats(MissionSearchCondition condition, Long memberId) {
    return null;
  }
}
