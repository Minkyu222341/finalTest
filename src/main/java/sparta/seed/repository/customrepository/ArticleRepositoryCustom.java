package sparta.seed.repository.customrepository;

import com.querydsl.core.QueryResults;
import org.springframework.data.domain.Pageable;
import sparta.seed.domain.Article;
import sparta.seed.domain.dto.responseDto.ArticleSearchCondition;

public interface ArticleRepositoryCustom {
  QueryResults<Article> getAllArticle(Pageable pageable, ArticleSearchCondition condition);
}
