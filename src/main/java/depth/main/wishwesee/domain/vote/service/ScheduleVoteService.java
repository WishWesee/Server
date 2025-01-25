package depth.main.wishwesee.domain.vote.service;

import depth.main.wishwesee.domain.invitation.domain.Invitation;
import depth.main.wishwesee.domain.invitation.domain.repository.InvitationRepository;
import depth.main.wishwesee.domain.user.domain.User;
import depth.main.wishwesee.domain.user.domain.repository.UserRepository;
import depth.main.wishwesee.domain.vote.domain.ScheduleVote;
import depth.main.wishwesee.domain.vote.domain.ScheduleVoter;
import depth.main.wishwesee.domain.vote.domain.repository.ScheduleVoteRepository;
import depth.main.wishwesee.domain.vote.domain.repository.ScheduleVoterRepository;
import depth.main.wishwesee.domain.vote.dto.request.VoteScheduleReq;
import depth.main.wishwesee.domain.vote.dto.response.CheckNicknameRes;
import depth.main.wishwesee.domain.vote.dto.response.MyScheduleVoteIdsRes;
import depth.main.wishwesee.domain.vote.dto.response.ScheduleVoterRes;
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
public class ScheduleVoteService {

    private final UserRepository userRepository;
    private final InvitationRepository invitationRepository;
    private final ScheduleVoteRepository scheduleVoteRepository;
    private final ScheduleVoterRepository scheduleVoterRepository;

    // 닉네임 중복체크(회원과는 체크하지말것)
    public ResponseEntity<ApiResponse> checkDuplicateNickname(Long invitationId, String nickname) {
        Invitation invitation = validateInvitationById(invitationId);
        CheckNicknameRes checkNicknameRes = CheckNicknameRes.builder()
                .duplicated(checkDuplicateScheduleNickname(invitation, nickname))
                .build();
        return ResponseEntity.ok(ApiResponse.builder()
                .check(true)
                .information(checkNicknameRes)
                .build());
    }

    // (비회원) 이름 입력 후 투표 현황 조회
    public ResponseEntity<ApiResponse> getScheduleVoteByNickname(Long invitationId, String nickname) {
        List<Long> ids = scheduleVoterRepository.findScheduleVoteIdsByNickname(invitationId, nickname);
        MyScheduleVoteIdsRes myScheduleVoteIdsRes = MyScheduleVoteIdsRes.builder()
                .scheduleVoteIds(ids)
                .build();
        return ResponseEntity.ok(ApiResponse.builder()
                .check(true)
                .information(myScheduleVoteIdsRes)
                .build());
    }

    // 투표하기&재투표하기
    @Transactional
    public ResponseEntity<Void> voteSchedule(UserPrincipal userPrincipal, Long invitationId, VoteScheduleReq voteScheduleReq) {
        Invitation invitation = validateInvitationById(invitationId);
        checkVoteOpened(invitation);
        User user = Optional.ofNullable(userPrincipal)
                .map(principal -> validateUserById(principal.getId()))
                .orElse(null);
        String nickname = user != null ? user.getName() : voteScheduleReq.getNickname();
        DefaultAssert.isTrue(nickname != null && !nickname.isEmpty(), "닉네임이 존재하지 않습니다.");
        if (!invitation.isScheduleVoteMultiple()) {
            checkVoteMultiple(voteScheduleReq.getScheduleVoteIds());
        }
        List<ScheduleVote> scheduleVotes = scheduleVoteRepository.findByInvitationId(invitation.getId());
        scheduleVotes.stream()
                .map(scheduleVote -> scheduleVoterRepository.findByScheduleVoteAndNicknameAndUser(scheduleVote, nickname, user))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(scheduleVoterRepository::delete);
        scheduleVotes.stream()
                .filter(scheduleVote -> voteScheduleReq.getScheduleVoteIds().contains(scheduleVote.getId()))
                .forEach(scheduleVote -> saveScheduleVoter(user, scheduleVote, nickname));
        return ResponseEntity.noContent().build();
    }

