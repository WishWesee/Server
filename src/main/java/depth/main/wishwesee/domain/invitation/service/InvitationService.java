package depth.main.wishwesee.domain.invitation.service;

import depth.main.wishwesee.domain.content.domain.*;
import depth.main.wishwesee.domain.content.domain.repository.BlockRepository;
import depth.main.wishwesee.domain.content.dto.request.*;
import depth.main.wishwesee.domain.invitation.domain.Invitation;
import depth.main.wishwesee.domain.invitation.domain.repository.InvitationRepository;
import depth.main.wishwesee.domain.invitation.dto.request.InvitationReq;
import depth.main.wishwesee.domain.s3.service.S3Uploader;
import depth.main.wishwesee.domain.user.domain.User;
import depth.main.wishwesee.domain.user.domain.repository.UserRepository;
import depth.main.wishwesee.domain.vote.domain.ScheduleVote;
import depth.main.wishwesee.domain.vote.domain.repository.VoteRepository;
import depth.main.wishwesee.domain.vote.dto.request.ScheduleVoteReq;
import depth.main.wishwesee.global.config.security.token.UserPrincipal;
import depth.main.wishwesee.global.exception.DefaultException;
import depth.main.wishwesee.global.payload.ApiResponse;
import depth.main.wishwesee.global.payload.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvitationService {
    private final S3Uploader s3Uploader;
    private final InvitationRepository invitationRepository;
    private final BlockRepository blockRepository;
    private final VoteRepository voteRepository;
    private  final UserRepository userRepository;
    @Transactional
    public ResponseEntity<?> saveTemporaryInvitation(InvitationReq invitationReq, MultipartFile cardImage, List<MultipartFile> photoImages, UserPrincipal userPrincipal){
        // 사용자 인증 여부 확인
        if(userPrincipal == null){
            throw new DefaultException(ErrorCode.INVALID_AUTHENTICATION, "회원만 임시 저장이 가능합니다.");
        }

        // 공통 저장 로직 호출 (임시 저장 필드를 true로 설정)
        Invitation invitation = saveOrUpdateInvitation(invitationReq, cardImage, photoImages, userPrincipal, true);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("초대장이 임시 저장되었습니다.")
                .build();
        return ResponseEntity.ok(apiResponse);
    }
    @Transactional
    public ResponseEntity<?> publishInvitation(InvitationReq invitationReq, MultipartFile cardImage, List<MultipartFile> photoImages,
                                                       UserPrincipal userPrincipal) {
        // 공통 저장 로직 호출 (임시 저장 필드를 false로 설정)
        Invitation invitation = saveOrUpdateInvitation(invitationReq, cardImage, photoImages, userPrincipal, false);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("초대장이 게시되었습니다.")
                .build();
        return ResponseEntity.ok(apiResponse);

    }
    private Invitation saveOrUpdateInvitation(InvitationReq invitationReq, MultipartFile cardImage, List<MultipartFile> photoImages, UserPrincipal userPrincipal, boolean isTemporary) {
        Invitation invitation;

        if (invitationReq.getInvitationId() != null) {
            // 기존 초대장 수정
            invitation = invitationRepository.findById(invitationReq.getInvitationId())
                    .orElseThrow(() -> new DefaultException(ErrorCode.NOT_FOUND, "해당 초대장이 존재하지 않습니다."));

            invitation.updateTempSaved(isTemporary);  // 임시 저장 여부 업데이트
            updateInvitationDetails(invitation, invitationReq, cardImage, photoImages);

        } else {
            // 새 초대장 생성
            invitation = createNewInvitation(invitationReq, cardImage, userPrincipal, isTemporary);
            invitationRepository.save(invitation);

            // 블럭 데이터 저장
            if (invitationReq.getBlocks() != null && !invitationReq.getBlocks().isEmpty()) {
                saveBlocks(invitationReq.getBlocks(), invitation, photoImages);
            }

            // 일정 투표 데이터 저장
            if (invitationReq.getScheduleVotes() != null && !invitationReq.getScheduleVotes().isEmpty()) {
                saveScheduleVotes(invitationReq.getScheduleVotes(), invitation);
            }
        }
        return invitation;
    }

    private Invitation createNewInvitation(InvitationReq invitationReq, MultipartFile cardImage,
                                           UserPrincipal userPrincipal, boolean isTemporary) {
        // S3에 카드 이미지 업로드
        String cardImageUrl;

        try {
            cardImageUrl = cardImage != null ? s3Uploader.uploadFile(cardImage) : null;
        } catch (Exception e) {
            throw new DefaultException(ErrorCode.INVALID_FILE_PATH, "S3 업로드 중 오류가 발생했습니다.");
        }


        User user = null;
        if(userPrincipal != null){
            user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new DefaultException(ErrorCode.NOT_FOUND,"사용자를 찾을 수 없습니다"));
        }

        return Invitation.builder()
                .title(invitationReq.getTitle())
                .cardImage(cardImageUrl)
                .tempSaved(isTemporary)
                .startDate(invitationReq.getStartDate())
                .startTime(invitationReq.getStartTime())
                .endDate(invitationReq.getEndDate())
                .endTime(invitationReq.getEndTime())
                .location(invitationReq.getLocation())
                .address(invitationReq.getAddress())
                .mapLink(invitationReq.getMapLink())
                .mapViewType(invitationReq.getMapViewType())
                .voteDeadline(invitationReq.getVoteDeadline())
                .attendanceSurveyEnabled(invitationReq.isAttendanceSurveyEnabled())
                .scheduleVoteMultiple(userPrincipal != null ? invitationReq.isScheduleVoteMultiple() : false)
                .scheduleVoteClosed(invitationReq.isScheduleVoteClosed())
                .attendanceSurveyClosed(invitationReq.isAttendanceSurveyClosed())
                .sender(user)
                .build();
    }

    private void updateInvitationDetails(Invitation invitation, InvitationReq invitationReq, MultipartFile cardImage, List<MultipartFile> photoImages) {
        invitation.updateTitle(invitationReq.getTitle());
        invitation.updateDateTime(invitationReq.getStartDate(), invitationReq.getStartTime(), invitationReq.getEndDate(), invitationReq.getEndTime());
        invitation.updateLocationDetails(invitationReq.getLocation(), invitationReq.getAddress(), invitationReq.getMapLink());
        invitation.updateMapViewType(invitationReq.getMapViewType());
        invitation.updateAttendanceSurvey(invitationReq.isAttendanceSurveyEnabled());
        invitation.updateVoteDeadline(invitationReq.getVoteDeadline());
        invitation.updateScheduleVoteMultiple(invitationReq.isScheduleVoteMultiple());
        invitation.updateScheduleVoteClosed(invitationReq.isScheduleVoteClosed());
        invitation.updateAttendanceSurveyClosed(invitationReq.isAttendanceSurveyClosed());

        String cardImageUrl;

        try {
            cardImageUrl = cardImage != null ? s3Uploader.uploadFile(cardImage) : null;
        } catch (Exception e) {
            throw new DefaultException(ErrorCode.INVALID_FILE_PATH, "S3 업로드 중 오류가 발생했습니다.");
        }

        invitation.updateCardImage(cardImageUrl);

        // 기존 블록 및 일정 투표 삭제 후 다시 추가
        blockRepository.deleteAllByInvitation(invitation);
        voteRepository.deleteAllByInvitation(invitation);

        // 블럭 데이터 저장
        if (invitationReq.getBlocks() != null && !invitationReq.getBlocks().isEmpty()) {
            saveBlocks(invitationReq.getBlocks(), invitation, photoImages);
        }

        // 일정 투표 데이터 저장
        if (invitationReq.getScheduleVotes() != null && !invitationReq.getScheduleVotes().isEmpty()) {
            saveScheduleVotes(invitationReq.getScheduleVotes(), invitation);
        }

    }

    private void saveScheduleVotes(List<ScheduleVoteReq> scheduleVoteReqs, Invitation savedInvitation) {
        for(ScheduleVoteReq voteReq : scheduleVoteReqs){
            ScheduleVote scheduleVote = ScheduleVote.builder()
                    .startDate(voteReq.getStartDate())
                    .startTime(voteReq.getStartTime())
                    .endDate(voteReq.getEndDate())
                    .endTime(voteReq.getEndTime())
                    .invitation(savedInvitation)
                    .build();
            voteRepository.save(scheduleVote);
        }
    }

    private void saveBlocks(List<BlockReq> blockReqs, Invitation invitation, List<MultipartFile> photoImages) {

        int photoIndex = 0; // 업로드된 사진 파일의 인덱스를 관리

        for (BlockReq blockReq : blockReqs) {
            Block block;

            // 실제 타입별로 처리
            if (blockReq instanceof TextBlockReq) {
                TextBlockReq textBlockReq = (TextBlockReq) blockReq;
                block = Text.builder()
                        .sequence(textBlockReq.getSequence())
                        .content(textBlockReq.getContent())
                        .invitation(invitation)
                        .build();
            } else if (blockReq instanceof BoxBlockReq) {
                BoxBlockReq boxBlockReq = (BoxBlockReq) blockReq;
                block = Box.builder()
                        .sequence(boxBlockReq.getSequence())
                        .title(boxBlockReq.getTitle())
                        .color(boxBlockReq.getColor())
                        .content(boxBlockReq.getContent())
                        .invitation(invitation)
                        .build();
            } else if (blockReq instanceof TimeTableBlockReq) {
                TimeTableBlockReq timeTableBlockReq = (TimeTableBlockReq) blockReq;
                block = TimeTable.builder()
                        .sequence(timeTableBlockReq.getSequence())
                        .content(timeTableBlockReq.getContent())
                        .invitation(invitation)
                        .build();
            } else if (blockReq instanceof PhotoBlockReq) {
                // JSON에 명시된 photo 블록에 파일 매칭
                if (photoImages != null && photoIndex < photoImages.size()) {
                    PhotoBlockReq photoBlockReq = (PhotoBlockReq)blockReq;
                    String photoUrl = s3Uploader.uploadFile(photoImages.get(photoIndex++));
                    block = Photo.builder()
                            .sequence(blockReq.getSequence())
                            .image(photoUrl) // 업로드된 S3 URL 사용
                            .invitation(invitation)
                            .build();
                } else {
                    throw new DefaultException(ErrorCode.INVALID_PARAMETER, "photo 블록에 대한 이미지가 충분하지 않습니다.");
                }
            } else {
                throw new DefaultException(ErrorCode.INVALID_PARAMETER, "지원되지 않는 블록 타입입니다.");
            }

            blockRepository.save(block);

        }
    }
}

