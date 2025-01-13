package depth.main.wishwesee.domain.invitation.domain;

import depth.main.wishwesee.domain.common.BaseEntity;
import depth.main.wishwesee.domain.user.domain.User;
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
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitation_id")
    private Invitation invitation;

    @Builder
    public Feedback(String image, String content, User user, Invitation invitation) {
        this.image = image;
        this.content = content;
        this.user = user;
        this.invitation = invitation;
    }
}
