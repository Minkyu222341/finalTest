package sparta.seed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.seed.domain.Article;

public interface ArticleRepository extends JpaRepository<Article,Long> {
}
