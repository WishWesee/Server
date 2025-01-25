package depth.main.wishwesee.domain.invitation.domain.repository;

import depth.main.wishwesee.domain.invitation.domain.Feedback;
import depth.main.wishwesee.domain.invitation.domain.Invitation;
import depth.main.wishwesee.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByInvitationOrderByCreatedDateDesc(Invitation invitation);

    int countByInvitation(Invitation invitation);

    void deleteByInvitation(Invitation invitation);

    boolean existsByInvitationAndUser(Invitation invitation, User user);
}
