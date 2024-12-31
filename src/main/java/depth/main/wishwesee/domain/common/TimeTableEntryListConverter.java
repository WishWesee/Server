package depth.main.wishwesee.domain.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import depth.main.wishwesee.domain.content.domain.TimeTable;
import jakarta.persistence.AttributeConverter;

import java.util.List;

public class TimeTableEntryListConverter implements AttributeConverter<List<TimeTable.TimeTableEntry>, String> {

    private final ObjectMapper objectMapper;

    public TimeTableEntryListConverter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules(); // 필요한 모듈 자동 등록
    }

    @Override
    public String convertToDatabaseColumn(List<TimeTable.TimeTableEntry> attribute) {
        try {
            // 객체(List<TimeTableEntry>)를 JSON 문자열로 변환
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new RuntimeException("JSON 직렬화 중 오류 발생", e);
        }
    }

    @Override
    public List<TimeTable.TimeTableEntry> convertToEntityAttribute(String dbData) {
        try {
            // JSON 문자열을 객체(List<TimeTableEntry>)로 변환
            return objectMapper.readValue(dbData, new TypeReference<List<TimeTable.TimeTableEntry>>() {});
        } catch (Exception e) {
            throw new RuntimeException("JSON 역직렬화 중 오류 발생", e);
        }
    }
}

