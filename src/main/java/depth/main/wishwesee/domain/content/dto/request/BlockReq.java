package depth.main.wishwesee.domain.content.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TextBlockReq.class, name = "text"),
        @JsonSubTypes.Type(value = TimeTableBlockReq.class, name = "timeTable"),
        @JsonSubTypes.Type(value = BoxBlockReq.class, name = "box"),
        @JsonSubTypes.Type(value = PhotoBlockReq.class, name = "photo")
})
public abstract class BlockReq {
    @NotNull(message = "sequence는 null일 수 없습니다.")
    private int sequence;
}
