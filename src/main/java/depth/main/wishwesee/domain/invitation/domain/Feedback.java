package depth.main.wishwesee.domain.invitation.domain;

import depth.main.wishwesee.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feedback extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long id;

    private String image;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_invitation_id")
    private ReceivedInvitation receivedInvitation;

    @Builder
    public Feedback(String image, String content, ReceivedInvitation receivedInvitation) {
        this.image = image;
        this.content = content;
        this.receivedInvitation = receivedInvitation;
    }
}
