package sparta.seed.mission.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sparta.seed.util.Timestamped;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Getter
@NoArgsConstructor
@Entity
public class ClearMission extends Timestamped {
  //PK
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  //완료한 유저의 Pk
  private Long memberId;
  //완료한 미션
  private String content;
  private String weekOfMonth;

  @Builder
  public ClearMission(Long memberId, String content, String weekOfMonth) {
    this.memberId = memberId;
    this.content = content;
    this.weekOfMonth = weekOfMonth;
  }
}
