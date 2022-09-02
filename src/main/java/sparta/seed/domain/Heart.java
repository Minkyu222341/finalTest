package sparta.seed.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Heart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "replayId")
    private Replay replay;
    //누른 유저의 Pk
    private Long memberId;

    @Builder
    public Heart(Long id, Replay replay, Long memberId) {
        this.id = id;
        this.replay = replay;
        this.memberId = memberId;
    }


}
