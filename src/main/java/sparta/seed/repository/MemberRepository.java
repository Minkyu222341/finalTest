package sparta.seed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.seed.domain.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {

  Optional<Member> findByUsername(String username);

  Optional<Member> findBySocialId(String id);

  Boolean existsByNickname(String nickname);

  Optional<Member> findByNickname(String nickname);
}
