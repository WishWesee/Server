package depth.main.wishwesee.domain.content.dto.response;

import depth.main.wishwesee.domain.content.domain.TimeTable;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
@Getter
public class TimeTableBlockRes extends BlockRes{
    @Schema(description = "시간표 내용 목록")
    private List<TimeTable.TimeTableEntry> content;

    @Builder
    public TimeTableBlockRes(int sequence, List<TimeTable.TimeTableEntry> content) {
        super(sequence);
        this.content = content;
    }
}
