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
@Schema(title = "InvitationDetailRes: 완성된 / 임시저장된 초대장 조회 응답 객체",
        description = "GET: /api/v1/invitation/{invitationId} 혹은 /api/v1/invitation/temporary/{invitationId}에서 사용합니다.")
public class InvitationDetailRes {
    @Schema(description = "초대장ID", example = "1", type = "Long")
    private Long invitationId;

    @Schema(description = "작성자 본인 여부", example = "true", type = "boolean")
    private boolean isOwner;

    @Schema(description = "보관함 저장 여부", example = "false", type = "boolean")
    private boolean alreadySaved;

    @Schema(description = "후기 작성이 가능한 상태인지 여부", example = "true", type = "boolean")
    private boolean canWriteFeedback;

    @Schema(description = "참석 여부 조사 활성화 여부", example = "true", type = "boolean")
    private boolean attendanceSurveyEnabled;

    @Schema(description = "카드 이미지 URL", example = "https://wishwesee-s3-image-bucket.s3.amazonaws.com/3f78b60d-c3b5-46db-aab2-9f8245ad7b35.jpg", type = "String")
    private String cardImage;

    @Schema(description = "초대장 제목", example = "크리스마스", type = "String")
    private String title;

    @Schema(description = "시작 날짜", example = "2025-01-01", type = "String")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(description = "시작 시간", example = "18:00", type = "String")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startTime;

    @Schema(description = "마지막 날짜", example = "2025-01-02", type = "String")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Schema(description = "마지막 시간", example = "22:00", type = "String")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime endTime;

    @Schema(description = "투표 마감일", example = "2025-01-05", type = "String")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate voteDeadline;

    @Schema(description = "일정 투표 복수 선택 가능 여부", example = "false", type = "boolean")
    private boolean scheduleVoteMultiple;

    @Schema(description = "사용자의 일정 투표 전적 여부", example = "false", type = "boolean")
    private boolean hasScheduleVote;

    @Schema(description = "일정 투표 리스트", type = "List<ScheduleVoteRes>")
    private List<ScheduleVoteRes> scheduleVotes; // 일정 투표 리스트

    @Schema(description = "일정 투표 마감 여부", example = "true", type = "boolean")
    private boolean scheduleVoteClosed;

    @Schema(description = "지도 보기 타입 (0: 주소만 보기, 1: 지도 보기)", example = "1", type = "int")
    private int mapViewType;

    @Schema(description = "사용자가 직접 입력한 장소명", example = "명지대학교 후문", type = "String")
    private  String userLocation;

    @Schema(description = "장소명", example = "명지대학교", type = "String")
    private String location;

    @Schema(description = "주소", example = "경기도 용인시 처인구 명지로 116 명지대학교", type = "String")
    private String address;

    @Schema(description = "지도 링크", example = "https://map.naver.com/v5/search/%EA%B2%BD%EA%B8%B0%EB%8F%84+%EC%9A%A9%EC%9D%B8%EC%8B%9C+%EC%B2%98%EC%9D%B8%EA%B5%AC+%EB%AA%85%EC%A7%80%EB%A1%9C+116+%EB%AA%85%EC%A7%80%EB%8C%80%ED%95%99%EA%B5%90", type = "String")
    private String mapLink;

    @Schema(description = "위도", example = "37.582218", type = "number")
    private double latitude;

    @Schema(description = "경도", example = "127.001739", type = "number")
    private double longitude;

    @Schema(description = "블록리스트", type = "List<BlockRes>")
    private List<BlockRes> blocks; // 블록 리스트

    @Builder
    public InvitationDetailRes(Long invitationId, boolean isOwner, boolean alreadySaved, boolean canWriteFeedback,String cardImage, String title, LocalDate startDate,
                               LocalTime startTime, LocalDate endDate, LocalTime endTime, LocalDate voteDeadline, boolean hasScheduleVote, boolean scheduleVoteMultiple,
                               List<ScheduleVoteRes> scheduleVotes, boolean scheduleVoteClosed, int mapViewType, String userLocation, String location, String address,
                               String mapLink, double latitude, double longitude, List<BlockRes> blocks, boolean attendanceSurveyEnabled) {

        this.invitationId = invitationId;
        this.title = title;
        this.cardImage = cardImage;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.userLocation = userLocation;
        this.location = location;
        this.address = address;
        this.mapLink = mapLink;
        this.mapViewType = mapViewType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.voteDeadline = voteDeadline;
        this.scheduleVoteMultiple = scheduleVoteMultiple;
        this.hasScheduleVote = hasScheduleVote;
        this.scheduleVoteClosed = scheduleVoteClosed;
        this.blocks = blocks;
        this.scheduleVotes = scheduleVotes;
        this.isOwner = isOwner;
        this.alreadySaved = alreadySaved;
        this.canWriteFeedback = canWriteFeedback;
        this.attendanceSurveyEnabled = attendanceSurveyEnabled;
    }

}

