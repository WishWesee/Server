package depth.main.wishwesee.domain.invitation.service;

import depth.main.wishwesee.domain.invitation.domain.Invitation;
import depth.main.wishwesee.domain.invitation.domain.ReceivedInvitation;
import depth.main.wishwesee.domain.invitation.domain.repository.InvitationRepository;
import depth.main.wishwesee.domain.invitation.domain.repository.ReceivedInvitationRepository;
import depth.main.wishwesee.domain.user.domain.User;
import depth.main.wishwesee.domain.user.domain.repository.UserRepository;
import depth.main.wishwesee.global.config.security.token.UserPrincipal;
import depth.main.wishwesee.global.exception.DefaultException;
import depth.main.wishwesee.global.payload.ErrorCode;
import depth.main.wishwesee.global.payload.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvitationService {
    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final ReceivedInvitationRepository receivedInvitationRepository;
    @Transactional
    public ResponseEntity<?> saveReceivedInvitation(Long invitationId, UserPrincipal userPrincipal) {
        // 현재 사용자 정보 가져오기
        User receiver = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new DefaultException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // 초대장 정보 가져오기
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new DefaultException(ErrorCode.NOT_FOUND, "해당 초대장이 존재하지 않습니다."));

        // 작성자 본인 확인
        if(invitation.getSender().getId().equals(receiver.getId())){
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
}
