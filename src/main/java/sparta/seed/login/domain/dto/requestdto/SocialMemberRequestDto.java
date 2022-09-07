package sparta.seed.login.domain.dto.requestdto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SocialMemberRequestDto {
    private String socialId;
    private String nickname;
    private String username;
    private String profileImage;
    private Integer level;
    private Integer exp;

    @Builder
    public SocialMemberRequestDto(String socialId, String nickname, String username, String profileImage, Integer level, Integer exp) {
        this.socialId = socialId;
        this.nickname = nickname;
        this.username = username;
        this.profileImage = profileImage;
        this.level = level;
        this.exp = exp;
    }
}