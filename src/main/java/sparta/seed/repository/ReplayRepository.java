package sparta.seed.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sparta.seed.domain.Replay;

import java.util.List;

public interface ReplayRepository extends JpaRepository<Replay,Long> {
	Page<Replay> findAllByArticle_Id (Long articleId, Pageable pageable);
	List<Replay> findAllByArticle_Id (Long articleId);
}
