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
public class CheckNicknameRes {

    @Schema(type = "boolean", example = "true", description = "닉네임의 중복 여부입니다. true: 이미 존재함, false: 존재하지 않음")
    private boolean isDuplicated;
}
