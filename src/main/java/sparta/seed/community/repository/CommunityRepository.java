package sparta.seed.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.seed.community.domain.Community;
import sparta.seed.community.repository.customrepository.CommunityRepositoryCustom;

import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, Long>, CommunityRepositoryCustom {
  List<Community> findByMemberId(Long id);


}
