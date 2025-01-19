package depth.main.wishwesee.domain.invitation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
@Getter
public class SaveInvitationReq {
    @Schema(description = "받을 초대장 ID", example = "1", type = "Long")
    private Long invitationId;
}
