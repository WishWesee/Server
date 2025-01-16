package depth.main.wishwesee.domain.vote.domain.repository;

import depth.main.wishwesee.domain.invitation.domain.Invitation;
import depth.main.wishwesee.domain.user.domain.User;
import depth.main.wishwesee.domain.vote.domain.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    boolean existsByInvitationAndNickname(Invitation invitation, String nickname);

    Optional<Attendance> findByInvitationAndNickname(Invitation invitation, String nickname);

    Attendance findByInvitationAndUser(Invitation invitation, User user);
}
