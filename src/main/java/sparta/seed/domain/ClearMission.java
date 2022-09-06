package sparta.seed.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
  private String weekOfMonth;

  @Builder
  public ClearMission(LocalDate createdAt, Long memberId, String content,String weekOfMonth) {
    this.createdAt = createdAt;
    this.memberId = memberId;
    this.content = content;
    this.weekOfMonth = weekOfMonth;
  }
}
