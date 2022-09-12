package sparta.seed.member.domain.dto.responsedto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberResponseDto {
    private Long id;
    private String nickname;
    private String username;
    @Builder
    public MemberResponseDto(Long id, String nickname, String username) {
        this.id = id;
        this.nickname = nickname;
        this.username = username;
    }
}

