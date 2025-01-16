package depth.main.wishwesee.domain.vote.controller;

import depth.main.wishwesee.domain.invitation.dto.request.CreateFeedbackReq;
import depth.main.wishwesee.domain.vote.dto.request.AttendanceVoteReq;
import depth.main.wishwesee.domain.vote.dto.response.AttendanceVoteStatusRes;
import depth.main.wishwesee.domain.vote.service.VoteService;
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

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/{invitationId}/attendance-vote")
public class VoteController {

    private final VoteService voteService;

    @Operation(summary = "참석 조사 현황 조회", description = "참석 조사 현황을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AttendanceVoteStatusRes.class))}),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping()
    public ResponseEntity<depth.main.wishwesee.global.payload.ApiResponse> getAttendanceVoteStatus(
            @Parameter(description = "초대장의 id를 입력해주세요.", required = true) @PathVariable Long invitationId
    ) {
        return voteService.getAttendanceVoteStatus(invitationId);
    }

    @Operation(summary = "내 참석 투표 결과 조회", description = "내 참석 조사 투표 결과를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AttendanceVoteStatusRes.class))}),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/my-vote")
    public ResponseEntity<depth.main.wishwesee.global.payload.ApiResponse> getMyAttendanceVote(
            @Parameter(description = "Accesstoken을 입력해주세요.") @CurrentUser Optional<UserPrincipal> userPrincipal,
            @Parameter(description = "초대장의 id를 입력해주세요.", required = true) @PathVariable Long invitationId,
            @Parameter(description = "닉네임을 입력해주세요. 회원은 닉네임을 입력하지 않습니다.") @RequestParam(required = false) String nickname
    ) {
        return voteService.getMyAttendanceVote(userPrincipal, invitationId, nickname);
    }

    @Operation(summary = "참석 여부 투표", description = "초대 참석 여부를 투표합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "투표 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))}),
            @ApiResponse(responseCode = "400", description = "투표 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping()
    public ResponseEntity<?> voteAttendance(
            @Parameter(description = "Accesstoken을 입력해주세요.") @CurrentUser Optional<UserPrincipal> userPrincipal,
            @Parameter(description = "초대장의 id를 입력해주세요.", required = true) @PathVariable Long invitationId,
            @Parameter(description = "Schemas의 CreateFeedbackReq를 확인해주세요. 후기에 들어갈 내용입니다.") @RequestBody AttendanceVoteReq attendanceVoteReq
    ) {
        return voteService.voteAttendance(userPrincipal, invitationId, attendanceVoteReq);
    }

    @Operation(summary = "닉네임 중복여부 확인", description = "투표 전, 닉네임 중복여부를 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "중복 여부 확인 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))}),
            @ApiResponse(responseCode = "400", description = "중복 여부 확인 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/check")
    public ResponseEntity<depth.main.wishwesee.global.payload.ApiResponse> checkNickname(
            @Parameter(description = "초대장의 id를 입력해주세요.", required = true) @PathVariable Long invitationId,
            @Parameter(description = "중복을 확인할 닉네임을 입력해주세요.") @RequestParam String nickname
    ) {
        return voteService.checkDuplicateNickname(invitationId, nickname);
    }

}
