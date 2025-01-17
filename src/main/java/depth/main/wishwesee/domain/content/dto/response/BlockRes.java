package depth.main.wishwesee.domain.content.dto.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "블록 응답 객체")
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TextBlockRes.class, name = "text"),
        @JsonSubTypes.Type(value = PhotoBlockRes.class, name = "photo"),
        @JsonSubTypes.Type(value = BoxBlockRes.class, name = "box"),
        @JsonSubTypes.Type(value = TimeTableBlockRes.class, name = "timeTable")
})
public abstract class BlockRes {
    @Schema(description = "초대장에 위치할 블록 순서", example = "1", type = "int")
    private int sequence;

    public BlockRes(int sequence) {
        this.sequence = sequence;
    }
}
