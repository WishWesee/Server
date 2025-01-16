package depth.main.wishwesee.domain.vote.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceVoteStatusRes {

    private int attending;

    private int notAttending;
}
