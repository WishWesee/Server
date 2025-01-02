package depth.main.wishwesee.domain.auth.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TokenMapping {
    private String email;
    private String accessToken;
    private String refreshToken;
}
