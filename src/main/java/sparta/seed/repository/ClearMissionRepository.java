package sparta.seed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.seed.domain.ClearMission;
import sparta.seed.repository.customrepository.ClearMissionRepositoryCustom;

public interface ClearMissionRepository extends JpaRepository<ClearMission,Long> , ClearMissionRepositoryCustom {
	int countAllByMemberId(Long memberId);
}
