package depth.main.wishwesee.domain.invitation.controller;

import depth.main.wishwesee.domain.auth.dto.res.AuthRes;
import depth.main.wishwesee.domain.invitation.dto.req.CreateFeedbackReq;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/invitation")
public class InvitationController {

    private final FeedbackService feedbackService;

    @Operation(summary = "후기 작성", description = "내가 받은/보낸 초대장의 후기를 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "후기 등록 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class) ) } ),
            @ApiResponse(responseCode = "400", description = "후기 등록 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PostMapping(value = "/{receivedInvitationId}/feedback")
    public ResponseEntity<?> saveFeedback(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "받은 초대장의 id를 입력해주세요.", required = true) @PathVariable Long receivedInvitationId,
            @Parameter(description = "후기 이미지입니다. 1장만 입력 가능합니다.") @RequestPart Optional<MultipartFile> image,
            @Parameter(description = "Schemas의 CreateFeedbackReq를 확인해주세요. 후기에 들어갈 내용입니다.") @RequestPart CreateFeedbackReq createFeedbackReq
    ) {
        feedbackService.saveFeedback(userPrincipal, receivedInvitationId, image, createFeedbackReq);
        return ResponseEntity.ok().build();
    }
}
