package depth.main.wishwesee.domain.vote.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class VoteScheduleReq {

    @Schema(type = "String", example = "김위시", description = "투표자의 이름입니다. 회원인 경우 이름을 입력하지 않습니다.")
    private String nickname;

    @Schema(type = "array", example = "[1, 2, 4]", description = "특정 사용자가 투표한 일정 투표 id 리스트입니다. ")
    private List<Long> scheduleVoteIds;
}
