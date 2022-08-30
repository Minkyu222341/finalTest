package sparta.seed.repository.customrepository.Impl;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import sparta.seed.domain.Article;
import sparta.seed.domain.dto.responseDto.ArticleSearchCondition;
import sparta.seed.repository.customrepository.ArticleRepositoryCustom;
import sparta.seed.util.TimeCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static sparta.seed.domain.QArticle.article;

@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepositoryCustom {
  @PersistenceContext
  EntityManager em;
  private final TimeCustom timeCustom;
  JPAQueryFactory queryFactory = new JPAQueryFactory(em);
  @Override
  public QueryResults<Article> getAllArticle(Pageable pageable, ArticleSearchCondition condition) {
    QueryResults<Article> result = queryFactory
            .select(article)
            .from(article)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .orderBy(article.id.desc())
            .where()
            .fetchResults();

    return result;
  }
}
