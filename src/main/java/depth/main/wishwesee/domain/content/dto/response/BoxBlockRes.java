package depth.main.wishwesee.domain.content.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BoxBlockRes extends BlockRes{
    @Schema(description = "박스 제목")
    private String title;

    @Schema(description = "박스 색상")
    private String color;

    @Schema(description = "박스 내용")
    private String content;
    @Builder
    public BoxBlockRes(int sequence, String title, String content, String color) {
        super(sequence);
        this.title = title;
        this.content = content;
        this.color = color;
    }

}

