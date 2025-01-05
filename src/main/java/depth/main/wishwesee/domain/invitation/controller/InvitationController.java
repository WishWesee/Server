package depth.main.wishwesee.domain.invitation.controller;

import depth.main.wishwesee.domain.invitation.dto.request.InvitationReq;
import depth.main.wishwesee.domain.invitation.service.InvitationService;
import depth.main.wishwesee.global.config.security.token.CurrentUser;
import depth.main.wishwesee.global.config.security.token.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/invitation")
public class InvitationController {
    private final InvitationService invitationService;
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> createInvitation(
            @RequestPart("invitation")@Valid InvitationReq invitationReq,
            @RequestPart(value = "cardImage", required = false) MultipartFile cardImage,
            @RequestPart(value = "photoImages", required = false) List<MultipartFile> photoImages,
            @CurrentUser UserPrincipal userPrincipal){

        return invitationService.publishInvitation(invitationReq, cardImage, photoImages, userPrincipal);
    }

}
