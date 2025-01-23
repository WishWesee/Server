package depth.main.wishwesee.domain.invitation.domain;

import depth.main.wishwesee.domain.common.BaseEntity;
import depth.main.wishwesee.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReceivedInvitation extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "received_invitation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitation_id")
    //@OnDelete(action = OnDeleteAction.CASCADE)
    private Invitation invitation;

    @Builder
    ReceivedInvitation(User receiver, Invitation invitation){
        this.receiver = receiver;
        this.invitation = invitation;
    }


}
