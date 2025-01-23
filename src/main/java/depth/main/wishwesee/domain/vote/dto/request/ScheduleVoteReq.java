package depth.main.wishwesee.domain.vote.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
@Schema(title = "ScheduleVoteReq: 일정 투표 요청 객체", description = "POST:/api/v1/invitation에서 사용합니다.")
@Getter
public class ScheduleVoteReq {
    @Schema(description = "일정 시작 날짜", example = "2024-12-24", type = "string", format = "date")
    private LocalDate startDate; // 일정 시작 날짜

    @Schema(description = "일정 시작 시간", example = "10:00", type = "string", format = "time")
    private LocalTime startTime; // 일정 시작 시간

    @Schema(description = "일정 종료 날짜", example = "2024-12-28", type = "string", format = "date")
    private LocalDate endDate;   // 일정 종료 날짜

    @Schema(description = "일정 종료 시간", example = "14:00", type = "string", format = "time")
    private LocalTime endTime;   // 일정 종료 시간
}
