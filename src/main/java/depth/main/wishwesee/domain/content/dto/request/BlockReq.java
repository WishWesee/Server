package depth.main.wishwesee.domain.content.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Schema(description = "블록 요청 객체")
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
    @Schema(description = "초대장에 위치할 블록 순서", type = "integer")
    @NotNull(message = "sequence는 null일 수 없습니다.")
    private int sequence;
}
