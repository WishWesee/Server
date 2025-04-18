package depth.main.wishwesee.domain.vote.service;

import depth.main.wishwesee.domain.invitation.domain.Invitation;
import depth.main.wishwesee.domain.invitation.domain.repository.InvitationRepository;
import depth.main.wishwesee.domain.user.domain.User;
import depth.main.wishwesee.domain.user.domain.repository.UserRepository;
import depth.main.wishwesee.domain.vote.domain.Attendance;
import depth.main.wishwesee.domain.vote.domain.repository.AttendanceRepository;
import depth.main.wishwesee.domain.vote.dto.request.VoteAttendanceReq;
import depth.main.wishwesee.domain.vote.dto.response.*;
import depth.main.wishwesee.global.DefaultAssert;
import depth.main.wishwesee.global.config.security.token.UserPrincipal;
import depth.main.wishwesee.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceVoteService {

    private final UserRepository userRepository;
    private final InvitationRepository invitationRepository;
    private final AttendanceRepository attendanceRepository;

    public ResponseEntity<ApiResponse> checkDuplicateNickname(Long invitationId, String nickname) {
        Invitation invitation = validateInvitationById(invitationId);
        CheckNicknameRes checkNicknameRes = CheckNicknameRes.builder()
                .duplicated(checkDuplicateAttendanceNickname(invitation, nickname))
                .build();
        return ResponseEntity.ok(ApiResponse.builder()
                .check(true)
                .information(checkNicknameRes)
                .build());
    }

    // (비회원) 이름 입력 후 투표 현황 조회
    public ResponseEntity<ApiResponse> getAttendanceVoteByNickname(Long invitationId, String nickname) {
        Invitation invitation = validateInvitationById(invitationId);
        // User user = userPrincipal.map(principal -> validateUserById(principal.getId())).orElse(null);
        Attendance attendance;
        // if (userPrincipal.isPresent()) {
        //     attendance = validateAttendanceByInvitationAndUser(invitation, user);
        // } else {
        Optional<Attendance> attendanceOp = attendanceRepository.findByInvitationAndNicknameAndUser(invitation, nickname, null);
        Boolean isAttending = attendanceOp.map(Attendance::getAttending).orElse(null);
        // }
        MyAttendanceVoteRes myAttendanceVoteRes = MyAttendanceVoteRes.builder()
                .attending(isAttending)
                .build();
        return ResponseEntity.ok(ApiResponse.builder()
                .check(true)
                .information(myAttendanceVoteRes)
                .build());
    }

    // 투표 현황 조회
    // (회원은 투표 전적이 있는 경우 본인 투표 상태도 함께 전달)
    public ResponseEntity<ApiResponse> getAttendanceVoteStatus(UserPrincipal userPrincipal, Long invitationId) {
        Invitation invitation = validateInvitationById(invitationId);
        User user = Optional.ofNullable(userPrincipal)
                .map(principal -> validateUserById(principal.getId()))
                .orElse(null);
        Boolean myAttendance = user != null
                ? attendanceRepository.findByInvitationAndUser(invitation, user)
                .map(Attendance::getAttending)
                .orElse(null)  // 참석 조사 전적이 없다면 null
                : null;
        boolean isSender = invitation.getSender().equals(user);
        AttendanceVoteStatusRes attendanceVoteStatusRes = AttendanceVoteStatusRes.builder()
                .attendanceSurveyClosed(invitation.isAttendanceSurveyClosed())
                .attendingCount(attendanceRepository.countByInvitationAndAttending(invitation, true))
                .notAttendingCount(attendanceRepository.countByInvitationAndAttending(invitation, false))
                .isAttending(myAttendance)
                .sender(isSender)
                .build();
        return ResponseEntity.ok(ApiResponse.builder()
                .check(true)
                .information(attendanceVoteStatusRes)
                .build());
    }

    public ResponseEntity<ApiResponse> getVoterList(UserPrincipal userPrincipal, Long invitationId, boolean isAttend) {
        Invitation invitation = validateInvitationById(invitationId);
        User user = validateUserById(userPrincipal.getId());
        DefaultAssert.isTrue(invitation.getSender() == user, "초대장 작성자가 아닙니다.");
        List<String> voterNames = attendanceRepository.findVoterNamesByInvitationAndAttendanceOrderByNicknameAsc(invitation, isAttend);
        AttendanceVoterRes attendanceVoterRes = AttendanceVoterRes.builder()
                .voterCount(attendanceRepository.countByInvitationAndAttending(invitation, isAttend))
                .voterNames(voterNames)
                .build();
        return ResponseEntity.ok(ApiResponse.builder()
                .check(true)
                .information(attendanceVoterRes)
                .build());
    }

    @Transactional
    public ResponseEntity<?> voteAttendance(UserPrincipal userPrincipal, Long invitationId, VoteAttendanceReq voteAttendanceReq) {
        Invitation invitation = validateInvitationById(invitationId);
        checkVoteClosed(invitation);
        User user = Optional.ofNullable(userPrincipal)
                .map(principal -> validateUserById(principal.getId()))
                .orElse(null);
        String nickname = user != null ? user.getName() : voteAttendanceReq.getNickname();
        DefaultAssert.isTrue(nickname != null && !nickname.isEmpty(), "닉네임이 존재하지 않습니다.");
        Attendance attendance = (user != null)
                ? attendanceRepository.findByInvitationAndUser(invitation, user).orElse(null)
                : attendanceRepository.findByInvitationAndNicknameAndUser(invitation, nickname, null).orElse(null);
        if (attendance != null) {
            attendance.updateAttending(voteAttendanceReq.isAttending());
        } else {
            Attendance newAttendance = Attendance.builder()
                    .nickname(nickname)
                    .attending(voteAttendanceReq.isAttending())
                    .user(user)
                    .invitation(invitation)
                    .build();
            attendanceRepository.save(newAttendance);
        }
        return ResponseEntity.noContent().build();
    }

    // (작성자) 투표 상태 변경
    @Transactional
    public ResponseEntity<ApiResponse> updateAttendanceSurvey(UserPrincipal userPrincipal, Long invitationId) {
        Invitation invitation = validateInvitationById(invitationId);
        User user = validateUserById(userPrincipal.getId());
        DefaultAssert.isTrue(invitation.getSender() == user, "초대장 작성자가 아닙니다.");
        boolean attendanceSurveyStatus = invitation.isAttendanceSurveyClosed();
        invitation.updateAttendanceSurveyClosed(!attendanceSurveyStatus);
        AttendanceSurveyClosedRes attendanceSurveyClosedRes = AttendanceSurveyClosedRes.builder()
                .attendanceSurveyClosed(invitation.isAttendanceSurveyClosed())
                .build();
        return ResponseEntity.ok(ApiResponse.builder()
                .check(true)
                .information(attendanceSurveyClosedRes)
                .build());
    }

    private void checkVoteClosed(Invitation invitation) {
        DefaultAssert.isTrue(invitation.isAttendanceSurveyEnabled(), "참석 조사가 비활성화되어 투표할 수 없습니다.");
        DefaultAssert.isTrue(!invitation.isAttendanceSurveyClosed(), "참석 조사가 마감되었습니다.");
    }

    private boolean checkDuplicateAttendanceNickname(Invitation invitation, String nickname) {
        return attendanceRepository.existsByInvitationAndNicknameAndUser(invitation, nickname, null);
    }

    private User validateUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        DefaultAssert.isOptionalPresent(userOptional, "사용자가 존재하지 않습니다.");
        return userOptional.get();
    }

    private Invitation validateInvitationById(Long invitationId) {
        Optional<Invitation> invitationOptional = invitationRepository.findById(invitationId);
        DefaultAssert.isOptionalPresent(invitationOptional, "초대장이 존재하지 않습니다.");
        return invitationOptional.get();
    }
}