    private void saveScheduleVoter(User user, ScheduleVote scheduleVote, String nickname) {
        scheduleVoterRepository.save(ScheduleVoter.builder()
                .user(user)
                .scheduleVote(scheduleVote)
                .nickname(nickname)
                .build());
    }

    private void checkVoteOpened(Invitation invitation) {
        DefaultAssert.isTrue(!invitation.isScheduleVoteClosed(), "일정 투표가 마감되었습니다.");
    }

    private void checkVoteClosed(Invitation invitation) {
        DefaultAssert.isTrue(invitation.isScheduleVoteClosed(), "일정 투표가 마감되지 않았습니다.");
    }

    private void checkVoteMultiple(List<Long> ids) {
        DefaultAssert.isTrue(ids.size() <= 1, "복수 투표가 불가능한 초대장입니다.");
    }

    private void checkIsSender(User user, User sender) {
        DefaultAssert.isTrue(sender == user, "초대장 작성자가 아닙니다.");
    }

    // 투표 마감 상태 변경하기 - 동적 스케쥴링 or 다른 방법

    // 작성자
    // 투표자 명단 조회하기
    public ResponseEntity<ApiResponse> getVoterList(UserPrincipal userPrincipal, Long invitationId, Long scheduleVoteId) {
        Invitation invitation = validateInvitationById(invitationId);
        User user = validateUserById(userPrincipal.getId());
        checkIsSender(user, invitation.getSender());
        ScheduleVote scheduleVote = validateScheduleVoteById(scheduleVoteId);
        ScheduleVoterRes scheduleVoterRes = ScheduleVoterRes.builder()
                .startDate(scheduleVote.getStartDate())
                .startTime(scheduleVote.getStartTime())
                .endDate(scheduleVote.getEndDate())
                .endTime(scheduleVote.getEndTime())
                .voterCount(scheduleVoterRepository.countByScheduleVoteId(scheduleVoteId))
                .voterNames(scheduleVoterRepository.findNicknamesByScheduleVoteIdOrderByNicknameAsc(scheduleVoteId))
                .build();
        return ResponseEntity.ok(ApiResponse.builder()
                .check(true)
                .information(scheduleVoterRes)
                .build());
    }

    // 일정 확정하기(일정 업데이트)
    @Transactional
    public ResponseEntity<Void> updateSchedule(UserPrincipal userPrincipal, Long invitationId, Long scheduleVoteId) {
        Invitation invitation = validateInvitationById(invitationId);
        User user = validateUserById(userPrincipal.getId());
        checkIsSender(user, invitation.getSender());
        checkVoteClosed(invitation);
        ScheduleVote scheduleVote = validateScheduleVoteById(scheduleVoteId);
        invitation.updateSchedule(scheduleVote.getStartDate(), scheduleVote.getStartTime(), scheduleVote.getEndDate(), scheduleVote.getEndTime());
        deleteScheduleVoteAndVoters(invitation);
        return ResponseEntity.noContent().build();
    }

    private void deleteScheduleVoteAndVoters(Invitation invitation) {
        List<ScheduleVote> scheduleVotes = scheduleVoteRepository.findByInvitationId(invitation.getId());
        scheduleVotes.forEach(scheduleVote -> scheduleVoterRepository.deleteByScheduleVote(scheduleVote));
        scheduleVoteRepository.deleteAllByInvitation(invitation);
    }


    private boolean checkDuplicateScheduleNickname(Invitation invitation, String nickname) {
        return scheduleVoterRepository.existsByInvitationIdAndNickname(invitation.getId(), nickname);
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

    private ScheduleVote validateScheduleVoteById(Long scheduleVoteId) {
        Optional<ScheduleVote> scheduleVoteOptional = scheduleVoteRepository.findById(scheduleVoteId);
        DefaultAssert.isOptionalPresent(scheduleVoteOptional, "해당 일정이 존재하지 않습니다.");
        return scheduleVoteOptional.get();
    }

}
