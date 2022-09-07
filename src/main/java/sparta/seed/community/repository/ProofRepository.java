package sparta.seed.community.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sparta.seed.community.domain.Proof;
import sparta.seed.community.repository.customrepository.ProofRepositoryCustom;

import java.util.List;

public interface ProofRepository extends JpaRepository<Proof,Long>, ProofRepositoryCustom {
	Page<Proof> findAllByCommunity_Id (Long communityId, Pageable pageable);
	List<Proof> findAllByCommunity_Id (Long communityId);
}
