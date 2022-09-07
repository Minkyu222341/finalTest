package sparta.seed.img.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import sparta.seed.community.domain.Comment;
import sparta.seed.img.domain.Img;

public interface
ImgRepository extends JpaRepository<Img,Long> {
	Img findByComment(Comment comment);
}
