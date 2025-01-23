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
@Schema(title = "FeedbackRes: 후기 조회 응답 객체", description = "GET: /api/v1/invitation/{invitationId}/feedback에서 사용합니다.")
public class FeedbackRes {

    @Schema(type = "Long", example = "1", description = "후기의 id입니다.")
    private Long feedbackId;

    @Schema(type = "String", example = "넘넘 재밌었당", description = "후기의 내용입니다.")
    private String content;

    @Schema(type = "String", example = "https://s3-bucket-name.amazon.com/weohifhbj.png", description = "후기의 사진입니다.")
    private String image;

    @Schema(type = "boolean", example = "true", description = "후기의 삭제 권한 여부입니다. 초대장 수신자는 본인이 작성한 후기만 삭제 가능하며, 발신자는 모두 삭제 가능합니다.")
    private boolean isDeletable;
}
