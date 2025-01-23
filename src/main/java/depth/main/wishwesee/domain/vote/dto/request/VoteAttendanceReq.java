package depth.main.wishwesee.domain.vote.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(title = "VoteAttendanceReq: 참석 조사 요청 객체", description = "POST: /api/v1/invitation/{invitationId}/attendance에서 사용합니다.")
public class VoteAttendanceReq {

    @Schema(type = "String", example = "김위시", description = "투표자의 이름입니다. 회원인 경우 이름을 입력하지 않습니다.")
    private String nickname;

    @Schema(type = "boolean", example = "true", description = "투표자의 참석 여부입니다. true: 참석, false: 불참")
    @Column(nullable = false)
    private boolean isAttending;
}
