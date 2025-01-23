package depth.main.wishwesee.domain.content.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(title = "PhotoBlockRes: 사진 블록 조회 응답 객체", description = "GET: /api/v1/invitation에서 사용합니다.")
public class PhotoBlockRes extends BlockRes{
    @Schema(description = "사진 이미지", example = "https://wishwesee-s3-image-bucket.s3.amazonaws.com/3f78b60d-c3b5-46db-aab2-9f8245ad7b35.jpg", type = "String")
    private String image;

    @Builder
    public PhotoBlockRes(int sequence, String image) {
        super(sequence);
        this.image = image;
    }
}
