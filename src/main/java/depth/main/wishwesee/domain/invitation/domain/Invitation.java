package depth.main.wishwesee.domain.invitation.domain;

import depth.main.wishwesee.domain.common.BaseEntity;
import depth.main.wishwesee.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
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


}
