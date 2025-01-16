package depth.main.wishwesee.domain.vote.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceVoterListRes {

    private List<VoterNameRes> attendVoterList;

    private List<VoterNameRes> notAttendVoterList;
}
