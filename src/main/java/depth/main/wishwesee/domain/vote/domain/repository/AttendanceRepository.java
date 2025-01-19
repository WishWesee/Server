package depth.main.wishwesee.domain.vote.domain.repository;

import depth.main.wishwesee.domain.vote.domain.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    int countByInvitationIdAndAttending(Long invitationId, boolean attending);
}
