package sparta.seed.domain;

import lombok.NoArgsConstructor;
import sparta.seed.util.Timestamped;

import javax.persistence.*;

@Entity
@NoArgsConstructor
public class Comment extends Timestamped {
  //PK
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  //닉네임
  private String nickname;
  //작성한 유저의 Pk
  private Long memberId;
  //내용
  private String content;
  //인증글
  @ManyToOne
  private Replay replay;

}
