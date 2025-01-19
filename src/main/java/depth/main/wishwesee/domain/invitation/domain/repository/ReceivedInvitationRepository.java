package depth.main.wishwesee.domain.invitation.domain.repository;

import depth.main.wishwesee.domain.invitation.domain.Invitation;
import depth.main.wishwesee.domain.invitation.domain.ReceivedInvitation;
import depth.main.wishwesee.domain.invitation.dto.response.MyInvitationOverViewRes;
import depth.main.wishwesee.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReceivedInvitationRepository extends JpaRepository<ReceivedInvitation, Long> {
    Optional<ReceivedInvitation> findByInvitation(Invitation invitation);

    boolean existsByInvitationAndReceiver(Invitation invitation, User user);
    boolean existsByReceiverAndInvitation(User receiver, Invitation invitation);

    List<ReceivedInvitation> findTop3ByReceiverOrderByCreatedDateDesc(User user); // 받은 초대장 최신순 3개

    @Query("SELECT ri FROM ReceivedInvitation ri WHERE ri.receiver = :user AND YEAR(ri.createdDate) = :year ORDER BY ri.createdDate DESC")
    List<ReceivedInvitation> findByReceiverAndYear(@Param("user") User user, @Param("year") int year); // 연도별 받은 초대장

}
