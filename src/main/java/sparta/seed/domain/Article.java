package sparta.seed.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import sparta.seed.util.Timestamped;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
public class Article extends Timestamped {
  //PK
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  //제목
  private String title;
  //작성자
  private String nickname;
  //작성한 유저의 PK
  private Long memberId;
  //내용
  private String content;
  //모집시작일
  private Timestamp startRecruitment;
  //모집마감일
  private Timestamp endRecruitment;
  //캠페인시작일
  private Timestamp startDate;
  //캠페인마감일
  private Timestamp endDate;
  //목표 달성 횟수
  private long limitScore;
  //참가인원 제한
  private long limitParticipants;
  //비밀글여부
  private boolean isSecret;
  //글비밀번호
  private String password;
  //카테고리
  private String category;
  //모집여부
  @ColumnDefault("true")
  private boolean isRecruitment;
  //좋아요
  @OneToMany(mappedBy = "article", cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
  @JsonManagedReference
  private List<Heart> heartList = new ArrayList<>();
  @OneToMany(mappedBy = "article", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  @JsonManagedReference
  private List<Replay> replayList = new ArrayList<>();
  @OneToMany(mappedBy = "article", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  @JsonManagedReference
  private List<Img> imgList = new ArrayList<>();

  @Builder
  public Article(Long id, String title, String nickname, Long memberId, String content, Timestamp startRecruitment, Timestamp endRecruitment, Timestamp startDate, Timestamp endDate, long limitScore, long limitParticipants, boolean isSecret, String password, String category, boolean isRecruitment, List<Heart> heartList, List<Replay> replayList, List<Img> imgList) {
    this.id = id;
    this.title = title;
    this.nickname = nickname;
    this.memberId = memberId;
    this.content = content;
    this.startRecruitment = startRecruitment;
    this.endRecruitment = endRecruitment;
    this.startDate = startDate;
    this.endDate = endDate;
    this.limitScore = limitScore;
    this.limitParticipants = limitParticipants;
    this.isSecret = isSecret;
    this.password = password;
    this.category = category;
    this.isRecruitment = isRecruitment;
    this.heartList = heartList;
    this.replayList = replayList;
    this.imgList = imgList;
  }
}
