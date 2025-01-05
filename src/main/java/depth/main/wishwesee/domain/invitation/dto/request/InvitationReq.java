package depth.main.wishwesee.domain.invitation.dto.request;

import depth.main.wishwesee.domain.content.dto.request.BlockReq;
import depth.main.wishwesee.domain.vote.dto.request.ScheduleVoteReq;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@Getter
public class InvitationReq {
    private Long id; // 초대장 ID (임시저장확인용)
    private String title; // 제목
    private boolean tempSaved; // 임시저장여부
    private LocalDate startDate; // 시작날짜
    private LocalTime startTime; // 시작시간
    private LocalDate endDate; // 마지막날짜
    private LocalTime endTime; // 마지막시간
    private String location; // 장소명
    private String address; // 주소
    private String mapLink; // 지도 링크
    private int mapViewType; // 0: 주소만보기, 1: 맵보기
    private LocalDate voteDeadline; // 투표마감일
    private boolean attendanceSurveyEnabled; // 참석여부조사
    private boolean scheduleVoteMultiple; // 일정투표복수선택여부
    private boolean scheduleVoteClosed; // 일정투표마감여부
    private boolean attendanceSurveyClosed; // 참석투표마감여부
    private List<BlockReq> blocks; // 블럭리스트
    private List<ScheduleVoteReq> scheduleVotes; // 일정투표날짜리스트

}
