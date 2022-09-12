package sparta.seed.community.repository.customrepository.impl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import sparta.seed.community.domain.Community;
import sparta.seed.community.domain.dto.requestdto.CommunitySearchCondition;
import sparta.seed.community.repository.customrepository.CommunityRepositoryCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static sparta.seed.community.domain.QCommunity.community;


@RequiredArgsConstructor
public class CommunityRepositoryImpl implements CommunityRepositoryCustom {
  @PersistenceContext
  EntityManager em;

  @Override
  public QueryResults<Community> getAllCommunity(Pageable pageable, CommunitySearchCondition condition) {
    JPAQueryFactory queryFactory = new JPAQueryFactory(em);
    return queryFactory // querydsl 강의 뒷쪽 페이징 참고
            .selectFrom(community)
            .where(titleEq(condition))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .orderBy(community.id.desc())
            .where()
            .fetchResults();
  }

  public List<Community> activeCommunity() {
    JPAQueryFactory queryFactory = new JPAQueryFactory(em);
    return queryFactory.selectFrom(community)
            .where(community.endDate.gt(String.valueOf(LocalDate.now())), community.startDate.loe(String.valueOf(LocalDate.now()))) // 종료일 > 현재 시간 , 시작일 <= 현재시간
            .orderBy(community.proofList.size().desc()) // 인증글 갯수대로 정렬
            .fetch().stream().limit(10).collect(Collectors.toList()); // 10개만 출력

  }


  private BooleanExpression titleEq(CommunitySearchCondition condition) {
    return StringUtils.hasText(condition.getTitle()) ? community.title.contains(condition.getTitle()) : null;
  }

}
