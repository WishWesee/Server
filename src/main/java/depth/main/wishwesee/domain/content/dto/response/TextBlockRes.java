package depth.main.wishwesee.domain.content.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(title = "TextBlockRes: 텍스트 블록 조회 응답 객체", description = "GET: /api/v1/invitation에서 사용합니다.")
public class TextBlockRes extends BlockRes{
    @Schema(description = "텍스트 내용", example = "엽떡,치킨,피자 먹자", type = "String")
    private String content;

    @Builder
    public TextBlockRes(int sequence, String content) {
        super(sequence);
        this.content = content;
    }
}
