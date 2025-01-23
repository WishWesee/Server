package depth.main.wishwesee.domain.content.dto.request;

import depth.main.wishwesee.domain.content.domain.TimeTable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;
@Schema(title = "TimeTableBlockReq: 시간표 블록 요청 객체", description = "POST: /api/v1/invitation에서 사용합니다.")
@Getter
public class TimeTableBlockReq extends BlockReq{
    @Schema(description = "시간표 내용 목록", type = "List<TimeTable.TimeTableEntry>")
    private List<TimeTable.TimeTableEntry> content; // 시간표 항목들
}
