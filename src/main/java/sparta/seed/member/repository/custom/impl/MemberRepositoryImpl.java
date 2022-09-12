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

  public Member secondVerification(String reissueTokenValue) { // 전달받은 리프레쉬토큰 값
    JPAQueryFactory queryFactory = new JPAQueryFactory(em);
    return queryFactory.select(member)
            .from(member)
            .leftJoin(refreshToken) // 멤버에 리프레쉬토큰을 레프트조인 
            .on(member.id.eq(refreshToken.refreshKey))              // 멤버테이블에 있는 PK와 리프레쉬토큰테이블에있는 key값 중 겹치는걸 다 가져오고 ,
            .where(refreshToken.refreshValue.eq(reissueTokenValue))// 그중에 리프레쉬토큰 테이블에있는 value와 입력받은 토큰값과 겹치는 걸 가져와라
            .fetchOne();                                           // 멤버 10명중 5명이 리프레쉬토큰이 만료되어 멤버테이블10개 리프레쉬토큰 테이블5개 있는상황 ->
                                                                    // PK와 key값이 겹치는 5개가 찾아와짐 -> 그중 value값이 겹치는 1개만 찾아짐 .
                                                                    // fetchOne -> 값이 2개이상이면 오류. 무조건 1개만 리턴
  }
}
