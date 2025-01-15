package depth.main.wishwesee.domain.invitation.domain.repository;

import depth.main.wishwesee.domain.invitation.domain.Invitation;
import depth.main.wishwesee.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    List<Invitation> findBySenderAndTempSavedTrue(User user); // 작성중 초대장

    List<Invitation> findTop3BySenderAndTempSavedFalseOrderByCreatedDateDesc(User user);  // 보낸 초대장 최신순 3개 조회
}
