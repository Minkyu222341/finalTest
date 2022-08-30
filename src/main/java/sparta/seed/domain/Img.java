package sparta.seed.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.NoArgsConstructor;
import sparta.seed.util.Timestamped;

import javax.persistence.*;

@Entity
@NoArgsConstructor
public class Img extends Timestamped {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String imgUrl;
  private String fileName;

  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "articleId")
  private Article article;

  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "replayId")
  private Replay replay;

  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "campaignId")
  private Campaign campaign;


}
