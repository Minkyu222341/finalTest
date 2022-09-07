package sparta.seed.mission.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import sparta.seed.mission.domain.Mission;

public interface MissionRepository extends JpaRepository<Mission,Long> {
}
