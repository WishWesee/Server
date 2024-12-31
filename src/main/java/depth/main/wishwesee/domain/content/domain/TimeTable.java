package depth.main.wishwesee.domain.content.domain;

import depth.main.wishwesee.domain.common.TimeTableEntryListConverter;

import depth.main.wishwesee.domain.invitation.domain.Invitation;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeTable extends Block {
    @Convert(converter = TimeTableEntryListConverter.class) // JSON 변환기 설정
    private List<TimeTableEntry> content; // 타임테이블 내용

    @Builder
    public TimeTable(int sequence, Invitation invitation, List<TimeTableEntry> content) {
        super(sequence, invitation);
        this.content = content;
    }

    @Getter
    public static class TimeTableEntry {
        private String time;
        private String content;

        @Builder
        public TimeTableEntry(String time, String content) {
            this.time = time;
            this.content = content;
        }
    }

}
