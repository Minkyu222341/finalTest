package sparta.seed.community.repository.customrepository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import sparta.seed.community.domain.Community;
import sparta.seed.community.repository.customrepository.ProofRepositoryCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static sparta.seed.community.domain.QCommunity.community;
import static sparta.seed.community.domain.QProof.proof;

public class ProofRepositoryImpl implements ProofRepositoryCustom {
  @PersistenceContext
  EntityManager em;

  @Override
  public Long getCertifiedProof(Community find) {
    JPAQueryFactory queryFactory = new JPAQueryFactory(em);
    long result = queryFactory.selectFrom(proof)
            .leftJoin(proof.community, community)
            .where(proof.heartList.size().goe(community.limitParticipants.divide(2)).and(proof.community.eq(find)))
            .fetchCount();
    return result;
  }


}
