package depth.main.wishwesee.domain.invitation.controller;

import depth.main.wishwesee.domain.invitation.dto.request.CreateFeedbackReq;
import depth.main.wishwesee.domain.invitation.dto.request.InvitationReq;
import depth.main.wishwesee.domain.invitation.service.FeedbackService;
import depth.main.wishwesee.global.config.security.token.CurrentUser;
import depth.main.wishwesee.global.config.security.token.UserPrincipal;
import depth.main.wishwesee.global.payload.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import depth.main.wishwesee.domain.invitation.service.InvitationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@Tag(name = "Invitation", description = "Invitation API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/invitation")
public class InvitationController {
    private final InvitationService invitationService;
    private final FeedbackService feedbackService;

    @Operation(summary = "초대장 작성 완료", description = "임시저장된 초대장 혹은 새로운 초대장 작성을 완료합니다.")
    @PostMapping
    public ResponseEntity<?> createInvitation(
            @Parameter(description = "초대장에 들어갈 데이터를 넣어주세요. Schemas의 InvitationReq를 참고해주세요.", required = true) @RequestPart("invitation")@Valid InvitationReq invitationReq,
            @Parameter(description = "초대장 카드 이미지를 넣어주세요.", required = true ) @RequestPart(value = "cardImage", required = true) MultipartFile cardImage,
            @Parameter(description = "초대장에 들어갈 사진 목록을 넣어주세요.", required = false) @RequestPart(value = "photoImages", required = false) List<MultipartFile> photoImages,
            @Parameter(description = "Accesstoken을 입력해주세요.", required = false) @CurrentUser UserPrincipal userPrincipal){

        return invitationService.publishInvitation(invitationReq, cardImage, photoImages, userPrincipal);
    }
    @Operation(summary = "초대장 임시 저장", description = "임시저장된 초대장 혹은 새로운 초대장을 임시저장합니다.")
    @PostMapping(value = "/save-temporary")
    public ResponseEntity<?> saveTemporaryInvitation(
            @Parameter(description = "초대장에 들어갈 데이터를 넣어주세요. Schemas의 InvitationReq를 참고해주세요.", required = true) @RequestPart("invitation") @Valid InvitationReq invitationReq,
            @Parameter(description = "초대장 카드 이미지를 넣어주세요.", required = false ) @RequestPart(value = "cardImage", required = false) MultipartFile cardImage,
            @Parameter(description = "초대장에 들어갈 사진 목록을 넣어주세요.", required = false) @RequestPart(value = "photoImages", required = false) List<MultipartFile> photoImages,
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal) {

        return invitationService.saveTemporaryInvitation(invitationReq, cardImage, photoImages, userPrincipal);
    }

    @Operation(summary = "후기 작성", description = "내가 받은/보낸 초대장의 후기를 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "후기 등록 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))}),
            @ApiResponse(responseCode = "400", description = "후기 등록 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping(value = "/{invitationId}/feedback")
    public ResponseEntity<Void> saveFeedback(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "초대장의 id를 입력해주세요.", required = true) @PathVariable Long invitationId,
            @Parameter(description = "후기 이미지입니다. 1장만 입력 가능합니다.") @RequestPart(required = false) MultipartFile image,
            @Parameter(description = "Schemas의 CreateFeedbackReq를 확인해주세요. 후기에 들어갈 내용입니다.") @RequestPart(required = false) CreateFeedbackReq createFeedbackReq
    ) {
        return feedbackService.saveFeedback(userPrincipal, invitationId, image, createFeedbackReq);
    }

    @Operation(summary = "후기 조회", description = "내가 받은/보낸 초대장의 후기를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "후기 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))}),
            @ApiResponse(responseCode = "400", description = "후기 조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/{invitationId}/feedback")
    public ResponseEntity<depth.main.wishwesee.global.payload.ApiResponse> getFeedbacks(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "초대장의 id를 입력해주세요.", required = true) @PathVariable Long invitationId
    ) {
        return feedbackService.getFeedbacks(userPrincipal, invitationId);
    }

    @Operation(summary = "후기 삭제", description = "내가 받은/보낸 초대장의 후기를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "후기 삭제 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))}),
            @ApiResponse(responseCode = "400", description = "후기 삭제 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @DeleteMapping ("/{invitationId}/feedback/{feedbackId}")
    public ResponseEntity<Void> deleteFeedback(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "초대장의 id를 입력해주세요.", required = true) @PathVariable Long invitationId,
            @Parameter(description = "후기의 id를 입력해주세요.", required = true) @PathVariable Long feedbackId
    ) {
        return feedbackService.deleteFeedback(userPrincipal, invitationId, feedbackId);
    }


    @Operation(summary = "초대장 받기", description = "초대장을 받고 받은 초대장 목록에 저장합니다. " +
            "만약 이미 받은 초대장이거나 본인이 작성한 초대장일 경우 에러 메시지를 반환합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "초대장 받기 성공"),
            @ApiResponse(responseCode = "400", description = "본인이 작성한 초대장"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "해당 초대장이나 사용자 정보를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "이미 저장된 초대장")
    })
    @PostMapping("/save-received")
    public ResponseEntity<?> saveReceivedInvitation(
            @Parameter(description = "저장할 초대장의 UUID토큰을 입력해주세요.", required = true) @RequestParam String invitationToken,
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal) {

        return invitationService.saveReceivedInvitation(invitationToken, userPrincipal);
    }
}
