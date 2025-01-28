package depth.main.wishwesee.domain.content.domain;

import depth.main.wishwesee.domain.invitation.domain.Invitation;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Box Entity
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Box extends Block {
    private String title;
    private String content;
    private int colorCode;

    @Builder
    public Box(int sequence, Invitation invitation, String title, String content, int colorCode){
        super(sequence, invitation);
        this.title = title;
        this.colorCode = colorCode;
        this.content = content;
    }

}

