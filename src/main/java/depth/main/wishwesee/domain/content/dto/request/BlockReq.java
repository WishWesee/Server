package depth.main.wishwesee.domain.content.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Schema(
        title = "BlockReq: 블록 요청 객체",
        description = "POST: /api/v1/invitation에서 사용합니다.",
        discriminatorProperty = "type",
        discriminatorMapping = {
                @DiscriminatorMapping(value = "text", schema = TextBlockReq.class),
                @DiscriminatorMapping(value = "timeTable", schema = TimeTableBlockReq.class),
                @DiscriminatorMapping(value = "box", schema = BoxBlockReq.class),
                @DiscriminatorMapping(value = "photo", schema = PhotoBlockReq.class),
                @DiscriminatorMapping(value = "divider", schema = DividerBlockReq.class)
        })
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TextBlockReq.class, name = "text"),
        @JsonSubTypes.Type(value = TimeTableBlockReq.class, name = "timeTable"),
        @JsonSubTypes.Type(value = BoxBlockReq.class, name = "box"),
        @JsonSubTypes.Type(value = PhotoBlockReq.class, name = "photo"),
        @JsonSubTypes.Type(value = DividerBlockReq.class, name = "divider")
})
public abstract class BlockReq {
    @Schema(description = "초대장에 위치할 블록 순서(각각 블럭마다 순서대로 입력해주세요).", example = "1", type = "int")
    @NotNull(message = "sequence는 null일 수 없습니다.")
    private int sequence;
}
