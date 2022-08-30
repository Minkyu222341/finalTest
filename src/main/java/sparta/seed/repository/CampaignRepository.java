package sparta.seed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.seed.domain.Campaign;

public interface CampaignRepository extends JpaRepository<Campaign,Long> {
}
