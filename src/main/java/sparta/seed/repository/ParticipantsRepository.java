package sparta.seed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.seed.domain.Article;
import sparta.seed.domain.Participants;

public interface ParticipantsRepository extends JpaRepository<Participants,Long> {
  Boolean existsByArticleAndMemberId(Article article, Long memberId);
}
