package sparta.seed.domain.dto.responseDto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import sparta.seed.domain.Article;
import sparta.seed.domain.Img;

import java.util.List;

@Getter
@Setter
public class ArticleResponseDto {

  private Long articleId;
  private List<Img> imgList;
  private String title;
  //모집여부
  private boolean isRecruitment;
  private List<Article> participantsList;
  //달성도
  private long participantsPer;
  //참가인원
  private Integer participantsCnt;

  @QueryProjection
  @Builder
  public ArticleResponseDto(Long articleId, List<Img> imgList, String title, boolean isRecruitment, List<Article> participantsList, long participantsPer, Integer participantsCnt) {
    this.articleId = articleId;
    this.imgList = imgList;
    this.title = title;
    this.isRecruitment = isRecruitment;
    this.participantsList = participantsList;
    this.participantsPer = participantsPer;
    this.participantsCnt = participantsCnt;
  }
}
