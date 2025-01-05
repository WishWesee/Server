package depth.main.wishwesee.domain.invitation.domain;

import depth.main.wishwesee.domain.common.BaseEntity;
import depth.main.wishwesee.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Invitation extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invitation_id")
    private Long id;

    private String title;

    private String cardImage;

    @Column(name = "is_temp_saved")
    private boolean tempSaved; // 임시저장여부

    private LocalDate startDate;

    private LocalTime startTime;

    private LocalDate endDate;

    private LocalTime endTime;

    private String location;

    private String address;

    private String mapLink;

    @Column(name = "map_view_type")
    private int mapViewType; // 0: 주소만보기, 1: 맵보기

    private LocalDate voteDeadline; // 투표마감일

    @Column(name = "is_attendance_survey_enabled")
    private boolean attendanceSurveyEnabled; // 참석여부조사

    @Column(name = "is_schedule_vote_multiple") // 일정투표복수가능여부
    private boolean scheduleVoteMultiple;

    @Column(name = "is_schedule_vote_closed") // 일정투표마감여부
    private boolean scheduleVoteClosed;

    @Column(name = "is_attendance_survey_closed") // 참석투표마감여부
    private boolean attendanceSurveyClosed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;


    @Builder
    public Invitation(String title, String cardImage, boolean tempSaved, LocalDate startDate,
                      LocalTime startTime, LocalDate endDate, LocalTime endTime, String location,
                      String address, String mapLink, int mapViewType, LocalDate voteDeadline, boolean attendanceSurveyEnabled,
                      boolean scheduleVoteMultiple, boolean scheduleVoteClosed, boolean attendanceSurveyClosed, User sender){
        this.title = title;
        this.cardImage = cardImage;
        this.tempSaved = tempSaved;
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
        this.sender = sender;
    }

    public void updateTempSaved(boolean isTemporary) {
        tempSaved = isTemporary;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateDateTime(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
    }

    public void updateLocationDetails(String location, String address, String mapLink) {
        this.location = location;
        this.address = address;
        this.mapLink = mapLink;
    }

    public void updateCardImage(String cardImageUrl) {
        this.cardImage = cardImageUrl;
    }

    public void updateAttendanceSurvey(boolean attendanceSurveyEnabled) {
        this.attendanceSurveyEnabled = attendanceSurveyEnabled;
    }

    public void updateMapViewType(int mapViewType) {
        this.mapViewType = mapViewType;
    }

    public void updateVoteDeadline(LocalDate voteDeadline) {
        this.voteDeadline = voteDeadline;
    }

    public void updateScheduleVoteMultiple(boolean scheduleVoteMultiple) {
        this.scheduleVoteMultiple = scheduleVoteMultiple;
    }

    public void updateScheduleVoteClosed(boolean scheduleVoteClosed) {
        this.scheduleVoteClosed = scheduleVoteClosed;
    }

    public void updateAttendanceSurveyClosed(boolean attendanceSurveyClosed) {
        this.attendanceSurveyClosed = attendanceSurveyClosed;
    }


}
