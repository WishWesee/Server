package depth.main.wishwesee.domain.auth.domain;

import depth.main.wishwesee.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Token extends BaseEntity {

    @Id
    @Column(name = "user_email" ,nullable = false, unique = true)
    private String userEmail;

    @Lob
    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Builder
    public Token(String userEmail, String refreshToken) {
        this.userEmail = userEmail;
        this.refreshToken = refreshToken;
    }

}