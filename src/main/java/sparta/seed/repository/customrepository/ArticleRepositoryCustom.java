package sparta.seed.repository.customrepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import sparta.seed.domain.dto.responseDto.ArticleResponseDto;
import sparta.seed.domain.dto.responseDto.ArticleSearchCondition;

public interface ArticleRepositoryCustom {
  Slice<ArticleResponseDto> getAllArticle(Pageable pageable, ArticleSearchCondition condition);
}
