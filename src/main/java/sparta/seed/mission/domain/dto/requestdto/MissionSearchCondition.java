package sparta.seed.mission.domain.dto.requestdto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class MissionSearchCondition {

  private String startDate;
  private String endDate;

  @QueryProjection
  public MissionSearchCondition(String startDate, String endDate) {
    this.startDate = startDate;
    this.endDate = endDate;
  }
}
