package depth.main.wishwesee.domain.invitation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import depth.main.wishwesee.domain.content.dto.response.BlockRes;
import depth.main.wishwesee.domain.vote.dto.response.ScheduleVoteRes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@Getter
public class CompletedInvitationRes {
    @Schema(description = "초대장ID")
    private Long invitationId;

    @Schema(description = "초대장UUID")
    private String invitationToken;

    @Schema(description = "초대장 제목")
    private String title;

    @Schema(description = "카드 이미지")
    private String cardImage;

    @Schema(description = "시작 날짜")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(description = "시작 시간")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startTime;

    @Schema(description = "마지막 날짜")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Schema(description = "마지막 시간")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime endTime;

    @Schema(description = "장소명")
    private String location;

    @Schema(description = "주소")
    private String address;

    @Schema(description = "지도 링크")
    private String mapLink;

    @Schema(description = "지도 보기 타입 (0: 주소만 보기, 1: 지도 보기)")
    private int mapViewType;

    @Schema(description = "투표 마감일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate voteDeadline;

    @Schema(description = "참석 여부 조사 활성화 여부")
    private boolean attendanceSurveyEnabled;

    @Schema(description = "일정 투표 복수 선택 가능 여부")
    private boolean scheduleVoteMultiple;

    @Schema(description = "일정 투표 마감 여부")
    private boolean scheduleVoteClosed;

    @Schema(description = "참석 여부 투표 마감 여부")
    private boolean attendanceSurveyClosed;

    @Schema(description = "참석 가능한 사람 수")
    private int attendingCount;

    @Schema(description = "참석 불가능한 사람 수")
    private int notAttendingCount;

    @Schema(description = "블록리스트")
    private List<BlockRes> blocks; // 블록 리스트

    @Schema(description = "일정 투표 리스트")
    private List<ScheduleVoteRes> scheduleVotes; // 일정 투표 리스트

    @Builder
    public CompletedInvitationRes(Long invitationId, String invitationToken,String title, String cardImage, LocalDate startDate,
                                  LocalTime startTime, LocalDate endDate, LocalTime endTime, String location, String address,
                                  String mapLink, int mapViewType, LocalDate voteDeadline, boolean attendanceSurveyEnabled,
                                  boolean scheduleVoteMultiple, boolean scheduleVoteClosed, boolean attendanceSurveyClosed,
                                  int attendingCount, int notAttendingCount,
                                  List<BlockRes> blocks, List<ScheduleVoteRes> scheduleVotes) {
        this.invitationToken = invitationToken;
        this.invitationId = invitationId;
        this.title = title;
        this.cardImage = cardImage;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.location = location;
        this.address = address;
        this.mapLink = mapLink;
        this.mapViewType = mapViewType;
        this.voteDeadline = voteDeadline;
        this.attendanceSurveyEnabled = attendanceSurveyEnabled;
        this.scheduleVoteMultiple = scheduleVoteMultiple;
        this.scheduleVoteClosed = scheduleVoteClosed;
        this.attendanceSurveyClosed = attendanceSurveyClosed;
        this.attendingCount = attendingCount;
        this.notAttendingCount = notAttendingCount;
        this.blocks = blocks;
        this.scheduleVotes = scheduleVotes;
    }

}
