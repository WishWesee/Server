package depth.main.wishwesee.domain.content.domain.repository;

import depth.main.wishwesee.domain.content.domain.TimeTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeTableRepository extends JpaRepository<TimeTable, Long> {
}
