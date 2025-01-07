package depth.main.wishwesee.domain.content.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TextBlockRes extends BlockRes{
    private String content;

    @Builder
    public TextBlockRes(int sequence, String content) {
        super(sequence);
        this.content = content;
    }
}
