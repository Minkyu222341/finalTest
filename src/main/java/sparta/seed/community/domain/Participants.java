package sparta.seed.community.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Participants {
  //PK
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JsonBackReference
  private Community community;
  private Long memberId;
  private String nickname;

  @Builder
  public Participants(Community community, Long memberId,String nickname) {
    this.community = community;
    this.memberId = memberId;
    this.nickname = nickname;
  }
}
