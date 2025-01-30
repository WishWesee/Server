package depth.main.wishwesee.domain.content.domain;

import depth.main.wishwesee.domain.invitation.domain.Invitation;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Text extends Block {
    private String content;
    private String font;
    private String color;
    private String styles;
    @Builder
    public Text(int sequence, Invitation invitation, String content, String font,
                String color, String styles){
        super(sequence, invitation);
        this.content = content;
        this.font = font;
        this.color = color;
        this.styles = styles;
    }

}

