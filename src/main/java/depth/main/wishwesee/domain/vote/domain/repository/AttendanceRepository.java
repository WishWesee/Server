package depth.main.wishwesee.domain.vote.domain.repository;

import depth.main.wishwesee.domain.invitation.domain.Invitation;
import depth.main.wishwesee.domain.user.domain.User;
import depth.main.wishwesee.domain.vote.domain.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByInvitationAndUser(Invitation invitation, User user);

    int countByInvitationAndAttending(Invitation invitation, boolean b);

    @Query("SELECT a.nickname FROM Attendance a WHERE a.invitation = :invitation AND a.attending = :isAttend ORDER BY a.nickname ASC ")
    List<String> findVoterNamesByInvitationAndAttendanceOrderByNicknameAsc(@Param("invitation") Invitation invitation, @Param("isAttend") boolean isAttend);

    Optional<Attendance> findByInvitationAndNicknameAndUser(Invitation invitation, String nickname, User user);

    boolean existsByInvitationAndNicknameAndUser(Invitation invitation, String nickname, User user);

    void deleteByInvitation(Invitation invitation);
}
