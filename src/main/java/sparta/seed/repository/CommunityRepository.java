package sparta.seed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.seed.domain.Community;
import sparta.seed.repository.customrepository.CommunityRepositoryCustom;

import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, Long>, CommunityRepositoryCustom {
  List<Community> findByMemberId(Long id);
}
