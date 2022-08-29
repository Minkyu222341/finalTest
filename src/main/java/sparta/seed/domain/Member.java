package sparta.seed.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Member {

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


  @Builder
  public Member(Long id, String username, String password, String nickname, String socialId, Authority authority, String profileImage) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.nickname = nickname;
    this.socialId = socialId;
    this.authority = authority;
    this.profileImage = profileImage;
  }
}