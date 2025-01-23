package depth.main.wishwesee.domain.vote.domain.repository;

import depth.main.wishwesee.domain.user.domain.User;
import depth.main.wishwesee.domain.vote.domain.ScheduleVote;
import depth.main.wishwesee.domain.vote.domain.ScheduleVoter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduleVoterRepository extends JpaRepository<ScheduleVoter, Long> {
    int countByScheduleVoteId(Long id);

    @Query("SELECT CASE WHEN COUNT(svr) > 0 THEN TRUE ELSE FALSE END " +
            "FROM ScheduleVoter svr " +
            "JOIN svr.scheduleVote sv " +
            "WHERE sv.invitation.id = :invitationId " +
            "AND svr.nickname = :nickname " +
            "AND svr.user IS NULL")
    boolean existsByInvitationIdAndNickname(@Param("invitationId") Long invitationId, @Param("nickname") String nickname);

    @Query("SELECT sv.id " +
            "FROM ScheduleVote sv " +
            "WHERE sv.invitation.id = :invitationId " +
            "AND EXISTS (" +
            "    SELECT 1 " +
            "    FROM ScheduleVoter svr " +
            "    WHERE svr.scheduleVote = sv " +
            "    AND svr.nickname = :nickname " +
            "    AND svr.user IS NULL" +
            ")")
    List<Long> findScheduleVoteIdsByNickname(@Param("invitationId") Long invitationId, @Param("nickname") String nickname);

    Optional<ScheduleVoter> findByScheduleVoteAndNicknameAndUser(ScheduleVote scheduleVote, String nickname, User user);

    @Query("SELECT svr.nickname " +
            "FROM ScheduleVoter svr " +
            "WHERE svr.scheduleVote.id = :scheduleVoteId " +
            "ORDER BY svr.nickname ASC")
    List<String> findNicknamesByScheduleVoteIdOrderByNicknameAsc(@Param("scheduleVoteId") Long scheduleVoteId);

}
