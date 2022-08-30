package sparta.seed.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Builder;
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

  @Builder
  public Img(Long id, String imgUrl, String fileName, Article article, Replay replay, Campaign campaign) {
    this.id = id;
    this.imgUrl = imgUrl;
    this.fileName = fileName;
    this.article = article;
    this.replay = replay;
    this.campaign = campaign;
  }
}
