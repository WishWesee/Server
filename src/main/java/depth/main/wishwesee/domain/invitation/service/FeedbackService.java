package depth.main.wishwesee.domain.invitation.service;

import depth.main.wishwesee.domain.invitation.domain.Feedback;
import depth.main.wishwesee.domain.invitation.domain.Invitation;
import depth.main.wishwesee.domain.invitation.domain.ReceivedInvitation;
import depth.main.wishwesee.domain.invitation.domain.repository.FeedbackRepository;
import depth.main.wishwesee.domain.invitation.domain.repository.InvitationRepository;
import depth.main.wishwesee.domain.invitation.domain.repository.ReceivedInvitationRepository;
import depth.main.wishwesee.domain.invitation.dto.request.CreateFeedbackReq;
import depth.main.wishwesee.domain.invitation.dto.response.FeedbackListRes;
import depth.main.wishwesee.domain.invitation.dto.response.FeedbackRes;
import depth.main.wishwesee.domain.invitation.dto.response.NotificationFeedbackRes;
import depth.main.wishwesee.domain.s3.service.S3Uploader;
import depth.main.wishwesee.domain.user.domain.User;
import depth.main.wishwesee.domain.user.domain.repository.UserRepository;
import depth.main.wishwesee.global.DefaultAssert;
import depth.main.wishwesee.global.config.security.token.UserPrincipal;
import depth.main.wishwesee.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedbackService {

    private final UserRepository userRepository;
    private final FeedbackRepository feedbackRepository;
    private final InvitationRepository invitationRepository;
    private final ReceivedInvitationRepository receivedInvitationRepository;
    private final S3Uploader s3Uploader;


    // 후기 등록
    @Transactional
    public ResponseEntity<Void> saveFeedback(UserPrincipal userPrincipal, Long invitationId, MultipartFile image, CreateFeedbackReq createFeedbackReq) {
        User user = validateUserById(userPrincipal.getId());
        Invitation invitation = validateInvitationById(invitationId);
        if (invitation.getSender() != user) {
            DefaultAssert.isTrue(receivedInvitationRepository.existsByInvitationAndReceiver(invitation, user), "내가 받은 초대장이 아닙니다.");
        }
        DefaultAssert.isTrue(checkWritableFeedback(invitation), "후기 작성 가능 기간이 아닙니다.");
        String imageUrl = uploadImageIfPresent(image);
        String content = validateContentIfPresent(createFeedbackReq);
        Feedback feedback = Feedback.builder()
                .image(imageUrl)
                .content(content)
                .user(user)
                .invitation(invitation)
                .build();
        feedbackRepository.save(feedback);
        return ResponseEntity.ok().build();
    }

    private String uploadImageIfPresent(MultipartFile image) {
        return image != null && !image.isEmpty() ? s3Uploader.uploadFile(image) : null;
    }

    private String validateContentIfPresent(CreateFeedbackReq createFeedbackReq) {
        if (createFeedbackReq == null) {
            return null;
        }
        String content = (createFeedbackReq.getContent() != null && !createFeedbackReq.getContent().isEmpty())
                ? createFeedbackReq.getContent()
                : null;
        if (content != null && content.length() > 50) {
            throw new InvalidParameterException("후기는 50자까지 작성 가능합니다.");
        }
        return content;
    }

    // 후기 삭제
    @Transactional
    public ResponseEntity<Void> deleteFeedback(UserPrincipal userPrincipal, Long invitationId, Long feedbackId) {
        User user = validateUserById(userPrincipal.getId());
        Invitation invitation = validateInvitationById(invitationId);
        Feedback feedback = validateFeedbackById(feedbackId);
        DefaultAssert.isTrue(invitation.getSender() == user || feedback.getUser() == user, "삭제 권한이 없습니다.");
        deleteFeedbackImage(feedback.getImage());
        feedbackRepository.delete(feedback);
        return ResponseEntity.noContent().build();
    }

    private void deleteFeedbackImage(String image) {
        if (image != null) {
            String imageName = s3Uploader.extractImageNameFromUrl(image);
            s3Uploader.deleteFile(imageName);
        }
    }

    // 후기 조회
    public ResponseEntity<ApiResponse> getFeedbacks(UserPrincipal userPrincipal, Long invitationId) {
        User user = validateUserById(userPrincipal.getId());
        Invitation invitation = validateInvitationById(invitationId);
        boolean isSender;
        if (invitation.getSender() != user) {
            isSender = false;
            DefaultAssert.isTrue(receivedInvitationRepository.existsByInvitationAndReceiver(invitation, user), "내가 받은 초대장이 아닙니다.");
        } else {
            isSender = true;
        }
        List<Feedback> feedbacks = feedbackRepository.findByInvitationOrderByCreatedDateDesc(invitation);
        List<FeedbackRes> feedbackResList = feedbacks.stream().map(
                feedback -> FeedbackRes.builder()
                        .feedbackId(feedback.getId())
                        .content(feedback.getContent() != null ? feedback.getContent() : null)
                        .image(feedback.getImage() != null ? feedback.getImage() : null)
                        .isDeletable(isSender || feedback.getUser() == user)
                        .build()
        ).toList();
        int feedbackCount = feedbackRepository.countByInvitation(invitation);
        FeedbackListRes feedbackListRes = FeedbackListRes.builder()
                .count(feedbackCount)
                .writable(checkWritableFeedback(invitation))
                .feedbackResList(feedbackResList)
                .build();
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .check(true)
                        .information(feedbackListRes)
                .build()
        );
    }

    private LocalDateTime formatInvitationDate(Invitation invitation) {
        LocalDateTime dateTime;
        if (invitation.getEndDate() == null) {
            dateTime = combineDateAndTime(invitation.getStartDate(), invitation.getStartTime());
        } else {
            dateTime = combineDateAndTime(invitation.getEndDate(), invitation.getEndTime());
        }
        return dateTime;
    }

    private LocalDateTime combineDateAndTime(LocalDate date, LocalTime time) {
        LocalTime defaultTime = LocalTime.MIDNIGHT; // 00:00:00
        return LocalDateTime.of(date, time != null ? time : defaultTime);
    }

    public boolean checkWritableFeedback(Invitation invitation) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = formatInvitationDate(invitation);
        return isAfterInvitationDate(endDate, now);
    }

    private boolean checkNotificationFeedback(Invitation invitation) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneDayAfterEndDate = formatInvitationDate(invitation).plusDays(1);
        return isAfterInvitationDate(oneDayAfterEndDate, now);
    }

    private boolean isAfterInvitationDate(LocalDateTime dateTime, LocalDateTime now) {
        // 요청 날짜가 비교 날짜 이후인지 확인
        return now.isAfter(dateTime);
    }

    public ResponseEntity<ApiResponse> notificationWritableFeedback(UserPrincipal userPrincipal) {
        User user = validateUserById(userPrincipal.getId());
        LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);
        List<ReceivedInvitation> receivedInvitations = receivedInvitationRepository.findByReceiverAndCreatedDateAfter(user, twoWeeksAgo);
        List<NotificationFeedbackRes> notificationFeedbackRes = receivedInvitations.stream()
                .map(receivedInvitation -> {
                    Invitation invitation = receivedInvitation.getInvitation();
                    if (checkNotificationFeedback(invitation) && !feedbackRepository.existsByInvitationAndUser(invitation, user)) {
                        return NotificationFeedbackRes.builder()
                                .invitationId(invitation.getId())
                                .invitationToken(invitation.getToken())
                                .title(invitation.getTitle())
                                .build();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();
        return ResponseEntity.ok(ApiResponse.builder()
                .check(true)
                .information(notificationFeedbackRes)
                .build());
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

    private Feedback validateFeedbackById(Long feedbackId) {
        Optional<Feedback> feedbackOptional = feedbackRepository.findById(feedbackId);
        DefaultAssert.isOptionalPresent(feedbackOptional, "후기가 존재하지 않습니다.");
        return feedbackOptional.get();
    }
}
