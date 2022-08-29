package sparta.seed.domain.dto.requestDto;

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

    @Builder
    public SocialMemberRequestDto(String socialId, String nickname, String username, String profileImage) {
        this.socialId = socialId;
        this.nickname = nickname;
        this.username = username;
        this.profileImage = profileImage;
    }
}