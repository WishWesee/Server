package depth.main.wishwesee.domain.vote.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
@Getter
@NoArgsConstructor
@Schema(description = "일정 투표 응답 객체")
public class ScheduleVoteRes {

    @Schema(description = "각 투표 항목별 id", example = "1", type = "Long")
    private Long scheduleVoteId;

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

    @Schema(description = "투표자 수", example = "3", type = "int")
    private int voterCount;

    @Schema(description = "나의 투표 여부를 표시합니다. true: 투표/false: 투표하지 않거나 비회원인 경우", example = "true", type = "boolean")
    @JsonProperty("isVoted")
    private boolean voted;

    @Builder
    public ScheduleVoteRes(Long scheduleVoteId, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime, int voterCount, boolean voted){
        this.scheduleVoteId = scheduleVoteId;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.voterCount = voterCount;
        this.voted = voted;
    }
}
