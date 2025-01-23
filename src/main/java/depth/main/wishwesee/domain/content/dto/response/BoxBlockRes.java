package depth.main.wishwesee.domain.content.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(title = "BoxBlockRes: 박스 블록 조회 응답 객체", description = "GET: /api/v1/invitation에서 사용합니다.")
public class BoxBlockRes extends BlockRes{
    @Schema(description = "박스 제목", example = "Dress Code", type = "String")
    private String title;

    @Schema(description = "박스 색상", example = "#FF5733", type = "String")
    private String color;

    @Schema(description = "박스 내용", example = "파랑색으로 입고오세용", type = "String")
    private String content;
    @Builder
    public BoxBlockRes(int sequence, String title, String content, String color) {
        super(sequence);
        this.title = title;
        this.content = content;
        this.color = color;
    }

}

