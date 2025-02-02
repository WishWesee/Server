package depth.main.wishwesee.domain.invitation.service;

import depth.main.wishwesee.domain.content.domain.*;
import depth.main.wishwesee.domain.content.domain.repository.BlockRepository;
import depth.main.wishwesee.domain.content.dto.request.*;
import depth.main.wishwesee.domain.content.dto.response.*;
import depth.main.wishwesee.domain.invitation.domain.Invitation;
import depth.main.wishwesee.domain.invitation.domain.ReceivedInvitation;
import depth.main.wishwesee.domain.invitation.domain.repository.FeedbackRepository;
import depth.main.wishwesee.domain.invitation.domain.repository.InvitationRepository;
import depth.main.wishwesee.domain.invitation.domain.repository.ReceivedInvitationRepository;
import depth.main.wishwesee.domain.invitation.dto.request.InvitationReq;
import depth.main.wishwesee.domain.invitation.dto.request.SaveInvitationReq;
import depth.main.wishwesee.domain.invitation.dto.response.InvitationDetailRes;
import depth.main.wishwesee.domain.invitation.dto.response.MyInvitationOverViewRes;
import depth.main.wishwesee.domain.invitation.dto.response.InvitationListRes;
import depth.main.wishwesee.domain.s3.service.S3Uploader;
import depth.main.wishwesee.domain.user.domain.User;
import depth.main.wishwesee.domain.user.domain.repository.UserRepository;
import depth.main.wishwesee.domain.vote.domain.ScheduleVote;
import depth.main.wishwesee.domain.vote.domain.repository.AttendanceRepository;
import depth.main.wishwesee.domain.vote.domain.repository.ScheduleVoteRepository;
import depth.main.wishwesee.domain.vote.domain.repository.ScheduleVoterRepository;
import depth.main.wishwesee.domain.vote.dto.request.ScheduleVoteReq;
import depth.main.wishwesee.domain.vote.dto.response.ScheduleVoteRes;
import depth.main.wishwesee.global.DefaultAssert;
import depth.main.wishwesee.global.config.security.token.UserPrincipal;
import depth.main.wishwesee.global.exception.DefaultException;
import depth.main.wishwesee.global.payload.ErrorCode;
import depth.main.wishwesee.global.payload.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvitationService {
    private final S3Uploader s3Uploader;
    private final InvitationRepository invitationRepository;
    private final BlockRepository blockRepository;
    private final ScheduleVoteRepository scheduleVoteRepository;
    private final ScheduleVoterRepository scheduleVoterRepository;
    private final UserRepository userRepository;
    private final ReceivedInvitationRepository receivedInvitationRepository;
    private  final AttendanceRepository attendanceRepository;
    private final FeedbackRepository feedbackRepository;
    private final FeedbackService feedbackService;
    @Transactional
    public ResponseEntity<?> saveTemporaryInvitation(InvitationReq invitationReq, MultipartFile cardImage, List<MultipartFile> photoImages, UserPrincipal userPrincipal){
        // 사용자 인증 여부 확인
        if(userPrincipal == null){
            throw new DefaultException(ErrorCode.INVALID_AUTHENTICATION, "회원만 임시 저장이 가능합니다.");
        }

        // 공통 저장 로직 호출 (임시 저장 필드를 true로 설정)
        Invitation invitation = saveOrUpdateInvitation(invitationReq, cardImage, photoImages, userPrincipal, true);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "초대장이 임시 저장되었습니다.",
                        "invitationId", invitation.getId()
                ));

    }
    @Transactional
    public ResponseEntity<?> publishInvitation(InvitationReq invitationReq, MultipartFile cardImage, List<MultipartFile> photoImages,
                                                       UserPrincipal userPrincipal) {
        // 공통 저장 로직 호출 (임시 저장 필드를 false로 설정)
        Invitation invitation = saveOrUpdateInvitation(invitationReq, cardImage, photoImages, userPrincipal, false);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "초대장 작성을 완료하였습니다.",
                        "invitationId", invitation.getId()
                ));

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
                .userLocation(invitationReq.getUserLocation())
                .location(invitationReq.getLocation())
                .address(invitationReq.getAddress())
                .mapLink(invitationReq.getMapLink())
                .latitude(invitationReq.getLatitude())
                .longitude(invitationReq.getLongitude())
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
        invitation.updateLocationDetails(invitationReq.getUserLocation(), invitationReq.getLocation(), invitationReq.getAddress(), invitationReq.getMapLink(),
                invitationReq.getLatitude(),invitationReq.getLongitude());
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
        scheduleVoteRepository.deleteAllByInvitation(invitation);

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
            scheduleVoteRepository.save(scheduleVote);
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
                        .font(textBlockReq.getFont())
                        .styles(textBlockReq.getStyles())
                        .color(textBlockReq.getColor())
                        .invitation(invitation)
                        .build();
            } else if (blockReq instanceof BoxBlockReq) {
                BoxBlockReq boxBlockReq = (BoxBlockReq) blockReq;
                block = Box.builder()
                        .sequence(boxBlockReq.getSequence())
                        .title(boxBlockReq.getTitle())
                        .colorCode(boxBlockReq.getColorCode())
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
            } else if (blockReq instanceof DividerBlockReq) {
                DividerBlockReq dividerBlockReq = (DividerBlockReq) blockReq;
                block = Divider.builder()
                        .sequence(dividerBlockReq.getSequence())
                        .invitation(invitation)
                        .build();
            } else {
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

    @Transactional
    public ResponseEntity<?> saveReceivedInvitation(SaveInvitationReq saveInvitationReq, UserPrincipal userPrincipal) {
        // 현재 사용자 정보 가져오기
        User receiver = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new DefaultException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // 초대장 정보 가져오기
        Invitation invitation = invitationRepository.findById(saveInvitationReq.getInvitationId())
                .orElseThrow(() -> new DefaultException(ErrorCode.NOT_FOUND, "해당 초대장이 존재하지 않습니다."));

        // 작성자 본인 확인
        if(invitation.getSender() != null && invitation.getSender().getId().equals(receiver.getId())){
            ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_PARAMETER, "본인이 작성한 초대장은 저장할 수 없습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // 중복 확인 (초대장이 이미 저장된 경우)
        boolean alreadyExists = receivedInvitationRepository.existsByReceiverAndInvitation(receiver, invitation);
        if (alreadyExists) {
            ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.DUPLICATE_ERROR, "이미 내 목록에 저장된 초대장입니다.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        // 초대장 저장
        ReceivedInvitation receivedInvitation = ReceivedInvitation.builder()
                .receiver(receiver)
                .invitation(invitation)
                .build();
        receivedInvitationRepository.save(receivedInvitation);

        return ResponseEntity.noContent().build();
    }



    public ResponseEntity<?> getInvitation(Long invitationId, UserPrincipal userPrincipal, boolean isTemporary) {
        // 초대장 조회
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new DefaultException(ErrorCode.NOT_FOUND, "해당 초대장이 존재하지 않습니다."));

        // 사용자 확인
        User user = Optional.ofNullable(userPrincipal)
                .map(principal -> validateUserById(principal.getId()))
                .orElse(null);

        // 초대장 상태 확인 (임시 저장 여부 및 완성 여부)
        validateInvitationState(isTemporary, invitation);

        // 작성자 본인 여부 확인
        boolean isOwner = user == invitation.getSender();

        // 임시 저장된 초대장은 작성자 본인만 접근 가능
        if(isTemporary && !isOwner){
            throw new DefaultException(ErrorCode.INVALID_AUTHENTICATION, "작성자 본인만 접근 가능합니다.");
        }

        // 보관함에 저장 여부 확인(완성된 초대장에서 적용)
        boolean alreadySaved = user != null && receivedInvitationRepository.existsByReceiverAndInvitation(user, invitation);

        // 초대장의 모든 블록 조회 및 변환
        List<BlockRes> blockResList = transformBlocks(blockRepository.findByInvitationId(invitation.getId()));

        // 초대장의 투표 정보 조회
        List<ScheduleVoteRes> scheduleVoteResList = getInvitationScheduleVote(user, invitation);

        // 사용자가 투표했는지 확인
        boolean hasVoted = user != null && scheduleVoterRepository.existsByInvitationIdAndUser(invitationId, user);

        // 후기 작성 가능 여부
        boolean canWriteFeedback = feedbackService.checkWritableFeedback(invitation);

        // 응답 DTO 생성
        InvitationDetailRes response = InvitationDetailRes.builder()
                .invitationId(invitation.getId())
                .isOwner(isOwner)
                .isLoggedIn(user != null)
                .cardImage(invitation.getCardImage())
                .title(invitation.getTitle())
                .startDate(invitation.getStartDate())
                .startTime(invitation.getStartTime())
                .endDate(invitation.getEndDate())
                .endTime(invitation.getEndTime())
                .voteDeadline(invitation.getVoteDeadline())
                .scheduleVoteMultiple(invitation.isScheduleVoteMultiple())
                .hasScheduleVote(hasVoted)
                .scheduleVotes(scheduleVoteResList)
                .scheduleVoteClosed(invitation.isScheduleVoteClosed())
                .mapViewType(invitation.getMapViewType())
                .userLocation(invitation.getUserLocation())
                .location(invitation.getLocation())
                .address(invitation.getAddress())
                .mapLink(invitation.getMapLink())
                .latitude(invitation.getLatitude())
                .longitude(invitation.getLongitude())
                .blocks(blockResList)
                .alreadySaved(alreadySaved)
                .canWriteFeedback(canWriteFeedback)
                .attendanceSurveyEnabled(invitation.isAttendanceSurveyEnabled())
                .build();

        return ResponseEntity.ok(response);
    }

    private List<BlockRes> transformBlocks(List<Block> allBlocks) {
        return allBlocks.stream()
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
                                .font(textBlock.getFont())
                                .styles(textBlock.getStyles())
                                .color(textBlock.getColor())
                                .build();
                    } else if (block instanceof Box boxBlock) {
                        return BoxBlockRes.builder()
                                .sequence(boxBlock.getSequence())
                                .title(boxBlock.getTitle())
                                .content(boxBlock.getContent())
                                .colorCode(boxBlock.getColorCode())
                                .build();
                    } else if (block instanceof TimeTable timeTableBlock) {
                        return TimeTableBlockRes.builder()
                                .sequence(timeTableBlock.getSequence())
                                .content(timeTableBlock.getContent())
                                .build();
                    } else if (block instanceof Divider dividerBlock){
                        return DividerBlockRes.builder()
                                .sequence(dividerBlock.getSequence())
                                .build();
                    } else {
                        throw new DefaultException(ErrorCode.INVALID_PARAMETER, "지원되지 않는 블록 타입입니다.");
                    }
                }).toList();
    }

    private void validateInvitationState(boolean isTemporary, Invitation invitation) {
        if(isTemporary != invitation.isTempSaved()){
            throw new DefaultException(ErrorCode.NOT_FOUND, "요청한 초대장의 상태가 일치하지 않습니다.");
        }
    }

    private List<ScheduleVoteRes> getInvitationScheduleVote(User user, Invitation invitation) {
        List<ScheduleVote> scheduleVotes = scheduleVoteRepository.findByInvitationId(invitation.getId());
        return scheduleVotes.stream()
                .map(vote -> {
                    boolean isVoted = user != null && scheduleVoterRepository.existsByScheduleVoteAndUser(vote, user);
                    return ScheduleVoteRes.builder()
                            .scheduleVoteId(vote.getId())
                            .startDate(vote.getStartDate())
                            .startTime(vote.getStartTime())
                            .endDate(vote.getEndDate())
                            .endTime(vote.getEndTime())
                            .voterCount(scheduleVoterRepository.countByScheduleVoteId(vote.getId()))
                            .voted(isVoted)
                            .build();
                })
                .collect(Collectors.toList());
    }


    public ResponseEntity<?> getMyInvitations(UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new DefaultException(ErrorCode.NOT_FOUND,"사용자를 찾을 수 없습니다"));

        // 작성 중인 초대장 조회
        List<MyInvitationOverViewRes.InvitationRes> draftingInvitations = convertToInvitationRes(
                invitationRepository.findBySenderAndTempSavedTrue(user)
        );

        int draftCount = draftingInvitations.size(); //작성중인 초대장 개수

        // 보낸 초대장 최신순 3개 조회
         List<MyInvitationOverViewRes.InvitationRes> sentInvitations = convertToInvitationRes(
                 invitationRepository.findTop3BySenderAndTempSavedFalseOrderByModifiedDateDesc(user)
         );

        // 받은 초대장 최신순 3개
        List<MyInvitationOverViewRes.InvitationRes> receivedInvitations = convertToInvitationRes(
                receivedInvitationRepository.findTop3ByReceiverOrderByCreatedDateDesc(user)
                        .stream()
                        .map(receivedInvitation -> receivedInvitation.getInvitation())
                        .toList()
        );

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

        List<MyInvitationOverViewRes.InvitationRes> sentInvitations = convertToInvitationRes(
                invitationRepository.findBySenderAndYearAndTempSavedFalse(user, year)
        );

        // 전체 보낸 초대장 개수
        int totalSentInvitations = sentInvitations.size();

        return createInvitationListResponse(sentInvitations, totalSentInvitations);
    }

    public ResponseEntity<?> getReceivedInvitationsByYear(UserPrincipal userPrincipal, int year) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new DefaultException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다"));

        List<MyInvitationOverViewRes.InvitationRes> receivedInvitations = convertToInvitationRes(
                receivedInvitationRepository.findByReceiverAndYear(user, year)
                        .stream()
                        .map(receivedInvitation -> receivedInvitation.getInvitation())
                        .toList()
        );

        // 전체 받은 초대장 총 개수
        int totalReceivedCount = receivedInvitations.size();

        return createInvitationListResponse(receivedInvitations, totalReceivedCount);

    }

    // 초대장 리스트 반환 메서드
    private List<MyInvitationOverViewRes.InvitationRes> convertToInvitationRes(List<Invitation> invitations) {
        return invitations.stream()
                .map(invitation -> MyInvitationOverViewRes.InvitationRes.builder()
                        .invitationId(invitation.getId())
                        .title(invitation.getTitle())
                        .cardImage(invitation.getCardImage())
                        .date(invitation.getCreatedDate())
                        .build())
                .toList();
    }

    // InvitationListRes 응답 생성 메서드
    private ResponseEntity<InvitationListRes> createInvitationListResponse(List<MyInvitationOverViewRes.InvitationRes> invitations, int totalCount) {
        InvitationListRes response = InvitationListRes.builder()
                .totalInvitations(totalCount)
                .invitations(invitations)
                .build();
        return ResponseEntity.ok(response);
    }
    @Transactional
    public ResponseEntity<?> deleteSentInvitation(Long invitationId, UserPrincipal userPrincipal) {
        // 초대장 조회
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new DefaultException(ErrorCode.NOT_FOUND, "해당 초대장이 존재하지 않습니다."));

        // 보낸 사람 확인
        if (!invitation.getSender().getId().equals(userPrincipal.getId())) {
            throw new DefaultException(ErrorCode.INVALID_AUTHENTICATION, "본인이 보낸 초대장만 삭제할 수 있습니다.");
        }

        // 관련 데이터 삭제
        // 1. Feedback 삭제
        feedbackRepository.deleteByInvitation(invitation);

        // 2. Attendance 삭제
        attendanceRepository.deleteByInvitation(invitation);

        // 3. ScheduleVote와 연관된 VoterNickname삭제
        List<ScheduleVote> scheduleVotes = scheduleVoteRepository.findByInvitation(invitation);
        for(ScheduleVote scheduleVote : scheduleVotes){
            scheduleVoterRepository.deleteByScheduleVote(scheduleVote);
        }

        scheduleVoteRepository.deleteByInvitation(invitation);

        // 4. ReceivedInvitation삭제
        receivedInvitationRepository.deleteByInvitation(invitation);

        // 5. Block 삭제(상속된엔티티포함)
        blockRepository.deleteByInvitation(invitation);

        // 6. 초대장 삭제
        invitationRepository.delete(invitation);

        return ResponseEntity.noContent().build();

    }
    @Transactional
    public ResponseEntity<?> deleteReceivedInvitation(Long invitationId, UserPrincipal userPrincipal) {
        // 현재 사용자 조회
        User receiver = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new DefaultException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // 받은 초대장 데이터 조회
        ReceivedInvitation receivedInvitation = receivedInvitationRepository.findByReceiverAndInvitationId(receiver, invitationId)
                .orElseThrow(() -> new DefaultException(ErrorCode.NOT_FOUND, "해당 초대장이 존재하지 않거나 수신하지 않았습니다."));

        // 받은 초대장에서만 삭제
        receivedInvitationRepository.delete(receivedInvitation);

        return ResponseEntity.noContent().build();

    }

    private User validateUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        DefaultAssert.isOptionalPresent(userOptional, "사용자가 존재하지 않습니다.");
        return userOptional.get();
    }

}



