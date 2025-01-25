package depth.main.wishwesee.domain.invitation.controller;

import depth.main.wishwesee.domain.invitation.dto.request.CreateFeedbackReq;
import depth.main.wishwesee.domain.invitation.dto.request.InvitationReq;
import depth.main.wishwesee.domain.invitation.dto.response.*;
import depth.main.wishwesee.domain.invitation.dto.request.SaveInvitationReq;
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
            @ApiResponse(responseCode = "200", description = "후기 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FeedbackListRes.class))}),
            @ApiResponse(responseCode = "400", description = "후기 조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/{invitationId}/feedback")
    public ResponseEntity<depth.main.wishwesee.global.payload.ApiResponse> getFeedbacks(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "초대장의 id를 입력해주세요.", required = true) @PathVariable Long invitationId
    ) {
        return feedbackService.getFeedbacks(userPrincipal, invitationId);
    }

    @Operation(summary = "후기 알림이 가능한 초대장 목록 조회", description = "내가 받은 초대장 중 후기 알림이 가능한 초대장 목록을 조회합니다. 이미 후기를 작성한 경우 보내지 않습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "목록 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = NotificationFeedbackRes.class))}),
            @ApiResponse(responseCode = "400", description = "목록 조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/my-invitations/feedback")
    public ResponseEntity<depth.main.wishwesee.global.payload.ApiResponse> getWritableFeedbackNotification(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ) {
        return feedbackService.notificationWritableFeedback(userPrincipal);
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
            @Parameter(description = "저장할 초대장의 ID를 입력해주세요.", required = true) @RequestBody SaveInvitationReq saveInvitationReq,
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal) {

        return invitationService.saveReceivedInvitation(saveInvitationReq, userPrincipal);
    }

    @Operation(summary = "완성된 초대장 조회", description = "완성된 초대장의 id를 통해 초대장의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "완성된 초대장 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CompletedInvitationRes.class))}),
            @ApiResponse(responseCode = "404", description = "초대장을 찾을 수 없음", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping("/{invitationId}")
    public ResponseEntity<?> getCompletedInvitation(
            @Parameter(description = "조회할 완성된 초대장의ID", required = true) @PathVariable Long invitationId,
            @Parameter(description = "Accesstoken을 입력해주세요.", required = false) @CurrentUser UserPrincipal userPrincipal
    ) {

        return invitationService.getCompletedInvitation(invitationId, userPrincipal);
    }

    @Operation(summary = "나의 초대장 목록 조회", description = "작성 중인 초대장, 보낸 초대장 3개, 받은 초대장 3개를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "나의 초대장 목록 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MyInvitationOverViewRes.class))}),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "사용자 정보 없음", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping("/my-invitations")
    public ResponseEntity<?> getMyInvitations(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ){
        return invitationService.getMyInvitations(userPrincipal);
    }
    @Operation(summary = "연도별 내가 보낸 초대장 목록 조회", description = "연도별로 내가 보낸 초대장 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "연도별 내가 보낸 초대장 목록 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = InvitationListRes.class))}),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "사용자 정보 없음", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping("/sent/{year}")
    public ResponseEntity<?> getSentInvitationsByYear(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "연도를 입력해주세요", required = true) @PathVariable int year
    ) {
        return invitationService.getSentInvitationByYear(userPrincipal, year);
    }

    @Operation(summary = "연도별 내가 받은 초대장 목록 조회", description = "연도별로 내가 받은 초대장 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "연도별 내가 받은 초대장 목록 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = InvitationListRes.class))}),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "사용자 정보 없음", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping("/received/{year}")
    public ResponseEntity<?> getReceivedInvitationsByYear(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "연도를 입력해주세요", required = true) @PathVariable int year
    ) {
        return invitationService.getReceivedInvitationsByYear(userPrincipal, year);
    }
    @Operation(summary = "보낸 초대장 삭제", description = "사용자가 보낸 초대장을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "성공적으로 삭제됨"),
            @ApiResponse(responseCode = "404", description = "초대장 또는 사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 (삭제 권한 없음)")
    })
    @DeleteMapping("/sent/{invitationId}")
    public ResponseEntity<?> deleteSentInvitation(
            @Parameter(description = "삭제할 초대장의 ID", required = true) @PathVariable Long invitationId,
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal) {

        return invitationService.deleteSentInvitation(invitationId, userPrincipal);
    }

    @Operation(summary = "받은 초대장 삭제", description = "사용자가 받은 초대장을 삭제합니다. 삭제 시 초대장 자체는 삭제되지 않고 사용자의 수신 목록에서만 삭제됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "성공적으로 삭제됨"),
            @ApiResponse(responseCode = "404", description = "해당 초대장이 존재하지 않거나 수신하지 않음")
    })
    @DeleteMapping("/received/{invitationId}")
    public ResponseEntity<?> deleteReceivedInvitation(
            @Parameter(description = "삭제할 초대장의 ID", required = true)  @PathVariable Long invitationId,
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal) {

        return invitationService.deleteReceivedInvitation(invitationId, userPrincipal);
    }


}
