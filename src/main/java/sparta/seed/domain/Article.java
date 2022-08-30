package sparta.seed.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import sparta.seed.util.Timestamped;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
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
  //인증주기
  //인증횟수
  //비밀글여부
  private boolean isSecret;
  //글비밀번호
  private String password;
  //카테고리
  private String category;
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

}
