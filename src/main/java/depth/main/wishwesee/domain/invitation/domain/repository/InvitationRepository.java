package depth.main.wishwesee.domain.invitation.domain.repository;

import depth.main.wishwesee.domain.invitation.domain.Invitation;
import depth.main.wishwesee.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    Optional<Invitation> findByInvitationToken(String invitationToken);
    List<Invitation> findBySenderAndTempSavedTrue(User user); // 작성중 초대장

    List<Invitation> findTop3BySenderAndTempSavedFalseOrderByCreatedDateDesc(User user);  // 보낸 초대장 최신순 3개

    @Query("SELECT i FROM Invitation i WHERE i.sender = :user AND FUNCTION('YEAR', i.createdDate) = :year AND i.tempSaved = false")
    List<Invitation> findBySenderAndYearAndTempSavedFalse(@Param("user") User user, @Param("year") int year); // 연도별 보낸 초대장

    int countBySenderAndTempSavedFalse(User user); // 보낸 초대장 개수
}
