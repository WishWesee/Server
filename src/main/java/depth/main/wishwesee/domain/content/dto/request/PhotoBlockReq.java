package depth.main.wishwesee.domain.content.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
@Schema(title = "PhotoBlockReq: 사진 블록 요청 객체", description = "POST: /api/v1/invitation에서 사용합니다.")
@Getter
public class PhotoBlockReq extends BlockReq{

}
