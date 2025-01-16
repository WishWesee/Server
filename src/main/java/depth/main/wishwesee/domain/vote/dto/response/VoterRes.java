package depth.main.wishwesee.domain.vote.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoterRes {

    @Schema(type = "int", example = "5", description = "투표자의 수입니다.")
    public int voterNum;

    @Schema(type = "array", example = "[\"김위시\", \"홍길동\"]", description = "투표자의 이름 목록입니다.")
    private List<String> voterNames;
}
