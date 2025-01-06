package depth.main.wishwesee.domain.invitation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "초대장 응답 DTO")
public class InvitationRes {
    @Schema(description = "초대장 작성 완료 혹은 임시저장시 응답 메세지")
    private String message;
    @Schema(description = "초대장ID")
    private Long invitationId;

    @Builder
    InvitationRes(String message, Long invitationId){
        this.message = message;
        this.invitationId = invitationId;
    }
}
