package depth.main.wishwesee.domain.vote.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "AttendanceVoteStatusRes: 참석 조사 조회 응답 객체", description = "GET: /api/v1/invitation/{invitationId}/attendance에서 사용합니다.")
public class AttendanceVoteStatusRes {

    @Schema(type = "int", example = "4", description = "참석에 투표한 투표자들의 수입니다.")
    private int attendingCount;

    @Schema(type = "int", example = "2", description = "불참에 투표한 투표자들의 수입니다.")
    private int notAttendingCount;

    @Schema(type = "Boolean", example = "true", description = "회원의 경우, 내 투표 상태입니다. true: 참석, false: 불참, null: 비회원이거나 투표하지 않은 경우")
    @JsonProperty("isAttending")
    private Boolean isAttending;

    @Schema(type = "boolean", example = "false", description = "작성자 본인 여부입니다.")
    @JsonProperty("isSender")
    private boolean sender;
}
