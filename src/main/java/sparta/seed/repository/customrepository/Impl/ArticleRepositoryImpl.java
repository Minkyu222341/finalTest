package sparta.seed.repository.customrepository.Impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import sparta.seed.domain.dto.responseDto.ArticleResponseDto;
import sparta.seed.domain.dto.responseDto.ArticleSearchCondition;
import sparta.seed.repository.customrepository.ArticleRepositoryCustom;
import sparta.seed.util.TimeCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepositoryCustom {
  @PersistenceContext
  EntityManager em;
  private final TimeCustom timeCustom;
  JPAQueryFactory queryFactory = new JPAQueryFactory(em);
  @Override
  public Slice<ArticleResponseDto> getAllArticle(Pageable pageable, ArticleSearchCondition condition) {
//    QueryResults<Article> result = queryFactory
//            .select(new ArticleResponseDto(article.id,
//                    article.imgList,
//                    article.title,
//                    article.category,
//                    article.isRecruitment,
//                    article.))
//            .from(article)
//            .offset(pageable.getOffset())
//            .limit(pageable.getPageSize() + 1)
//            .orderBy(article.id.desc())
//            .where()
//            .fetchResults();

    return null;
  }
}
