package depth.main.wishwesee.domain.vote.domain.repository;

import depth.main.wishwesee.domain.vote.domain.ScheduleVote;
import depth.main.wishwesee.domain.vote.domain.VoterNickname;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoterNicknameRepository extends JpaRepository<VoterNickname, Long> {
    void deleteByScheduleVote(ScheduleVote scheduleVote);
}
