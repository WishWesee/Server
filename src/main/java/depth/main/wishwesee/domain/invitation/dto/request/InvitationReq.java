package depth.main.wishwesee.domain.invitation.dto.request;

import depth.main.wishwesee.domain.content.dto.request.BlockReq;
import depth.main.wishwesee.domain.vote.dto.request.ScheduleVoteReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@Getter
@Schema(description = "초대장 요청 DTO")
public class InvitationReq {
    @Schema(description = "초대장 ID(임시저장 확인용)", type = "integer")
    private Long invitationId; // 초대장 ID (임시저장확인용)

    @Schema(description = "초대장 제목", type = "String")
    private String title; // 제목

    @Schema(description = "임시저장 여부", type = "boolean")
    private boolean tempSaved; // 임시저장여부

    @Schema(description = "시작 날짜", type = "string", format = "date", example = "2024-12-30")
    private LocalDate startDate; // 시작날짜

    @Schema(description = "시작 시간", type = "string", format = "time", example = "14:00")
    private LocalTime startTime; // 시작시간

    @Schema(description = "마지막 날짜", type = "string", format = "date", example = "2024-12-31")
    private LocalDate endDate; // 마지막날짜

    @Schema(description = "마지막 시간", type = "string", format = "time", example = "2024-12-31")
    private LocalTime endTime; // 마지막시간

    @Schema(description = "장소명", type = "string")
    private String location; // 장소명

    @Schema(description = "주소", type = "string")
    private String address; // 주소

    @Schema(description = "지도 링크", type = "string")
    private String mapLink; // 지도 링크

    @Schema(description = "지도 보기 타입 (0: 주소만 보기, 1: 지도 보기)", type = "integer")
    private int mapViewType; // 0: 주소만보기, 1: 맵보기

    @Schema(description = "투표 마감일", type = "string", format = "date")
    private LocalDate voteDeadline; // 투표마감일

    @Schema(description = "참석 여부 조사 활성화 여부", type = "boolean")
    private boolean attendanceSurveyEnabled; // 참석여부조사

    @Schema(description = "일정 투표 복수 선택 가능 여부", type = "boolean")
    private boolean scheduleVoteMultiple; // 일정투표복수선택여부

    @Schema(description = "일정 투표 마감 여부", type = "boolean")
    private boolean scheduleVoteClosed; // 일정투표마감여부

    @Schema(description = "참석 여부 투표 마감 여부", type = "boolean")
    private boolean attendanceSurveyClosed; // 참석투표마감여부

    @Schema(description = "블록 리스트", type = "array")
    private List<BlockReq> blocks; // 블럭리스트

    @Schema(description = "일정 투표 날짜 리스트", type = "array")
    private List<ScheduleVoteReq> scheduleVotes; // 일정투표날짜리스트
}
