package sparta.seed.domain;

import lombok.NoArgsConstructor;
import sparta.seed.util.Timestamped;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
public class Mission extends Timestamped {
  //PK
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  //미션내용
  private String content;
}
