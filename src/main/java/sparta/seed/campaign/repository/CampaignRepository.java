package sparta.seed.campaign.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import sparta.seed.campaign.domain.Campaign;

public interface CampaignRepository extends JpaRepository<Campaign,Long> {
}
