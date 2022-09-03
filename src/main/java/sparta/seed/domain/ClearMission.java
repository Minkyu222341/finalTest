package sparta.seed.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sparta.seed.util.Timestamped;

import javax.persistence.*;


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

  @Builder
  public ClearMission(Long memberId, String content) {
    this.memberId = memberId;
    this.content = content;
  }
}
