package depth.main.wishwesee.domain.invitation.dto.response;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Schema(title = "MyInvitationOverViewRes: 내 초대장 목록 응답 객체",
description = "GET: /api/v1/invitation/my-invitations에서 사용합니다.")
public class MyInvitationOverViewRes {
    @Schema(description = "작성중인 초대장 개수", example = "3", type = "int")
    private int draftCount;

    @Schema(description = "작성 중인 초대장 목록", type = "List<InvitationRes>")
    private List<InvitationRes> draftingInvitations;

    @Schema(description = "보낸 초대장 3개", type = "List<InvitationRes>")
    private List<InvitationRes> sentInvitations;

    @Schema(description = "받은 초대장 3개", type = "List<InvitationRes>")
    private List<InvitationRes> receivedInvitations;

    @Builder
    MyInvitationOverViewRes(int draftCount, List<InvitationRes> draftingInvitations,
                            List<InvitationRes> sentInvitations, List<InvitationRes> receivedInvitations){
        this.draftCount = draftCount;
        this.draftingInvitations = draftingInvitations;
        this.sentInvitations = sentInvitations;
        this.receivedInvitations = receivedInvitations;
    }

    @Getter
    @Schema(title = "MyInvitationOverViewRes: '나의 초대장'목록에서 초대장 조회 응답 객체",
            description = "GET: /api/v1/invitation/my-invitations에서 사용합니다.")
    public static class InvitationRes{
        @Schema(description = "초대장ID", example = "5", type = "Long")
        private Long invitationId;

        @Schema(description = "초대장 카드 이미지 URL", example = "https://wishwesee-s3-image-bucket.s3.amazonaws.com/3f78b60d-c3b5-46db-aab2-9f8245ad7b35.jpg", type = "String")
        private String cardImage;

        @Schema(description = "초대장 제목", example = "크리스마스파티", type = "String")
        private String title;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        @Schema(description = "초대장 임시저장/생성/받은/ 날짜", example = "2025-01-05", type = "String")
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





