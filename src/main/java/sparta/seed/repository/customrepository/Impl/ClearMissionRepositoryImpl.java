package sparta.seed.repository.customrepository.Impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import sparta.seed.domain.dto.requestDto.MissionSearchCondition;
import sparta.seed.domain.dto.responseDto.ClearMissionResponseDto;
import sparta.seed.domain.dto.responseDto.QClearMissionResponseDto;
import sparta.seed.repository.customrepository.ClearMissionRepositoryCustom;
import sparta.seed.util.DateUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
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
    List<ClearMissionResponseDto> result = queryFactory.select(new QClearMissionResponseDto(clearMission.createdAt, clearMission.count()))
            .from(clearMission)
            .where(clearMission.createdAt.between(dateScope.get(0), dateScope.get(1)), clearMission.memberId.eq(memberId))
            .groupBy(clearMission.createdAt)
            .fetch();
    return result;
  }

  @Override
  public List<Long> WeekMissionStats(MissionSearchCondition condition, Long memberId) {
    JPAQueryFactory queryFactory = new JPAQueryFactory(em);
    return null;
  }
}
