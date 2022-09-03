package sparta.seed.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sparta.seed.domain.Proof;

import java.util.List;

public interface ProofRepository extends JpaRepository<Proof,Long> {
	Page<Proof> findAllByCommunity_Id (Long communityId, Pageable pageable);
	List<Proof> findAllByCommunity_Id (Long communityId);
}
