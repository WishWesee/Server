package depth.main.wishwesee.domain.vote.service;

import depth.main.wishwesee.domain.invitation.domain.Invitation;
import depth.main.wishwesee.domain.invitation.domain.repository.InvitationRepository;
import depth.main.wishwesee.domain.user.domain.User;
import depth.main.wishwesee.domain.user.domain.repository.UserRepository;
import depth.main.wishwesee.domain.vote.domain.Attendance;
import depth.main.wishwesee.domain.vote.domain.repository.AttendanceRepository;
import depth.main.wishwesee.domain.vote.dto.request.AttendanceVoteReq;
import depth.main.wishwesee.domain.vote.dto.response.AttendanceVoteStatusRes;
import depth.main.wishwesee.domain.vote.dto.response.CheckNicknameRes;
import depth.main.wishwesee.domain.vote.dto.response.MyVoteRes;
import depth.main.wishwesee.domain.vote.dto.response.VoterNameRes;
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
public class VoteService {

    private final UserRepository userRepository;
    private final InvitationRepository invitationRepository;
    private final AttendanceRepository attendanceRepository;

    public ResponseEntity<ApiResponse> checkDuplicateNickname(Long invitationId, String nickname) {
        Invitation invitation = validateInvitationById(invitationId);
        CheckNicknameRes checkNicknameRes = CheckNicknameRes.builder()
                .isDuplicated(checkDuplicateAttendanceNickname(invitation, nickname))
                .build();
        return ResponseEntity.ok(ApiResponse.builder()
                .check(true)
                .information(checkNicknameRes)
                .build());
    }

    // 중복된 닉네임이 있는(비회원) || 투표 전적이 있는(회원) 경우 조회
    public ResponseEntity<ApiResponse> getMyAttendanceVote(Optional<UserPrincipal> userPrincipal, Long invitationId, String nickname) {
        Invitation invitation = validateInvitationById(invitationId);
        User user = userPrincipal.map(principal -> validateUserById(principal.getId())).orElse(null);
        Attendance attendance;
        if (userPrincipal.isPresent()) {
            attendance = attendanceRepository.findByInvitationAndUser(invitation, user);
        } else {
            attendance = validateAttendanceByInvitationAndNickname(invitation,nickname);
        }
        MyVoteRes myVoteRes = MyVoteRes.builder()
                .attending(attendance.getAttending())
                .build();
        return ResponseEntity.ok(ApiResponse.builder()
                .check(true)
                .information(myVoteRes)
                .build());
    }

    public ResponseEntity<ApiResponse> getAttendanceVoteStatus(Long invitationId) {
        Invitation invitation = validateInvitationById(invitationId);
        AttendanceVoteStatusRes attendanceVoteStatusRes = AttendanceVoteStatusRes.builder()
                .attending(attendanceRepository.countByInvitationAndAttending(invitation, true))
                .notAttending(attendanceRepository.countByInvitationAndAttending(invitation, false))
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
        List<String> voterNames = attendanceRepository.findVoterNamesByInvitationAndAttendance(invitation, isAttend);
        VoterNameRes voterNameRes = VoterNameRes.builder()
                .voters(voterNames)
                .build();
        return ResponseEntity.ok(ApiResponse.builder()
                .check(true)
                .information(voterNameRes)
                .build());
    }

    // 닉네임 존재
    // 업데이트
    @Transactional
    public ResponseEntity<?> voteAttendance(Optional<UserPrincipal> userPrincipal, Long invitationId, AttendanceVoteReq attendanceVoteReq) {
        Invitation invitation = validateInvitationById(invitationId);
        if(isVoteClosed(invitation)) {
            return buildBadRequestResponse("참석 조사가 마감되었습니다.");
        }
        User user = userPrincipal.map(principal -> validateUserById(principal.getId())).orElse(null);
        String nickname = user != null ? user.getName() : attendanceVoteReq.getNickname();
        // 비회원 닉네임 검증
        if (isDuplicateNickname(invitation, nickname)) {

        }
        Attendance attendance = Attendance.builder()
                .nickname(nickname)
                .attending(attendanceVoteReq.getAttending())
                .user(user)
                .invitation(invitation)
                .build();
        attendanceRepository.save(attendance);
        return ResponseEntity.noContent().build();
    }

    // 투표 수정
    public ResponseEntity<?> revoteAttendance(Optional<UserPrincipal> userPrincipal, Long invitationId, AttendanceVoteReq attendanceVoteReq) {
        Invitation invitation = validateInvitationById(invitationId);
        if(isVoteClosed(invitation)) {
            return buildBadRequestResponse("참석 조사가 마감되었습니다.");
        }
        Attendance attendance;
        if (userPrincipal.isPresent()) {
            User user = validateUserById(userPrincipal.get().getId());
            attendance = attendanceRepository.findByInvitationAndUser(invitation, user);
        } else {
            attendance = validateAttendanceByInvitationAndNickname(invitation, attendanceVoteReq.getNickname());
        }
        attendance.updateAttending(attendanceVoteReq.getAttending());
        return ResponseEntity.noContent().build();
    }

    // 투표자 목록 확인
    // (작성자) 투표 상태 변경
    // 투표 수 조회 및 본인 투표 여부
    // 1. 그냥 조회
    // 2. 회원
    // 3. 수정 시

    private boolean isDuplicateNickname(Invitation invitation, String nickname) {
        DefaultAssert.isTrue(nickname != null, "닉네임을 입력해주세요.");
        return !checkDuplicateAttendanceNickname(invitation, nickname);
    }

    private boolean isVoteClosed(Invitation invitation) {
        DefaultAssert.isTrue(invitation.isAttendanceSurveyEnabled(), "참석 조사가 비활성화되어 투표할 수 없습니다.");
        return invitation.isAttendanceSurveyClosed();
    }

    private ResponseEntity<ApiResponse> buildBadRequestResponse(String msg) {
        return ResponseEntity.badRequest().body(
                ApiResponse.builder()
                        .check(false)
                        .information(msg)
                        .build()
        );
    }

    private Attendance validateAttendanceByInvitationAndNickname(Invitation invitation, String nickname) {
        Optional<Attendance> attendanceOptional = attendanceRepository.findByInvitationAndNickname(invitation, nickname);
        DefaultAssert.isTrue(attendanceOptional.isPresent(), "해당 닉네임의 투표자가 존재하지 않습니다.");
        return attendanceOptional.get();
    }

    // 닉네임 중복 체크
    private boolean checkDuplicateAttendanceNickname(Invitation invitation, String nickname) {
        return attendanceRepository.existsByInvitationAndNickname(invitation, nickname);
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
