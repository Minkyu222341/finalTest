package sparta.seed.community.domain.dto.requestdto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CommunityRequestDto {
  private String startDate;
  private String endDate;
  private Integer limitScore;
  private Integer limitParticipants;
  private boolean secret;
  private String password;
  private String title;
  private String content;
}
