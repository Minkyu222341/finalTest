package sparta.seed.repository.customrepository.Impl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import sparta.seed.domain.Community;
import sparta.seed.domain.dto.requestDto.CommunitySearchCondition;
import sparta.seed.repository.customrepository.CommunityRepositoryCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static sparta.seed.domain.QCommunity.community;

@RequiredArgsConstructor
public class CommunityRepositoryImpl implements CommunityRepositoryCustom {
  @PersistenceContext
  EntityManager em;
  @Override
  public QueryResults<Community> getAllCommunity(Pageable pageable, CommunitySearchCondition condition) {
    JPAQueryFactory queryFactory = new JPAQueryFactory(em);

    QueryResults<Community> result = queryFactory
            .selectFrom(community)
            .where(titleEq(condition))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .orderBy(community.id.desc())
            .where()
            .fetchResults();
    return result;
  }
  private BooleanExpression titleEq(CommunitySearchCondition condition) {
    return StringUtils.hasText(condition.getTitle()) ? community.title.contains(condition.getTitle()) : null;
  }

}
