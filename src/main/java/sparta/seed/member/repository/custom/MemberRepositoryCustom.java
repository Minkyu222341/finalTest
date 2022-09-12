package sparta.seed.member.repository.custom;

import sparta.seed.member.domain.Member;

public interface MemberRepositoryCustom {
  Member secondVerification(String reissueTokenValue);
}
