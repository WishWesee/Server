package depth.main.wishwesee.domain.content.dto.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;

@Getter
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
    private int sequence;

    public BlockRes(int sequence) {
        this.sequence = sequence;
    }
}
