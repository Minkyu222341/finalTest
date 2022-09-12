package sparta.seed.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.seed.community.domain.Heart;
import sparta.seed.community.domain.Proof;

public interface HeartRepository extends JpaRepository<Heart,Long> {
	Boolean existsByProofAndMemberId (Proof proof, Long memberId);
	Heart findByProofAndMemberId (Proof proof, Long memberId);
}
