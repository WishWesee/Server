package depth.main.wishwesee.domain.invitation.domain.repository;

import depth.main.wishwesee.domain.invitation.domain.Invitation;
import depth.main.wishwesee.domain.invitation.domain.ReceivedInvitation;
import depth.main.wishwesee.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReceivedInvitationRepository extends JpaRepository<ReceivedInvitation, Long> {
    Optional<ReceivedInvitation> findByInvitation(Invitation invitation);

    boolean existsByInvitationAndReceiver(Invitation invitation, User user);
    boolean existsByReceiverAndInvitation(User receiver, Invitation invitation);
}
