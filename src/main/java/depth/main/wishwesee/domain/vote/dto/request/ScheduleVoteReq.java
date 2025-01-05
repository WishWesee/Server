package depth.main.wishwesee.domain.vote.dto.request;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class ScheduleVoteReq {
    private LocalDate startDate; // 일정 시작 날짜
    private LocalTime startTime; // 일정 시작 시간
    private LocalDate endDate;   // 일정 종료 날짜
    private LocalTime endTime;   // 일정 종료 시간
}
