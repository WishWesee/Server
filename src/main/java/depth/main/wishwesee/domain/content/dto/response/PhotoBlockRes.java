package depth.main.wishwesee.domain.content.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PhotoBlockRes extends BlockRes{
    @Schema(description = "사진 이미지")
    private String image;

    @Builder
    public PhotoBlockRes(int sequence, String image) {
        super(sequence);
        this.image = image;
    }
}
