package depth.main.wishwesee.domain.invitation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "NotificationFeedbackRes: 후기 알림이 가능한 초대장 목록 조회 응답 객체", description = "GET: /api/v1/invitation/my-invitations/feedback에서 사용합니다.")
public class NotificationFeedbackRes {

    @Schema(description = "초대장ID", example = "1", type = "Long")
    private Long invitationId;

    @Schema(description = "초대장 토큰", example = "328d5c51-79b4-4ae3-860b-17cbe178f345", type = "String")
    private String invitationToken;

    @Schema(description = "초대장 제목", example = "크리스마스", type = "String")
    private String title;
}
