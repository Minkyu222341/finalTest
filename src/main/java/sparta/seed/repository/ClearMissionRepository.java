package sparta.seed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.seed.domain.ClearMission;

import java.time.LocalDate;
import java.util.List;

public interface ClearMissionRepository extends JpaRepository<ClearMission,Long> {
	int countAllByMemberId(Long memberId);
	List<ClearMission> findAllByMemberIdAndCreatedAt(Long memberid, LocalDate targetDay);
}
