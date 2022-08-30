package sparta.seed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.seed.domain.Article;
import sparta.seed.repository.customrepository.ArticleRepositoryCustom;

public interface ArticleRepository extends JpaRepository<Article,Long> , ArticleRepositoryCustom {
}
