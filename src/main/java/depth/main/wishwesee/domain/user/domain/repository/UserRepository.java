package depth.main.wishwesee.domain.user.domain.repository;

import depth.main.wishwesee.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
