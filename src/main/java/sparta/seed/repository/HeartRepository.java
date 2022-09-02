package sparta.seed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.seed.domain.Heart;
import sparta.seed.domain.Replay;

public interface HeartRepository extends JpaRepository<Heart,Long> {
	Boolean existsByReplayAndMemberId (Replay replay, Long memberId);
	Heart findByReplayAndMemberId (Replay replay, Long memberId);
}
