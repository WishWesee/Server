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
@Schema(title = "MyScheduleVoteIdsRes: 특정 닉네임의 투표 현황 응답 객체", description = "GET: /api/v1/invitation/{invitationId}/schedule/guest에서 사용합니다.")
public class MyScheduleVoteIdsRes {

    @Schema(type = "array", example = "[1, 2, 4]", description = "특정 닉네임의 사용자가 투표한 일정 투표 id입니다. 투표 전적이 없는 경우 빈 리스트로 전달합니다.")
    private List<Long> scheduleVoteIds;
}


