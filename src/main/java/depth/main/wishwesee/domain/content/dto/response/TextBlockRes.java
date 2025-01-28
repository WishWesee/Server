package depth.main.wishwesee.domain.content.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(title = "TextBlockRes: 텍스트 블록 조회 응답 객체", description = "GET: /api/v1/invitation에서 사용합니다.")
public class TextBlockRes extends BlockRes{
    @Schema(description = "텍스트 내용", example = "엽떡,치킨,피자 먹자", type = "String")
    private String content;

    @Schema(description = "텍스트 폰트", example = "고딕", type = "String")
    private String font;

    @Schema(description = "텍스트 색상", example = "#000000", type = "String")
    private String color;

    @Schema(description = "텍스트 스타일", example = "underline", type = "String")
    private String styles;

    @Builder
    public TextBlockRes(int sequence, String content, String font, String color, String styles) {
        super(sequence);
        this.content = content;
        this.font = font;
        this.color = color;
        this.styles = styles;
    }
}
