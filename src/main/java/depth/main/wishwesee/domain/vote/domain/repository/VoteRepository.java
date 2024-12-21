package depth.main.wishwesee.domain.vote.domain.repository;

import depth.main.wishwesee.domain.vote.domain.ScheduleVote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<ScheduleVote, Long> {
}
