package depth.main.wishwesee.domain.vote.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.Getter;

@Getter
public class VoteAttendanceReq {

    @Schema(type = "String", example = "김위시", description = "투표자의 이름입니다. 회원인 경우 이름을 입력하지 않습니다.")
    private String nickname;

    @Schema(type = "boolean", example = "true", description = "투표자의 참석 여부입니다. true: 참석, false: 불참")
    @Column(nullable = false)
    private boolean isAttending;
}
