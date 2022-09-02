package sparta.seed.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sparta.seed.util.Timestamped;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
public class Campaign extends Timestamped {
  //PK
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String thumbnail;
  //제목
  private String title;
  //이미지
  @OneToMany(mappedBy = "campaign", cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
  @JsonManagedReference
  private List<Img> imgList = new ArrayList<>();

  @Builder
  public Campaign(String title) {
    this.title = title;
  }

  public void setThumbnail(String thumbnail) {
    this.thumbnail = thumbnail;
  }
  public void addImg(Img img){
    this.imgList.add(img);
  }
}
