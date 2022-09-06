package sparta.seed.repository.customrepository.Impl;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import sparta.seed.domain.dto.requestDto.MissionSearchCondition;
import sparta.seed.domain.dto.responseDto.ClearMissionResponseDto;
import sparta.seed.repository.customrepository.ClearMissionRepositoryCustom;
import sparta.seed.util.DateUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static sparta.seed.domain.QClearMission.clearMission;

@RequiredArgsConstructor
public class ClearMissionRepositoryImpl implements ClearMissionRepositoryCustom {

  @PersistenceContext
  EntityManager em;
  private final DateUtil dateUtil;

  @Override
  public List<ClearMissionResponseDto> dailyMissionStats(MissionSearchCondition condition, Long memberId) {
    JPAQueryFactory queryFactory = new JPAQueryFactory(em);
    List<LocalDate> dateScope = dateUtil.scopeOfStats(condition);
    List<Tuple> fetch = queryFactory.select(clearMission.createdAt, clearMission.count())
            .from(clearMission)
            .where(clearMission.createdAt.between(dateScope.get(0), dateScope.get(1)), clearMission.memberId.eq(memberId))
            .groupBy(clearMission.createdAt)
            .fetch();

    List<ClearMissionResponseDto> result = new ArrayList<>();
    for (Tuple tuple : fetch) {
      LocalDate clearDate = tuple.get(clearMission.createdAt);
      int clearCount = tuple.get(clearMission.count()).intValue();
      result.add(ClearMissionResponseDto.builder()
              .selcetedDate(String.valueOf(clearDate))
              .clearMissionCnt(clearCount)
              .build());
    }
    return result;
  }

  @Override
  public List<Long> WeekMissionStats(MissionSearchCondition condition, Long memberId) {
    JPAQueryFactory queryFactory = new JPAQueryFactory(em);
    return null;
  }
}
