package depth.main.wishwesee.domain.content.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PhotoBlockRes extends BlockRes{
    private String image;

    @Builder
    public PhotoBlockRes(int sequence, String image) {
        super(sequence);
        this.image = image;
    }
}
