package sparta.seed.domain.dto.requestDto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CommunityRequestDto {
  private String startDate;
  private String endDate;
  private Integer limitScore;
  private Integer limitParticipants;
  private boolean isSecret;
  private String password;
  private String title;
  private String content;
}
