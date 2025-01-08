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
    private String color;

    @Builder
    public Box(int sequence, Invitation invitation, String title, String content, String color){
        super(sequence, invitation);
        this.title = title;
        this.color = color;
        this.content = content;
    }

}

