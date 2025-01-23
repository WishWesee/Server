package depth.main.wishwesee.domain.content.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
@Schema(title = "BoxBlockReq: 박스 블록 요청 객체", description = "POST: /api/v1/invitation에서 사용합니다.")
@Getter
public class BoxBlockReq extends BlockReq{
    @Schema(description = "박스 제목", example = "Dress Code", type = "String")
    private String title;  // 박스 제목

    @Schema(description = "박스 색상", example = "#FF5733", type = "String")
    private String color;  // 박스 색상

    @Schema(description = "박스 내용", example = "파랑색으로 입고오세용", type = "String")
    private String content; // 박스 내용

}
