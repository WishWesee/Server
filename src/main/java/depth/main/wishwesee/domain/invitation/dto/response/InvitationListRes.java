package depth.main.wishwesee.domain.invitation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(title = "InvitationListRes: 해당 연도의 보낸 혹은 받은 초대장 목록 조회 응답 객체",
description = "GET: /api/v1/invitation/sent/{year} 혹은 /api/v1/invitation/sent/{year}에서 사용합니다.")
public class InvitationListRes {
    @Schema(description = "전체 보낸 혹은 받은 초대장 개수", example = "10", type = "int")
    private int totalInvitations;

    @Schema(description = "해당 연도의 보낸 혹은 받은 초대장 목록", type = "List<MyInvitationOverViewRes.InvitationRes>")
    private List<MyInvitationOverViewRes.InvitationRes> invitations;

    @Builder
    InvitationListRes(int totalInvitations, List<MyInvitationOverViewRes.InvitationRes> invitations){
        this.totalInvitations = totalInvitations;
        this.invitations = invitations;
    }

}
