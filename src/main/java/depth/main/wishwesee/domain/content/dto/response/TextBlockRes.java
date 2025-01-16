package depth.main.wishwesee.domain.content.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TextBlockRes extends BlockRes{
    @Schema(description = "텍스트 내용")
    private String content;

    @Builder
    public TextBlockRes(int sequence, String content) {
        super(sequence);
        this.content = content;
    }
}
