package depth.main.wishwesee.domain.invitation.domain.repository;

import depth.main.wishwesee.domain.invitation.domain.Feedback;
import depth.main.wishwesee.domain.invitation.domain.ReceivedInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByReceivedInvitationOrderByCreatedDateDesc(ReceivedInvitation receivedInvitation);

    int countByReceivedInvitation(ReceivedInvitation receivedInvitation);
}
