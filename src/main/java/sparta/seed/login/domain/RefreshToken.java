package sparta.seed.login.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Getter
@NoArgsConstructor
@Table(name = "refresh_token")
@Entity
public class RefreshToken {

  @Id
  private Long refreshKey;
  private String refreshValue;

  public RefreshToken updateValue(String token) {
    this.refreshValue = token;
    return this;
  }

  @Builder
  public RefreshToken(Long refreshKey, String refreshValue) {
    this.refreshKey = refreshKey;
    this.refreshValue = refreshValue;
  }
}
