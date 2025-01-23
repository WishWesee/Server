package depth.main.wishwesee.domain.vote.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "ScheduleVoterRes: 특정 일정의 투표자 목록 응답 객체", description = "GET: /api/v1/invitation/{invitationId}/schedule/{scheduleVoteId}/voter에서 사용합니다.")
public class ScheduleVoterRes {

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

    @Schema(type = "array", example = "[김위시, 홍길동]", description = "투표자의 이름 목록입니다.")
    private List<String> voterNames;
}
