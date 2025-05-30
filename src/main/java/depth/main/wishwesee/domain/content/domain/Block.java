package depth.main.wishwesee.domain.content.domain;

import depth.main.wishwesee.domain.invitation.domain.Invitation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
public class Block {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "block_id")
    private Long id;

    private int sequence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitation_id")
    //@OnDelete(action = OnDeleteAction.CASCADE)
    private Invitation invitation;

    protected Block(int sequence, Invitation invitation) {
        this.sequence = sequence;
        this.invitation = invitation;
    }
}

