package sparta.seed.img.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sparta.seed.campaign.domain.Campaign;
import sparta.seed.community.domain.Proof;
import sparta.seed.util.Timestamped;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class Img extends Timestamped {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String imgUrl;

  @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.PERSIST)
  @JsonBackReference
  @JoinColumn(name = "proofId")
  private Proof proof;

  @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.PERSIST)
  @JsonBackReference
  @JoinColumn(name = "campaignId")
  private Campaign campaign;

  @Builder
  public Img(Long id, String imgUrl, Proof proof, Campaign campaign) {
    this.id = id;
    this.imgUrl = imgUrl;
    this.proof = proof;
    this.campaign = campaign;
  }
}
