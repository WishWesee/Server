package depth.main.wishwesee.domain.content.dto.request;

import depth.main.wishwesee.domain.content.domain.TimeTable;
import lombok.Getter;

import java.util.List;
@Getter
public class TimeTableBlockReq extends BlockReq{
    private List<TimeTable.TimeTableEntry> content; // 시간표 항목들

}
