package sparta.seed.community.repository.customrepository;


import sparta.seed.community.domain.Community;

public interface ProofRepositoryCustom {
  Long getCertifiedProof(Community community);
}
