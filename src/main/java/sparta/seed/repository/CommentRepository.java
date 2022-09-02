package sparta.seed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.seed.domain.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
	List<Comment> findAllByReplay_Id(Long replayId);
}
