package depth.main.wishwesee.domain.content.dto.request;

import depth.main.wishwesee.domain.content.domain.TimeTable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;
@Schema(description = "시간표 블록 요청")
@Getter
public class TimeTableBlockReq extends BlockReq{
    @Schema(description = "시간표 내용 목록", type = "List<TimeTable.TimeTableEntry>")
    private List<TimeTable.TimeTableEntry> content; // 시간표 항목들
}
