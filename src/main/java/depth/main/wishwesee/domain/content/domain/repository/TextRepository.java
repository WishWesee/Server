package depth.main.wishwesee.domain.content.domain.repository;

import depth.main.wishwesee.domain.content.domain.Text;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TextRepository extends JpaRepository<Text, Long> {
}
