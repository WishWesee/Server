package depth.main.wishwesee.domain.vote.controller;

import depth.main.wishwesee.domain.vote.dto.request.VoteScheduleReq;
import depth.main.wishwesee.domain.vote.dto.response.*;
import depth.main.wishwesee.domain.vote.service.ScheduleVoteService;
import depth.main.wishwesee.global.config.security.token.CurrentUser;
import depth.main.wishwesee.global.config.security.token.UserPrincipal;
import depth.main.wishwesee.global.payload.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Schedule Vote", description = "Schedule Vote API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/invitation/{invitationId}/schedule")
public class ScheduleVoteController {

    private final ScheduleVoteService scheduleVoteService;

    @Operation(summary = "(비회원) 특정 닉네임의 참석 조사 결과 조회", description = "비회원일 경우, 이름 입력 후 해당 이름의 참석 조사 투표 결과를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MyScheduleVoteIdsRes.class))}),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/guest")
    public ResponseEntity<depth.main.wishwesee.global.payload.ApiResponse> getScheduleVoteByNickname(
            @Parameter(description = "초대장의 id를 입력해주세요.", required = true) @PathVariable Long invitationId,
            @Parameter(description = "닉네임을 입력해주세요.") @RequestParam String nickname
    ) {
        return scheduleVoteService.getScheduleVoteByNickname(invitationId, nickname);
    }

    @Operation(summary = "닉네임 중복여부 확인", description = "닉네임 중복여부를 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "중복 여부 확인 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CheckNicknameRes.class))}),
            @ApiResponse(responseCode = "400", description = "중복 여부 확인 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/check")
    public ResponseEntity<depth.main.wishwesee.global.payload.ApiResponse> checkNickname(
            @Parameter(description = "초대장의 id를 입력해주세요.", required = true) @PathVariable Long invitationId,
            @Parameter(description = "중복을 확인할 닉네임을 입력해주세요.") @RequestParam String nickname
    ) {
        return scheduleVoteService.checkDuplicateNickname(invitationId, nickname);
    }

    @Operation(summary = "일정 투표", description = "일정 투표 및 투표를 수정합니다. 비회원의 경우 이미 존재하는 닉네임 입력 시 해당 닉네임의 투표 수정이 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "투표 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))}),
            @ApiResponse(responseCode = "400", description = "투표 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping()
    public ResponseEntity<Void> voteSchedule(
            @Parameter(description = "Accesstoken을 입력해주세요.") @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "초대장의 id를 입력해주세요.", required = true) @PathVariable Long invitationId,
            @Parameter(description = "Schemas의 VoteScheduleReq를 확인해주세요. 일정 투표 id의 리스트와 닉네임(비회원)입니다.") @RequestBody VoteScheduleReq voteScheduleReq
    ) {
        return scheduleVoteService.voteSchedule(userPrincipal, invitationId, voteScheduleReq);
    }

    @Operation(summary = "투표자 목록 조회", description = "투표자 목록을 조회합니다. 작성자만 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ScheduleVoterRes.class))}),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/{scheduleVoteId}/voters")
    public ResponseEntity<depth.main.wishwesee.global.payload.ApiResponse> getScheduleVoterList(
            @Parameter(description = "Accesstoken을 입력해주세요.") @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "초대장의 id를 입력해주세요.", required = true) @PathVariable Long invitationId,
            @Parameter(description = "확인하고자 하는 일정 투표의 id를 입력해주세요.", required = true) @PathVariable Long scheduleVoteId
    ) {
        return scheduleVoteService.getVoterList(userPrincipal, invitationId, scheduleVoteId);
    }

    @Operation(summary = "일정 투표 확정", description = "일정 투표 종료 후, 일정을 확정합니다. 작성자만 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))}),
            @ApiResponse(responseCode = "400", description = "수정 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PutMapping()
    public ResponseEntity<?> updateInvitationSchedule(
            @Parameter(description = "Accesstoken을 입력해주세요.") @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "초대장의 id를 입력해주세요.", required = true) @PathVariable Long invitationId,
            @Parameter(description = "확정하고자 하는 일정 투표의 id를 입력해주세요.", required = true) @RequestParam Long scheduleVoteId
    ) {
        return scheduleVoteService.updateSchedule(userPrincipal, invitationId, scheduleVoteId);
    }
}
