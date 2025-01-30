package depth.main.wishwesee.domain.content.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(title = "DividerBlockRes: 구분선 블록 조회 응답 객체", description = "GET: /api/v1/invitation에서 사용합니다.")
public class DividerBlockRes extends BlockRes{
    @Builder
    public DividerBlockRes(int sequence) {
        super(sequence);
    }
}
