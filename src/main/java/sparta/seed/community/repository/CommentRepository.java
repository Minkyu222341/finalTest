package sparta.seed.community.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import sparta.seed.community.domain.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
	List<Comment> findAllByProof_Id(Long replayId);
}