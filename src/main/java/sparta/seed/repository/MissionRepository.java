package sparta.seed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.seed.domain.Mission;

public interface MissionRepository extends JpaRepository<Mission,Long> {
}
