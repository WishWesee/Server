package depth.main.wishwesee.domain.invitation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import depth.main.wishwesee.domain.content.dto.response.BlockRes;
import depth.main.wishwesee.domain.vote.dto.response.ScheduleVoteRes;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@Getter
public class CompletedInvitationRes {
    private Long invitationId;

    private String invitationToken;

    private String title;

    private String cardImage;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime endTime;

    private String location;

    private String address;

    private String mapLink;

    private int mapViewType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate voteDeadline;

    private boolean attendanceSurveyEnabled;

    private boolean scheduleVoteMultiple;

    private boolean scheduleVoteClosed;

    private boolean attendanceSurveyClosed;

    private int attendingCount;

    private int notAttendingCount;

    private List<BlockRes> blocks; // 블록 리스트

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
