package depth.main.wishwesee.domain.content.domain;

import depth.main.wishwesee.domain.content.domain.Block;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

// Box Entity
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Box extends Block {
    private String title;
    private String content;
    private String color;

}

