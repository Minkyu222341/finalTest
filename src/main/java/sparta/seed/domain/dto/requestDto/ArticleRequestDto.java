package sparta.seed.domain.dto.requestDto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@NoArgsConstructor
@Getter
public class ArticleRequestDto {
  private Timestamp startRecruitment;
  private Timestamp endRecruitment;
  private Timestamp startDate;
  private Timestamp endDate;

  private Integer limitScore;
  private Integer limitParticipants;
  private boolean isSecret;
  private String password;
  private String title;
  private String content;
  private String category;
}
