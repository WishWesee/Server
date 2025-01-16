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
public class AttendanceSurveyClosedRes {

    @Schema(type = "boolean", example = "true", description = "참석 조사 마감 여부입니다. true: 조사 마감, false: 조사 진행 중")
    private boolean attendanceSurveyClosed;
}
