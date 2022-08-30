package sparta.seed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.seed.domain.Heart;

public interface HeartRepository extends JpaRepository<Heart,Long> {
}
