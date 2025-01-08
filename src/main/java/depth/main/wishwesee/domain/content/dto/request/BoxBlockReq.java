package depth.main.wishwesee.domain.content.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
@Schema(description = "박스 블록 요청")
@Getter
public class BoxBlockReq extends BlockReq{
    @Schema(description = "박스 제목", type = "string")
    private String title;  // 박스 제목

    @Schema(description = "박스 색상", type = "string")
    private String color;  // 박스 색상

    @Schema(description = "박스 내용", type = "string")
    private String content; // 박스 내용

}
