package depth.main.wishwesee.domain.invitation.domain.repository;

import depth.main.wishwesee.domain.invitation.domain.Feedback;
import depth.main.wishwesee.domain.invitation.domain.Invitation;
import depth.main.wishwesee.domain.invitation.domain.ReceivedInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByInvitationOrderByCreatedDateDesc(Invitation invitation);

    int countByInvitation(Invitation invitation);

    void deleteByInvitation(Invitation invitation);
}
