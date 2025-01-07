package depth.main.wishwesee.domain.content.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BoxBlockRes extends BlockRes{
    private String title;
    private String color;
    private String content;
    @Builder
    public BoxBlockRes(int sequence, String title, String content, String color) {
        super(sequence);
        this.title = title;
        this.content = content;
        this.color = color;
    }

}

