package sparta.seed.repository.customrepository;

import sparta.seed.domain.Community;

public interface ProofRepositoryCustom {
  Long getCertifiedProof(Community community);
}
