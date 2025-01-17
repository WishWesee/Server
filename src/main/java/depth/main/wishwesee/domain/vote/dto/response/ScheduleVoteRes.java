package depth.main.wishwesee.domain.vote.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
@Getter
@Schema(description = "일정 투표 응답 객체")
public class ScheduleVoteRes {
    @Schema(description = "일정 시작 날짜", example = "2025-01-01", type = "String")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(description = "일정 시작 시간", example = "10:00", type = "String")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startTime;

    @Schema(description = "일정 종료 날짜", example = "2025-01-01", type = "String")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Schema(description = "일정 종료 시간", example = "12:00", type = "String")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime endTime;

    @Builder
    public ScheduleVoteRes(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime){
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
    }
}
