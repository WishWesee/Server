package depth.main.wishwesee.domain.vote.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceVoteStatusRes {

    @Schema(type = "int", example = "4", description = "참석에 투표한 투표자들의 수입니다.")
    private int attending;

    @Schema(type = "int", example = "2", description = "불참에 투표한 투표자들의 수입니다.")
    private int notAttending;

    @Schema(type = "Boolean", example = "true", description = "회원의 경우, 내 투표 상태입니다. true, false, null(비회원이거나 투표하지 않은 경우)")
    private Boolean myAttending;

    @Schema(type = "boolean", example = "false", description = "작성자 본인 여부입니다.")
    private boolean isSender;
}
