package depth.main.wishwesee.domain.invitation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "FeedbackRes: 후기 목록 조회 응답 객체", description = "GET: /api/v1/invitation/{invitationId}/feedback에서 사용합니다.")
public class FeedbackListRes {

    @Schema(type = "int", example = "3", description = "후기의 개수입니다.")
    private int count;

    @Schema(type = "List", example = "Schemas의 FeedbackRes를 확인해주세요.", description = "후기의 리스트입니다.")
    private List<FeedbackRes> feedbackResList;
}
