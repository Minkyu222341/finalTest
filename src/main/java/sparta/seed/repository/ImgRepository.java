package sparta.seed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.seed.domain.Img;

public interface ImgRepository extends JpaRepository<Img,Long> {
}
