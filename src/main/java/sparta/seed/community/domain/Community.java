package sparta.seed.community.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import sparta.seed.community.domain.dto.requestdto.CommunityRequestDto;
import sparta.seed.img.domain.Img;
import sparta.seed.util.Timestamped;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class Community extends Timestamped {
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
  //캠페인시작일
  private String startDate;
  //캠페인마감일
  private String endDate;
  //목표 달성 횟수
  private long limitScore;
  //참가인원 제한
  private long limitParticipants;
  //비밀글여부

  private boolean passwordFlag;
  //글비밀번호
  private String password;
  //모집여부
  @ColumnDefault("true")
  private boolean recruitment;
  @OneToMany(mappedBy = "community", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  @JsonManagedReference
  private List<Proof> proofList = new ArrayList<>();
  @OneToOne(mappedBy = "community", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  @JsonManagedReference
  private Img img;

  @OneToMany(mappedBy = "community", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  @JsonManagedReference
  private List<Participants> participantsList = new ArrayList<>();


  @Builder
  public Community(Long id, String title, String nickname, Long memberId, String content, String startDate, String endDate, long limitScore, long limitParticipants, boolean secret, String password, boolean recruitment, List<Proof> proofList, Img img, List<Participants> participantsList) {
    this.id = id;
    this.title = title;
    this.nickname = nickname;
    this.memberId = memberId;
    this.content = content;
    this.startDate = startDate;
    this.endDate = endDate;
    this.limitScore = limitScore;
    this.limitParticipants = limitParticipants;
    this.passwordFlag = secret;
    this.password = password;
    this.recruitment = recruitment;
    this.proofList = proofList;
    this.img = img;
    this.participantsList = participantsList;
  }

  public void update(CommunityRequestDto requestDto) {
    this.startDate = requestDto.getStartDate();
    this.endDate = requestDto.getEndDate();
    this.limitScore = requestDto.getLimitScore();
    this.limitParticipants = requestDto.getLimitParticipants();
    this.passwordFlag = requestDto.isSecret();
    this.password = requestDto.getPassword();
    this.title = requestDto.getTitle();
    this.content = requestDto.getContent();
  }

  public void addParticipant(Participants participants) {
    participantsList.add(participants);
  }

  public void setImg(Img img) {
    this.img = img;
  }


}