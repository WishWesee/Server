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
@Schema(title = "MyAttendanceVoteRes: 투표자의 참석 여부 응답 객체", description = "GET: /api/v1/invitation/{invitationId}/attendance/guest에서 사용합니다.")
public class MyAttendanceVoteRes {

    @Schema(type = "Boolean", example = "true", description = "투표자의 참석 여부입니다. true: 참석, false: 불참, null: 투표 전적 없음")
    @JsonProperty("isAttending")
    private Boolean attending;
}
