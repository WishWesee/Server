package depth.main.wishwesee.domain.vote.domain;

import depth.main.wishwesee.domain.invitation.domain.Invitation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ScheduleVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_vote_id")
    private Long id;

    private LocalDate startDate;

    private LocalTime startTime;

    private LocalDate endDate;

    private LocalTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitation_id")
    //@OnDelete(action = OnDeleteAction.CASCADE)
    private Invitation invitation;

    @Builder
    public ScheduleVote(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime, Invitation invitation){
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.invitation = invitation;
    }

}