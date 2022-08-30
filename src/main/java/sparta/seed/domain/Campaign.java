package sparta.seed.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import sparta.seed.util.Timestamped;

import javax.persistence.*;
import java.util.List;

@Entity
public class Campaign extends Timestamped {
  //PK
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  //제목
  private String title;
  //이미지
  @OneToMany(mappedBy = "campaign", cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
  @JsonManagedReference
  private List<Img> imgList;
}
