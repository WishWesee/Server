package depth.main.wishwesee.domain.vote.domain.repository;

import depth.main.wishwesee.domain.vote.domain.ScheduleVoter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleVoterRepository extends JpaRepository<ScheduleVoter, Long> {
   int countByScheduleVoteId(Long id);
}
