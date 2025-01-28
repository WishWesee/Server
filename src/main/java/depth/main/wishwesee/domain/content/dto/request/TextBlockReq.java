package depth.main.wishwesee.domain.content.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(title = "TextBlockReq: 텍스트 블록 요청 객체", description = "POST: /api/v1/invitation에서 사용합니다.")
public class TextBlockReq extends BlockReq{
    @Schema(description = "텍스트 내용", example = "엽떡,치킨,피자 먹자", type = "String")
    private String content;

    @Schema(description = "텍스트 폰트", example = "고딕", type = "String")
    private String font;

    @Schema(description = "텍스트 색상", example = "#000000", type = "String")
    private String color;

    @Schema(description = "텍스트 스타일", example = "underline", type = "String")
    private String styles;

}