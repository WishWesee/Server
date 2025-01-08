package depth.main.wishwesee.domain.invitation.service;

import depth.main.wishwesee.domain.invitation.domain.Feedback;
import depth.main.wishwesee.domain.invitation.domain.Invitation;
import depth.main.wishwesee.domain.invitation.domain.ReceivedInvitation;
import depth.main.wishwesee.domain.invitation.domain.repository.FeedbackRepository;
import depth.main.wishwesee.domain.invitation.domain.repository.ReceivedInvitationRepository;
import depth.main.wishwesee.domain.invitation.dto.request.CreateFeedbackReq;
import depth.main.wishwesee.domain.s3.service.S3Uploader;
import depth.main.wishwesee.domain.user.domain.User;
import depth.main.wishwesee.domain.user.domain.repository.UserRepository;
import depth.main.wishwesee.global.DefaultAssert;
import depth.main.wishwesee.global.config.security.token.UserPrincipal;
import depth.main.wishwesee.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedbackService {

    private final UserRepository userRepository;
    private final FeedbackRepository feedbackRepository;
    private final ReceivedInvitationRepository receivedInvitationRepository;
    private final S3Uploader s3Uploader;


    // 후기 등록
    @Transactional
    public void saveFeedback(UserPrincipal userPrincipal, Long receivedInvitationId, Optional<MultipartFile> image, CreateFeedbackReq createFeedbackReq) {
        User user = validateUserById(userPrincipal.getId());
        ReceivedInvitation receivedInvitation = validateReceivedInvitation(receivedInvitationId);
        DefaultAssert.isTrue(receivedInvitation.getReceiver() == user || receivedInvitation.getInvitation().getSender() == user, "잘못된 접근입니다.");
        String imageUrl = uploadImageIfPresent(image);
        String content = validateContentIfPresent(createFeedbackReq);
        Feedback feedback = Feedback.builder()
                .image(imageUrl)
                .content(content)
                .receivedInvitation(receivedInvitation)
                .build();
        feedbackRepository.save(feedback);
    }

    private String uploadImageIfPresent(Optional<MultipartFile> image) {
        return (image != null && image.isPresent()) ? s3Uploader.uploadFile(image.get()) : null;
    }

    private String validateContentIfPresent(CreateFeedbackReq createFeedbackReq) {
        String content = (createFeedbackReq.getContent() != null && !createFeedbackReq.getContent().isEmpty()) ? createFeedbackReq.getContent() : null;
        if (content != null && content.length() > 50) {
            throw new InvalidParameterException("후기는 50자까지 작성 가능합니다.");
        }
        return content;
    }


    // 후기 삭제


    // 후기 조회


    private User validateUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        DefaultAssert.isOptionalPresent(userOptional);
        return userOptional.get();
    }

    private ReceivedInvitation validateReceivedInvitation(Long receivedInvitationId) {
        Optional<ReceivedInvitation> receivedInvitationOptional = receivedInvitationRepository.findById(receivedInvitationId);
        DefaultAssert.isOptionalPresent(receivedInvitationOptional);
        return receivedInvitationOptional.get();
    }
}
