package depth.main.wishwesee.domain.content.dto.response;

import depth.main.wishwesee.domain.content.domain.TimeTable;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
@Getter
public class TimeTableBlockRes extends BlockRes{
    private List<TimeTable.TimeTableEntry> content;

    @Builder
    public TimeTableBlockRes(int sequence, List<TimeTable.TimeTableEntry> content) {
        super(sequence);
        this.content = content;
    }
}
