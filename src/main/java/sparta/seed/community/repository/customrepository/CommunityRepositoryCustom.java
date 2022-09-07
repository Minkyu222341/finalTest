package sparta.seed.community.repository.customrepository;

import com.querydsl.core.QueryResults;
import org.springframework.data.domain.Pageable;
import sparta.seed.community.domain.Community;
import sparta.seed.community.domain.dto.requestdto.CommunitySearchCondition;

public interface CommunityRepositoryCustom {
  QueryResults<Community> getAllCommunity(Pageable pageable, CommunitySearchCondition condition);

}
