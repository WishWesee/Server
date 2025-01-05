package depth.main.wishwesee.domain.content.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "텍스트 블록 요청")
public class TextBlockReq extends BlockReq{
    @Schema(description = "텍스트 내용", type = "string")
    private String content;

}