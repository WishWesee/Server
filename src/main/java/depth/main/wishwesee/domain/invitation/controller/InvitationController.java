package depth.main.wishwesee.domain.invitation.controller;

import depth.main.wishwesee.domain.invitation.dto.request.InvitationReq;
import depth.main.wishwesee.domain.invitation.service.InvitationService;
import depth.main.wishwesee.global.config.security.token.CurrentUser;
import depth.main.wishwesee.global.config.security.token.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Invitation", description = "Invitation API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/invitation")
public class InvitationController {
    private final InvitationService invitationService;
    @Operation(summary = "초대장 작성 완료", description = "임시저장된 초대장 혹은 새로운 초대장 작성을 완료합니다.")
    @PostMapping(value = "/publish", consumes = {"multipart/form-data"})
    public ResponseEntity<?> createInvitation(
            @Parameter(description = "초대장에 들어갈 데이터를 넣어주세요.(Schemas의 InvitationReq를 참고해주세요.", required = true) @RequestPart("invitation")@Valid InvitationReq invitationReq,
            @Parameter(description = "초대장 카드 이미지를 넣어주세요.", required = true ) @RequestPart(value = "cardImage", required = true) MultipartFile cardImage,
            @Parameter(description = "초대장에 들어갈 사진 목록을 넣어주세요.", required = false) @RequestPart(value = "photoImages", required = false) List<MultipartFile> photoImages,
            @Parameter(description = "Accesstoken을 입력해주세요.", required = false) @CurrentUser UserPrincipal userPrincipal){

        return invitationService.publishInvitation(invitationReq, cardImage, photoImages, userPrincipal);
    }
    @Operation(summary = "초대장 임시 저장", description = "임시저장된 초대장 혹은 새로운 초대장을 임시저장합니다.")
    @PostMapping(value = "/save-temporary", consumes = {"multipart/form-data"})
    public ResponseEntity<?> saveTemporaryInvitation(
            @Parameter(description = "초대장에 들어갈 데이터를 넣어주세요.(Schemas의 InvitationReq를 참고해주세요.", required = true) @RequestPart("invitation") @Valid InvitationReq invitationReq,
            @Parameter(description = "초대장 카드 이미지를 넣어주세요.", required = false ) @RequestPart(value = "cardImage", required = false) MultipartFile cardImage,
            @Parameter(description = "초대장에 들어갈 사진 목록을 넣어주세요.", required = false) @RequestPart(value = "photoImages", required = false) List<MultipartFile> photoImages,
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal) {

        return invitationService.saveTemporaryInvitation(invitationReq, cardImage, photoImages, userPrincipal);
    }
}
