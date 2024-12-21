package depth.main.wishwesee.domain.content.domain;

import depth.main.wishwesee.domain.content.domain.Block;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeTable extends Block {
    private LocalTime time;
    private String content;

}
