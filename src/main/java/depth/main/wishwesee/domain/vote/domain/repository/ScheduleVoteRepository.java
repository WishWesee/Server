package depth.main.wishwesee.domain.vote.domain.repository;

import depth.main.wishwesee.domain.invitation.domain.Invitation;
import depth.main.wishwesee.domain.vote.domain.ScheduleVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleVoteRepository extends JpaRepository<ScheduleVote, Long> {
    void deleteAllByInvitation(Invitation invitation);

    @Query("SELECT v FROM ScheduleVote v WHERE v.invitation.id = :invitationId")
    List<ScheduleVote> findByInvitationId(@Param("invitationId") Long invitationId);

    List<ScheduleVote> findByInvitation(Invitation invitation);

    void deleteByInvitation(Invitation invitation);
}
