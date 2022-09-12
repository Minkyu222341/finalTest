package sparta.seed.member.repository.custom.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import sparta.seed.member.domain.Member;
import sparta.seed.member.repository.custom.MemberRepositoryCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static sparta.seed.login.domain.QRefreshToken.refreshToken;
import static sparta.seed.member.domain.QMember.member;

public class MemberRepositoryImpl implements MemberRepositoryCustom {
  @PersistenceContext
  EntityManager em;

  public Member secondVerification(String reissueTokenValue) {
    JPAQueryFactory queryFactory = new JPAQueryFactory(em);
    return queryFactory.select(member)
            .from(member)
            .leftJoin(refreshToken)
            .on(member.id.eq(refreshToken.refreshKey), refreshToken.refreshValue.eq(reissueTokenValue))
            .fetchOne();
  }
}
