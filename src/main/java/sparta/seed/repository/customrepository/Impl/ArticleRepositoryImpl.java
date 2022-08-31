package sparta.seed.repository.customrepository.Impl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import sparta.seed.domain.Article;
import sparta.seed.domain.dto.responseDto.ArticleSearchCondition;
import sparta.seed.repository.customrepository.ArticleRepositoryCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static sparta.seed.domain.QArticle.article;

@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepositoryCustom {
  @PersistenceContext
  EntityManager em;
//  JPAQueryFactory queryFactory = new JPAQueryFactory(em);
  @Override
  public QueryResults<Article> getAllArticle(Pageable pageable, ArticleSearchCondition condition) {
    JPAQueryFactory queryFactory = new JPAQueryFactory(em);

    QueryResults<Article> result = queryFactory
            .selectFrom(article)
            .where(titleEq(condition))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .orderBy(article.id.desc())
            .where()
            .fetchResults();
    return result;
  }
  private BooleanExpression titleEq(ArticleSearchCondition condition) {
    return StringUtils.hasText(condition.getTitle()) ? article.title.eq(condition.getTitle()) : null;
  }

}
