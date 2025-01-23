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
@Schema(title = "CheckNicknameRes: 닉네임 중복 여부 응답 객체",
        description = "GET: /api/v1/invitation/{invitationId}/schedule/check," +
        "/api/v1/invitation/{invitationId}/attendance/check에서 사용합니다.")
public class CheckNicknameRes {

    @Schema(type = "boolean", example = "true", description = "닉네임의 중복 여부입니다. true: 이미 존재함, false: 존재하지 않음")
    @JsonProperty("isDuplicated")
    private boolean duplicated;
}
