package sparta.seed.domain;

import lombok.NoArgsConstructor;
import sparta.seed.util.Timestamped;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
public class Replay extends Timestamped {
  //PK
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;



  //댓글리스트
  @OneToMany(mappedBy = "replay",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
  private List<Comment> commentList;
}
