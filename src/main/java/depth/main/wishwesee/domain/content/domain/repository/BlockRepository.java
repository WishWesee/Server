package depth.main.wishwesee.domain.content.domain.repository;

import depth.main.wishwesee.domain.content.domain.Block;
import depth.main.wishwesee.domain.invitation.domain.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockRepository extends JpaRepository<Block, Long> {

    void deleteAllByInvitation(Invitation invitation);
}
