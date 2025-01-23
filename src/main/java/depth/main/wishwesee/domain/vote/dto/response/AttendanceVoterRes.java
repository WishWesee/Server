package depth.main.wishwesee.domain.vote.dto.response;

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
@Schema(title = "AttendanceVoterRes: 참석 조사 투표자 목록 조회 응답 객체", description = "GET: /api/v1/invitation/{invitationId}/attendance/voter에서 사용합니다.")
public class AttendanceVoterRes {

    @Schema(type = "int", example = "5", description = "투표자의 수입니다.")
    public int voterCount;

    @Schema(type = "array", example = "[김위시, 홍길동]", description = "투표자의 이름 목록입니다.")
    private List<String> voterNames;
}
