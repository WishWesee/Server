package depth.main.wishwesee.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "UserProfileRes: 회원 프로필 조회 응답 객체", description = "GET: /api/v1/user에서 사용합니다.")
public class UserProfileRes {

    @Schema(type = "Long", example = "1", description = "사용자의 userId입니다.")
    private Long userId;

    @Schema(type = "String", example = "김뎁스", description = "사용자의 이름입니다.")
    private String name;

    @Schema(type = "String", example = "https://lh3.googleusercontent.com/a/ACg8ocLU6k_7elIaIDZKUmqVPU3bg-Eajwu-gV", description = "사용자의 프로필 사진입니다.")
    private String image;
}
