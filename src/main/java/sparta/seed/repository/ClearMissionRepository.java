package sparta.seed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.seed.domain.ClearMission;
import sparta.seed.repository.customrepository.ClearMissionRepositoryCustom;
import java.time.LocalDate;
import java.util.List;

public interface ClearMissionRepository extends JpaRepository<ClearMission,Long> , ClearMissionRepositoryCustom {

	int countAllByMemberId(Long memberId);
	List<ClearMission> findAllByMemberIdAndCreatedAt(Long memberid, LocalDate targetDay);
}
