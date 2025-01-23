package depth.main.wishwesee.domain.content.domain.repository;

import depth.main.wishwesee.domain.content.domain.Block;
import depth.main.wishwesee.domain.invitation.domain.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block, Long> {

    void deleteAllByInvitation(Invitation invitation);

    @Query("SELECT p.image FROM Photo p WHERE p.invitation.id = :invitationId AND p.sequence = :sequence")
    Optional<String> findExistingPhotoUrlBySequence(@Param("invitationId") Long invitationId, @Param("sequence") int sequence);

    @Query("SELECT b FROM Block b WHERE b.invitation.id = :invitationId")
    List<Block> findByInvitationId(@Param("invitationId") Long invitationId);

    void deleteByInvitation(Invitation invitation);
}
