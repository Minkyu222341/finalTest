package sparta.seed.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.seed.member.domain.Member;
import sparta.seed.member.repository.custom.MemberRepositoryCustom;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> , MemberRepositoryCustom {

  Optional<Member> findByUsername(String username);

  Optional<Member> findBySocialId(String id);
  Boolean existsByNickname(String nickname);
}
