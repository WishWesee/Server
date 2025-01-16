package depth.main.wishwesee.domain.vote.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyVoteRes {

    @Schema(type = "Boolean", example = "true", description = "투표자의 참석 여부입니다. true: 참석, false: 불참")
    private Boolean attending;
}
