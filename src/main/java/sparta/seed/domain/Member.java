package sparta.seed.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sparta.seed.domain.dto.requestDto.SocialMemberRequestDto;
import sparta.seed.util.Timestamped;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Member extends Timestamped {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String username;

  private String password;

  private String nickname;

  private String socialId;

  @Enumerated(EnumType.STRING)
  private Authority authority;

  private String profileImage;

  private Integer level;
  private Integer exp;

  @Builder
  public Member(Long id, String username, String password, String nickname, String socialId, Authority authority, String profileImage, Integer level, Integer exp) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.nickname = nickname;
    this.socialId = socialId;
    this.authority = authority;
    this.profileImage = profileImage;
    this.level = level;
    this.exp = exp;
  }

  public void updateNickname(SocialMemberRequestDto requestDto) {
    nickname = requestDto.getNickname();
  }
}