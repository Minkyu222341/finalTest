package sparta.seed.repository.customrepository;

import com.querydsl.core.QueryResults;
import org.springframework.data.domain.Pageable;
import sparta.seed.domain.Community;
import sparta.seed.domain.dto.responseDto.CommunitySearchCondition;

public interface CommunityRepositoryCustom {
  QueryResults<Community> getAllCommunity(Pageable pageable, CommunitySearchCondition condition);

}
