package depth.main.wishwesee.domain.invitation.service;

import depth.main.wishwesee.domain.content.domain.*;
import depth.main.wishwesee.domain.content.domain.repository.BlockRepository;
import depth.main.wishwesee.domain.content.dto.request.*;
import depth.main.wishwesee.domain.content.dto.response.*;
import depth.main.wishwesee.domain.invitation.domain.Invitation;
import depth.main.wishwesee.domain.invitation.domain.repository.InvitationRepository;
import depth.main.wishwesee.domain.invitation.domain.repository.ReceivedInvitationRepository;
import depth.main.wishwesee.domain.invitation.dto.request.InvitationReq;
import depth.main.wishwesee.domain.invitation.dto.response.CompletedInvitationRes;
import depth.main.wishwesee.domain.invitation.dto.response.MyInvitationOverViewRes;
import depth.main.wishwesee.domain.invitation.dto.response.SentInvitationRes;
import depth.main.wishwesee.domain.s3.service.S3Uploader;
import depth.main.wishwesee.domain.user.domain.User;
import depth.main.wishwesee.domain.user.domain.repository.UserRepository;
import depth.main.wishwesee.domain.vote.domain.ScheduleVote;
import depth.main.wishwesee.domain.vote.domain.repository.VoteRepository;
import depth.main.wishwesee.domain.vote.dto.request.ScheduleVoteReq;
import depth.main.wishwesee.domain.vote.dto.response.ScheduleVoteRes;
import depth.main.wishwesee.global.config.security.token.UserPrincipal;
import depth.main.wishwesee.global.exception.DefaultException;
import depth.main.wishwesee.global.payload.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvitationService {
    private final S3Uploader s3Uploader;
    private final InvitationRepository invitationRepository;
    private final BlockRepository blockRepository;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final ReceivedInvitationRepository receivedInvitationRepository;
    @Transactional
    public ResponseEntity<?> saveTemporaryInvitation(InvitationReq invitationReq, MultipartFile cardImage, List<MultipartFile> photoImages, UserPrincipal userPrincipal){
        // 사용자 인증 여부 확인
        if(userPrincipal == null){
            throw new DefaultException(ErrorCode.INVALID_AUTHENTICATION, "회원만 임시 저장이 가능합니다.");
        }

        // 공통 저장 로직 호출 (임시 저장 필드를 true로 설정)
        Invitation invitation = saveOrUpdateInvitation(invitationReq, cardImage, photoImages, userPrincipal, true);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "초대장이 임시 저장되었습니다.", "invitationId", invitation.getId()));

    }
    @Transactional
    public ResponseEntity<?> publishInvitation(InvitationReq invitationReq, MultipartFile cardImage, List<MultipartFile> photoImages,
                                                       UserPrincipal userPrincipal) {
        // 공통 저장 로직 호출 (임시 저장 필드를 false로 설정)
        Invitation invitation = saveOrUpdateInvitation(invitationReq, cardImage, photoImages, userPrincipal, false);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "초대장 작성을 완료하였습니다.", "invitationId", invitation.getId()));

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
        String cardImageUrl = uploadImageIfPresent(cardImage, null);

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

        // 기존 이미지와 다를 경우 삭제 및 이미지 업로드
        String updatedCardImageUrl = uploadImageIfPresent(cardImage, invitation.getCardImage());
        invitation.updateCardImage(updatedCardImageUrl);

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
                String currentPhotoUrl = blockRepository.findExistingPhotoUrlBySequence(invitation.getId(), blockReq.getSequence()).orElse(null);
                String newPhotoUrl = null;

                if (photoImages != null && photoIndex < photoImages.size()) {
                    newPhotoUrl = s3Uploader.uploadFile(photoImages.get(photoIndex++));
                    deleteOldImageIfExists(currentPhotoUrl);
                }

                block = Photo.builder()
                        .sequence(blockReq.getSequence())
                        .image(newPhotoUrl != null ? newPhotoUrl : currentPhotoUrl)
                        .invitation(invitation)
                        .build();
            } else{
                throw new DefaultException(ErrorCode.INVALID_PARAMETER, "지원되지 않는 블록 타입입니다.");
            }

        blockRepository.save(block);

        }
    }
    private String uploadImageIfPresent(MultipartFile file, String oldImageUrl) {
        if (file != null && !file.isEmpty()) {
            String uploadedUrl = s3Uploader.uploadFile(file);
            deleteOldImageIfExists(oldImageUrl);
            return uploadedUrl;
        }
        return oldImageUrl;
    }

    private void deleteOldImageIfExists(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            s3Uploader.deleteFile(imageUrl);
        }
    }

    public ResponseEntity<?> getCompletedInvitation(Long id, UserPrincipal userPrincipal) {
        // 초대장 조회
        Invitation invitation = invitationRepository.findById(id)
                .orElseThrow(() -> new DefaultException(ErrorCode.NOT_FOUND, "해당 초대장이 존재하지 않습니다."));

        // 초대장의 모든 블록 조회
        List<Block> allBlocks = blockRepository.findByInvitationId(invitation.getId());

        List<BlockRes> blockResList = allBlocks.stream()
                .map(block -> {
                    if (block instanceof Photo photoBlock) {
                        return PhotoBlockRes.builder()
                                .sequence(photoBlock.getSequence())
                                .image(photoBlock.getImage())
                                .build();
                    } else if (block instanceof Text textBlock) {
                        return TextBlockRes.builder()
                                .sequence(textBlock.getSequence())
                                .content(textBlock.getContent())
                                .build();
                    } else if (block instanceof Box boxBlock) {
                        return BoxBlockRes.builder()
                                .sequence(boxBlock.getSequence())
                                .title(boxBlock.getTitle())
                                .content(boxBlock.getContent())
                                .color(boxBlock.getColor())
                                .build();
                    } else if (block instanceof TimeTable timeTableBlock) {
                        return TimeTableBlockRes.builder()
                                .sequence(timeTableBlock.getSequence())
                                .content(timeTableBlock.getContent())
                                .build();
                    } else {
                        throw new DefaultException(ErrorCode.INVALID_PARAMETER, "지원되지 않는 블록 타입입니다.");
                    }
                }).toList();

        // 일정 투표 리스트 조회
        List<ScheduleVote> scheduleVotes = voteRepository.findByInvitationId(invitation.getId());

        List<ScheduleVoteRes> scheduleVoteResList = scheduleVotes.stream()
                .map(vote -> ScheduleVoteRes.builder()
                        .startDate(vote.getStartDate())
                        .startTime(vote.getStartTime())
                        .endDate(vote.getEndDate())
                        .endTime(vote.getEndTime())
                        .build())
                .toList();

        // 응답 DTO 생성
        CompletedInvitationRes response = CompletedInvitationRes.builder()
                .invitationId(invitation.getId())
                .title(invitation.getTitle())
                .cardImage(invitation.getCardImage())
                .tempSaved(invitation.isTempSaved())
                .startDate(invitation.getStartDate())
                .startTime(invitation.getStartTime())
                .endDate(invitation.getEndDate())
                .endTime(invitation.getEndTime())
                .location(invitation.getLocation())
                .address(invitation.getAddress())
                .mapLink(invitation.getMapLink())
                .mapViewType(invitation.getMapViewType())
                .voteDeadline(invitation.getVoteDeadline())
                .attendanceSurveyEnabled(invitation.isAttendanceSurveyEnabled())
                .scheduleVoteMultiple(invitation.isScheduleVoteMultiple())
                .scheduleVoteClosed(invitation.isScheduleVoteClosed())
                .attendanceSurveyClosed(invitation.isAttendanceSurveyClosed())
                .blocks(blockResList)
                .scheduleVotes(scheduleVoteResList)
                .build();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> getMyInvitations(UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new DefaultException(ErrorCode.NOT_FOUND,"사용자를 찾을 수 없습니다"));

        // 작성 중인 초대장 조회
        List<MyInvitationOverViewRes.InvitationRes> draftingInvitations = invitationRepository.findBySenderAndTempSavedTrue(user)
                .stream()
                .map(invitation -> MyInvitationOverViewRes.InvitationRes.builder()
                        .invitationId(invitation.getId())
                        .title(invitation.getTitle())
                        .cardImage(invitation.getCardImage())
                        .date(invitation.getModifiedDate())
                        .build())
                .toList();

        int draftCount = draftingInvitations.size(); //작성중인 초대장 개수

        // 보낸 초대장 최신순 3개 조회
         List<MyInvitationOverViewRes.InvitationRes> sentInvitations = invitationRepository.findTop3BySenderAndTempSavedFalseOrderByCreatedDateDesc(user)
                .stream()
                .map(invitation -> MyInvitationOverViewRes.InvitationRes.builder()
                        .invitationId(invitation.getId())
                        .title(invitation.getTitle())
                        .cardImage(invitation.getCardImage())
                        .date(invitation.getCreatedDate()) // 보낸 초대장 생성 날짜
                        .build())
                .toList();


        // 받은 초대장 최신순 3개 조회
        List<MyInvitationOverViewRes.InvitationRes> receivedInvitations = receivedInvitationRepository.findTop3ByReceiverOrderByCreatedDateDesc(user)
                .stream()
                .map(receivedInvitation -> MyInvitationOverViewRes.InvitationRes.builder()
                        .invitationId(receivedInvitation.getInvitation().getId())
                        .title(receivedInvitation.getInvitation().getTitle())
                        .cardImage(receivedInvitation.getInvitation().getCardImage())
                        .date(receivedInvitation.getCreatedDate()) // 받은 날짜 기준
                        .build())
                .toList();

        MyInvitationOverViewRes myInvitationOverViewRes = MyInvitationOverViewRes.builder()
                .draftCount(draftCount)
                .draftingInvitations(draftingInvitations)
                .sentInvitations(sentInvitations)
                .receivedInvitations(receivedInvitations)
                .build();

        return ResponseEntity.ok(myInvitationOverViewRes);
    }

    public ResponseEntity<?> getSentInvitationByYear(UserPrincipal userPrincipal, int year) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new DefaultException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다"));

        // 전체 보낸 초대장 개수
        int totalSentInvitations = invitationRepository.countBySenderAndTempSavedFalse(user);

        List<MyInvitationOverViewRes.InvitationRes> receivedInvitations = invitationRepository.findBySenderAndYearAndTempSavedFalse(user, year)
                .stream()
                .map(invitation -> MyInvitationOverViewRes.InvitationRes.builder()
                        .invitationId(invitation.getId())
                        .title(invitation.getTitle())
                        .cardImage(invitation.getCardImage())
                        .date(invitation.getCreatedDate())
                        .build())
                .toList();

        SentInvitationRes response = SentInvitationRes.builder()
                .totalSentInvitations(totalSentInvitations)
                .invitations(receivedInvitations)
                .build();

        return ResponseEntity.ok(response);
    }
}



