package depth.main.wishwesee.domain.invitation.dto.response;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class MyInvitationOverViewRes {
    @Schema(description = "작성 중인 초대장 목록")
    private List<InvitationRes> draftingInvitations;

    @Schema(description = "보낸 초대장 3개")
    private List<InvitationRes> sentInvitations;

    @Schema(description = "받은 초대장 3개")
    private List<InvitationRes> receivedInvitations;

    @Builder
    MyInvitationOverViewRes(List<InvitationRes> draftingInvitations, List<InvitationRes> sentInvitations,
                            List<InvitationRes> receivedInvitations){
        this.draftingInvitations = draftingInvitations;
        this.sentInvitations = sentInvitations;
        this.receivedInvitations = receivedInvitations;
    }

    @Getter
    public static class InvitationRes{
        @Schema(description = "초대장ID")
        private Long invitationId;

        @Schema(description = "초대장 카드 이미지 URL")
        private String cardImage;

        @Schema(description = "초대장 제목")
        private String title;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        @Schema(description = "초대장 임시저장/생성/받은/ 날짜")
        private LocalDateTime date;

        @Builder
        InvitationRes(Long invitationId, String cardImage, String title, LocalDateTime date){
            this.invitationId = invitationId;
            this.cardImage = cardImage;
            this.title = title;
            this.date = date;
        }

    }



}





