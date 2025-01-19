package depth.main.wishwesee.domain.vote.domain;

import depth.main.wishwesee.domain.user.domain.User;
import depth.main.wishwesee.domain.vote.domain.ScheduleVote;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoterNickname {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "voter_nickname_id")
    private Long id;

    private String nickname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_vote_id")
    private ScheduleVote scheduleVote;

}

