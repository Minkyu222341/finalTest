package sparta.seed.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.seed.community.domain.Community;
import sparta.seed.community.domain.Participants;

public interface ParticipantsRepository extends JpaRepository<Participants,Long> {
  Boolean existsByCommunityAndMemberId(Community Community, Long memberId);
  Long deleteByMemberId(Long memberId);
}
