package sparta.seed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.seed.domain.Comment;
import sparta.seed.domain.Img;

public interface ImgRepository extends JpaRepository<Img,Long> {
	Img findByComment(Comment comment);
}
