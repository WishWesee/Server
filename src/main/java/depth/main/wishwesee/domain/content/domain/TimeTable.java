package depth.main.wishwesee.domain.content.domain;

import depth.main.wishwesee.domain.common.TimeTableEntryListConverter;

import depth.main.wishwesee.domain.invitation.domain.Invitation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TimeTable extends Block {
    @Convert(converter = TimeTableEntryListConverter.class) // JSON 변환기 설정
    private List<TimeTableEntry> content; // 타임테이블 내용

    @Builder
    public TimeTable(int sequence, Invitation invitation, List<TimeTableEntry> content) {
        super(sequence, invitation);
        this.content = content;
    }

    @Getter
    @Schema(description = "시간표 내용")
    public static class TimeTableEntry {
        @Schema(description = "시간", example = "10:00", type = "String")
        private String time;

        @Schema(description = "내용", example = "회의", type = "String")
        private String content;

        @Builder
        public TimeTableEntry(String time, String content) {
            this.time = time;
            this.content = content;
        }
    }

}
