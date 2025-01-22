package depth.main.wishwesee.domain.vote.controller;

import depth.main.wishwesee.domain.vote.dto.request.AttendanceVoteReq;
import depth.main.wishwesee.domain.vote.dto.response.*;
import depth.main.wishwesee.domain.vote.service.AttendanceVoteService;
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

@Tag(name = "Attendance Vote", description = "Attendance Vote API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/invitation/{invitationId}/attendance")
public class AttendanceVoteController {

    private final AttendanceVoteService attendanceVoteService;

    @Operation(summary = "참석 조사 조회", description = "참석 조사 현황을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AttendanceVoteStatusRes.class))}),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping()
    public ResponseEntity<depth.main.wishwesee.global.payload.ApiResponse> getAttendanceVoteStatus(
            @Parameter(description = "Accesstoken을 입력해주세요.") @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "초대장의 id를 입력해주세요.", required = true) @PathVariable Long invitationId
    ) {
        return attendanceVoteService.getAttendanceVoteStatus(userPrincipal, invitationId);
    }

    @Operation(summary = "(비회원) 특정 닉네임의 참석 조사 결과 조회", description = "비회원일 경우, 이름 입력 후 (중복된 이름이 존재한다면) 해당 이름의 참석 조사 투표 결과를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MyVoteRes.class))}),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/guest")
    public ResponseEntity<depth.main.wishwesee.global.payload.ApiResponse> getAttendanceVoteByNickname(
            @Parameter(description = "초대장의 id를 입력해주세요.", required = true) @PathVariable Long invitationId,
            @Parameter(description = "닉네임을 입력해주세요.") @RequestParam String nickname
    ) {
        return attendanceVoteService.getAttendanceVoteByNickname(invitationId, nickname);
    }

    @Operation(summary = "투표자 목록 조회", description = "투표자 목록을 조회합니다. 작성자만 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VoterRes.class))}),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/voters")
    public ResponseEntity<depth.main.wishwesee.global.payload.ApiResponse> getAttendanceVoterList(
            @Parameter(description = "Accesstoken을 입력해주세요.") @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "초대장의 id를 입력해주세요.", required = true) @PathVariable Long invitationId,
            @Parameter(description = "확인하고자 하는 참석 여부를 입력해주세요. true: 참석(기본), false: 불참", required = true) @RequestParam(defaultValue = "true") boolean isAttend
    ) {
        return attendanceVoteService.getVoterList(userPrincipal, invitationId, isAttend);
    }

    @Operation(summary = "참석 여부 투표", description = "참석 여부 투표 및 투표를 수정합니다. 비회원의 경우 이미 존재하는 닉네임 입력 시 해당 닉네임의 투표 수정이 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "투표 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))}),
            @ApiResponse(responseCode = "400", description = "투표 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping()
    public ResponseEntity<?> voteAttendance(
            @Parameter(description = "Accesstoken을 입력해주세요.") @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "초대장의 id를 입력해주세요.", required = true) @PathVariable Long invitationId,
            @Parameter(description = "Schemas의 AttendanceVoteReq를 확인해주세요. 참석 여부와 닉네임(비회원)입니다.") @RequestBody AttendanceVoteReq attendanceVoteReq
    ) {
        return attendanceVoteService.voteAttendance(userPrincipal, invitationId, attendanceVoteReq);
    }

    @Operation(summary = "참석 조사 마감 여부 수정", description = "참석 조사의 마감 여부를 수정합니다. 작성자만 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AttendanceSurveyClosedRes.class))}),
            @ApiResponse(responseCode = "400", description = "수정 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PutMapping()
    public ResponseEntity<?> updateAttendanceSurveyClosed(
            @Parameter(description = "Accesstoken을 입력해주세요.") @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "초대장의 id를 입력해주세요.", required = true) @PathVariable Long invitationId
    ) {
        return attendanceVoteService.updateAttendanceSurvey(userPrincipal, invitationId);
    }

    @Operation(summary = "닉네임 중복여부 확인", description = "투표 전, 닉네임 중복여부를 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "중복 여부 확인 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CheckNicknameRes.class))}),
            @ApiResponse(responseCode = "400", description = "중복 여부 확인 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/check")
    public ResponseEntity<depth.main.wishwesee.global.payload.ApiResponse> checkNickname(
            @Parameter(description = "초대장의 id를 입력해주세요.", required = true) @PathVariable Long invitationId,
            @Parameter(description = "중복을 확인할 닉네임을 입력해주세요.") @RequestParam String nickname
    ) {
        return attendanceVoteService.checkDuplicateNickname(invitationId, nickname);
    }

}
