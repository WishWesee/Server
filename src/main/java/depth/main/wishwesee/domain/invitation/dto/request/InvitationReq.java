package depth.main.wishwesee.domain.invitation.dto.request;

import depth.main.wishwesee.domain.content.dto.request.BlockReq;
import depth.main.wishwesee.domain.vote.dto.request.ScheduleVoteReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@Getter
@Schema(title = "InvitationReq: 초대장 요청 객체", description = "POST:/api/v1/invitation에서 사용합니다.")
public class InvitationReq {
    @Schema(description = "초대장 ID(임시저장 확인용)", example = "1", type = "integer")
    private Long invitationId; // 초대장 ID (임시저장확인용)

    @Schema(description = "초대장 제목", example = "크리스마스", type = "String")
    private String title; // 제목

    @Schema(description = "임시저장 여부", example = "false",type = "boolean")
    private boolean tempSaved; // 임시저장여부

    @Schema(description = "시작 날짜", type = "string", format = "date", example = "2024-12-30")
    private LocalDate startDate; // 시작날짜

    @Schema(description = "시작 시간", type = "string", format = "time", example = "14:00")
    private LocalTime startTime; // 시작시간

    @Schema(description = "마지막 날짜", type = "string", format = "date", example = "2024-12-31")
    private LocalDate endDate; // 마지막날짜

    @Schema(description = "마지막 시간", type = "string", format = "time", example = "2024-12-31")
    private LocalTime endTime; // 마지막시간

    @Schema(description = "사용자가 직접 입력한 장소명", example = "명지대학교 후문", type = "String")
    private  String userLocation;

    @Schema(description = "장소명", example = "명지대학교", type = "String")
    private String location; // 장소명

    @Schema(description = "주소", example = "경기도 용인시 처인구 명지로 116 명지대학교", type = "String")
    private String address; // 주소

    @Schema(description = "지도 링크", example = "https://map.naver.com/v5/search/%EA%B2%BD%EA%B8%B0%EB%8F%84+%EC%9A%A9%EC%9D%B8%EC%8B%9C+%EC%B2%98%EC%9D%B8%EA%B5%AC+%EB%AA%85%EC%A7%80%EB%A1%9C+116+%EB%AA%85%EC%A7%80%EB%8C%80%ED%95%99%EA%B5%90", type = "String")
    private String mapLink; // 지도 링크

    @Schema(description = "지도 보기 타입 (0: 주소만 보기, 1: 지도 보기)", example = "1", type = "int")
    private int mapViewType; // 0: 주소만보기, 1: 맵보기

    @Schema(description = "투표 마감일", example = "2025-01-05", type = "String")
    private LocalDate voteDeadline; // 투표마감일

    @Schema(description = "참석 여부 조사 활성화 여부", example = "true", type = "boolean")
    private boolean attendanceSurveyEnabled; // 참석여부조사

    @Schema(description = "일정 투표 복수 선택 가능 여부", example = "false", type = "boolean")
    private boolean scheduleVoteMultiple; // 일정투표복수선택여부

    @Schema(description = "일정 투표 마감 여부", example = "true", type = "boolean")
    private boolean scheduleVoteClosed; // 일정투표마감여부

    @Schema(description = "참석 여부 투표 마감 여부", example = "false", type = "boolean")
    private boolean attendanceSurveyClosed; // 참석투표마감여부

    @Schema(description = "블록 리스트", type = "List<BlockReq>")
    private List<BlockReq> blocks; // 블럭리스트

    @Schema(description = "일정 투표 날짜 리스트", type = "List<ScheduleVoteReq>")
    private List<ScheduleVoteReq> scheduleVotes; // 일정투표날짜리스트
}
