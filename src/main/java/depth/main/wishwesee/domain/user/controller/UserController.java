package depth.main.wishwesee.domain.user.controller;

import depth.main.wishwesee.domain.user.dto.response.UserProfileRes;
import depth.main.wishwesee.domain.user.service.UserService;
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

@Tag(name = "User", description = "User API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    @Operation(summary = "구글 계정명/프로필 사진 조회", description = "로그인한 사용자의 구글 계정명, 프로필 사진 url을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfileRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping(value = "")
    public ResponseEntity<depth.main.wishwesee.global.payload.ApiResponse> saveFeedback(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ) {
        return ResponseEntity.ok(userService.getProfile(userPrincipal));
    }

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴를 진행합니다. 이 경우, 닉네임, 초대장, 후기, 투표 전적을 제외한 회원의 개인정보만 삭제됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class) ) } ),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @DeleteMapping(value = "")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ) {
        userService.encryptUserInfoOnDelete(userPrincipal);
        return ResponseEntity.noContent().build();
    }

}
