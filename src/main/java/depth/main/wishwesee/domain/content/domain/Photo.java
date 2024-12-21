package depth.main.wishwesee.domain.content.domain;

import depth.main.wishwesee.domain.content.domain.Block;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Photo extends Block {
    private String image;


}

