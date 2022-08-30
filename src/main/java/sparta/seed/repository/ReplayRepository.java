package sparta.seed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.seed.domain.Replay;

public interface ReplayRepository extends JpaRepository<Replay,Long> {
}
