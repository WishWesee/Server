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
public class Photo extends Block {
    private String image;

    @Builder
    public Photo(int sequence, Invitation invitation,String image){
        super(sequence, invitation);
        this.image = image;
    }

}

