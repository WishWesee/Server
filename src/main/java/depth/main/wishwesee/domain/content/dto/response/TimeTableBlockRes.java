package depth.main.wishwesee.domain.content.dto.response;

import depth.main.wishwesee.domain.content.domain.TimeTable;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
@Getter
@Schema(title = "TimeTableBlockRes: 시간표 블록 조회 응답 객체", description = "GET: /api/v1/invitation에서 사용합니다.")
public class TimeTableBlockRes extends BlockRes{
    @Schema(description = "시간표 내용 목록", type = "List<TimeTable.TimeTableEntry>")
    private List<TimeTable.TimeTableEntry> content;

    @Builder
    public TimeTableBlockRes(int sequence, List<TimeTable.TimeTableEntry> content) {
        super(sequence);
        this.content = content;
    }
}
