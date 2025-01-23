package depth.main.wishwesee.domain.vote.domain;

import depth.main.wishwesee.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleVoter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_voter_id")
    private Long id;

    private String nickname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_vote_id")
    //@OnDelete(action = OnDeleteAction.CASCADE)
    private ScheduleVote scheduleVote;

    @Builder
    public ScheduleVoter(String nickname, User user, ScheduleVote scheduleVote) {
        this.nickname = nickname;
        this.user = user;
        this.scheduleVote = scheduleVote;
    }

}

