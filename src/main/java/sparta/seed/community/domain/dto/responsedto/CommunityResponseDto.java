package sparta.seed.community.domain.dto.responsedto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import sparta.seed.community.domain.Community;
import sparta.seed.img.domain.Img;

import java.util.List;

@Getter
@Setter
public class CommunityResponseDto {

  private Long communityId;
  private Img img;
  private String title;
  //모집여부
  private boolean participant;
  private List<Community> participantsList;
  //목표 달성 횟수
  private long limitScore;
  //참가인원 제한
  private long limitParticipants;
  //달성퍼센트
  private double successPercent;
  //참여퍼센트
  private double currentPercent;
  //참가인원
  private Integer participantsCnt;
  //사용자 닉네임
  private String nickname;
  //모임 시작날짜
  private String startDate;
  //모임 종료날짜
  private String endDate;
  //비밀글 여부
  private boolean secret;
  //패스워드
  private String password;
  //내용
  private String content;
  //캠페인 진행여부
  private String dateStatus;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
  private String createAt;
  private boolean writer;





  @QueryProjection
  @Builder
  public CommunityResponseDto(Long communityId, Img img, String title, boolean participant, List<Community> participantsList, long limitScore, long limitParticipants, double successPercent, double currentPercent, Integer participantsCnt, String nickname, String startDate, String endDate, boolean secret, String password, String content, String dateStatus, String createAt, boolean writer) {
    this.communityId = communityId;
    this.img = img;
    this.title = title;
    this.participant = participant;
    this.participantsList = participantsList;
    this.limitScore = limitScore;
    this.limitParticipants = limitParticipants;
    this.successPercent = successPercent;
    this.currentPercent = currentPercent;
    this.participantsCnt = participantsCnt;
    this.nickname = nickname;
    this.startDate = startDate;
    this.endDate = endDate;
    this.secret = secret;
    this.password = password;
    this.content = content;
    this.dateStatus = dateStatus;
    this.createAt = createAt;
    this.writer = writer;
  }
}
