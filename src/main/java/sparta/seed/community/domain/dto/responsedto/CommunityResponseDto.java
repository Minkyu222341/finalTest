package sparta.seed.community.domain.dto.responsedto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import sparta.seed.community.domain.Participants;
import sparta.seed.img.domain.Img;

import java.util.List;

@Getter
@Setter
public class CommunityResponseDto {

  private Long communityId;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
  private String createAt;
  private String nickname;
  private String title;
  private String content;
  private Img img;
  //모임 참가자
  private List<Participants> participantsList;
  private Integer participantsCnt;
  private long limitParticipants;
  //참여 퍼센트
  private double currentPercent;
  //로그인한 사용자가 현재 모임에 참여했는지 여부
  private boolean participant;
  //목표 인증글 수
  private long limitScore;
  //인증 퍼센트
  private double successPercent;
  private String startDate;
  private String endDate;
  //진행 여부
  private String dateStatus;
  private boolean secret;
  private String password;
  private boolean writer;

  @QueryProjection
  @Builder
  public CommunityResponseDto(Long communityId, Img img, String title, boolean participant, List<Participants> participantsList, long limitScore, long limitParticipants, double successPercent, double currentPercent, Integer participantsCnt, String nickname, String startDate, String endDate, boolean secret, String password, String content, String dateStatus, String createAt, boolean writer) {
    this.communityId = communityId;
    this.createAt = createAt;
    this.nickname = nickname;
    this.title = title;
    this.content = content;
    this.img = img;
    this.participantsList = participantsList;
    this.participantsCnt = participantsCnt;
    this.limitParticipants = limitParticipants;
    this.currentPercent = currentPercent;
    this.participant = participant;
    this.limitScore = limitScore;
    this.successPercent = successPercent;
    this.startDate = startDate;
    this.endDate = endDate;
    this.dateStatus = dateStatus;
    this.secret = secret;
    this.password = password;
    this.writer = writer;
  }
}
