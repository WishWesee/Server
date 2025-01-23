package depth.main.wishwesee.domain.invitation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
@Getter
@Schema(title = "SaveInvitationReq: 받을 초대장ID 요청 객체", description = "POST:/api/v1/invitation/save-received에서 사용합니다.")
public class SaveInvitationReq {
    @Schema(description = "받을 초대장 ID", example = "1", type = "Long")
    private Long invitationId;
}
