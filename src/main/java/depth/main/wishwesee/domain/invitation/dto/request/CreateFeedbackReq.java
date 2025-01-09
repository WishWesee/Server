package depth.main.wishwesee.domain.invitation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import lombok.Getter;

@Getter
public class CreateFeedbackReq {

    @Max(value = 50, message = "후기는 50자까지 가능합니다.")
    @Schema(type = "String", example = "너무 재밌는 파티였당", description = "후기의 내용입니다. 작성하지 않는 경우 null로 전달합니다.")
    private String content;
}
