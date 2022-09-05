package sparta.seed.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sparta.seed.util.Timestamped;

import javax.persistence.*;
import java.time.LocalDate;


@Getter
@NoArgsConstructor
@Entity
public class ClearMission{
  //PK
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  //완료한 유저의 Pk
  private LocalDate createdAt;
  private Long memberId;
  //완료한 미션
  private String content;

  @Builder
  public ClearMission(LocalDate createdAt, Long memberId, String content) {
    this.createdAt = createdAt;
    this.memberId = memberId;
    this.content = content;
  }
}
