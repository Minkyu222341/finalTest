package sparta.seed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.seed.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment,Long> {
}
