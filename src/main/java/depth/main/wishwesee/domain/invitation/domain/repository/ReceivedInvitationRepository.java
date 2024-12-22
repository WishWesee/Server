package depth.main.wishwesee.domain.invitation.domain.repository;

import depth.main.wishwesee.domain.invitation.domain.ReceivedInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceivedInvitationRepository extends JpaRepository<ReceivedInvitation, Long> {
}
