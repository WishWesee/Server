package depth.main.wishwesee.domain.vote.domain.repository;

import depth.main.wishwesee.domain.vote.domain.ScheduleVoter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoterNicknameRepository extends JpaRepository<ScheduleVoter, Long> {
}
