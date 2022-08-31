package sparta.seed.domain;

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
  private Article article;

  private Long memberId;

  @Builder
  public Participants(Long id, Article article, Long memberId) {
    this.id = id;
    this.article = article;
    this.memberId = memberId;
  }
}
