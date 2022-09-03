package sparta.seed.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
  private String fileName;

  @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
  @JsonBackReference
  @JoinColumn(name = "CommunityId")
  private Community community;

  @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
  @JsonBackReference
  @JoinColumn(name = "proofId")
  private Proof proof;

  @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
  @JsonBackReference
  @JoinColumn(name = "campaignId")
  private Campaign campaign;

  @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
  @JsonBackReference
  @JoinColumn(name = "commentId")
  private Comment comment;

  @Builder
  public Img(Long id, String imgUrl, String fileName, Community community, Proof proof, Campaign campaign, Comment comment) {
    this.id = id;
    this.imgUrl = imgUrl;
    this.fileName = fileName;
    this.community = community;
    this.proof = proof;
    this.campaign = campaign;
    this.comment = comment;
  }
}
