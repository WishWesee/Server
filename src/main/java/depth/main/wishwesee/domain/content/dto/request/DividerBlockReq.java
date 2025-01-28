package depth.main.wishwesee.domain.content.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(title = "DividerReq: 구분선 요청 객체", description = "POST: /api/v1/invitation에서 사용합니다.")
public class DividerBlockReq extends BlockReq{
}
