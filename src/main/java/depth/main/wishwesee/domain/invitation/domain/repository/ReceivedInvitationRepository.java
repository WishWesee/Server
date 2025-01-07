package depth.main.wishwesee.domain.invitation.domain.repository;

import depth.main.wishwesee.domain.invitation.domain.Invitation;
import depth.main.wishwesee.domain.invitation.domain.ReceivedInvitation;
import depth.main.wishwesee.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceivedInvitationRepository extends JpaRepository<ReceivedInvitation, Long> {
    boolean existsByReceiverAndInvitation(User receiver, Invitation invitation);
}
