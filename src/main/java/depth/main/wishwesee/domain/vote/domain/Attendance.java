package depth.main.wishwesee.domain.vote.domain;

import depth.main.wishwesee.domain.invitation.domain.Invitation;
import depth.main.wishwesee.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long id;

    private String nickname;

    @Column(name = "is_attending")
    private Boolean attending;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitation_id")
    private Invitation invitation;

    @Builder
    public Attendance(String nickname, boolean attending, User user, Invitation invitation) {
        this.nickname = nickname;
        this.attending = attending;
        this.user = user;
        this.invitation = invitation;
    }

    public void updateAttending(boolean attending) {
        this.attending = attending;
    }
}