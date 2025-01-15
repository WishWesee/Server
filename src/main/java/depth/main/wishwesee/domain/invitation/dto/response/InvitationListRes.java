package depth.main.wishwesee.domain.invitation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class InvitationListRes {
    @Schema(description = "전체 보낸 혹은 받은 초대장 개수")
    private int totalInvitations;

    @Schema(description = "해당 연도의 보낸 혹은 받은 초대장 목록")
    private List<MyInvitationOverViewRes.InvitationRes> invitations;

    @Builder
    InvitationListRes(int totalInvitations, List<MyInvitationOverViewRes.InvitationRes> invitations){
        this.totalInvitations = totalInvitations;
        this.invitations = invitations;
    }

}
