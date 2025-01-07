package depth.main.wishwesee.domain.invitation.controller;

import depth.main.wishwesee.domain.invitation.service.InvitationService;
import depth.main.wishwesee.global.config.security.token.CurrentUser;
import depth.main.wishwesee.global.config.security.token.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/invitation")
public class InvitationController {
    private final InvitationService invitationService;
    @PostMapping("/save-received")
    public ResponseEntity<?> saveReceivedInvitation(@RequestParam Long invitationId,
                                                    @CurrentUser UserPrincipal userPrincipal) {

        return invitationService.saveReceivedInvitation(invitationId, userPrincipal);
    }
}
