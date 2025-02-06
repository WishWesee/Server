package depth.main.wishwesee.domain.auth.service;

import depth.main.wishwesee.domain.auth.domain.Token;
import depth.main.wishwesee.domain.auth.dto.response.AuthRes;
import depth.main.wishwesee.domain.auth.exception.InvalidTokenException;
import depth.main.wishwesee.domain.auth.domain.repository.TokenRepository;
import depth.main.wishwesee.domain.auth.dto.request.RefreshTokenReq;
import depth.main.wishwesee.domain.auth.dto.response.TokenMapping;
import depth.main.wishwesee.global.DefaultAssert;
import depth.main.wishwesee.global.config.security.token.UserPrincipal;
import depth.main.wishwesee.global.payload.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AuthService {

    private final CustomTokenProviderService customTokenProviderService;

    private final TokenRepository tokenRepository;

    @Transactional
    public ResponseEntity<ApiResponse> refresh(RefreshTokenReq refreshTokenReq){
        try {
            String email = customTokenProviderService.getEmailFromToken(refreshTokenReq.getRefreshToken());
            DefaultAssert.isTrue(valid(refreshTokenReq.getRefreshToken()), "토큰 검증에 실패했습니다.");
            TokenMapping tokenMapping = customTokenProviderService.refreshToken(email);
            AuthRes authResponse = AuthRes.builder()
                    .accessToken(tokenMapping.getAccessToken())
                    .refreshToken(tokenMapping.getRefreshToken())
                    .build();
            ApiResponse apiResponse = ApiResponse.builder()
                    .check(true)
                    .information(authResponse)
                    .build();
            return ResponseEntity.ok(apiResponse);
        } catch (ExpiredJwtException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.builder()
                            .check(false)
                            .information("리프레시 토큰이 만료되었습니다. 다시 로그인해주세요.")
                            .build());
        }
    }

    @Transactional
    public ResponseEntity<?> signOut(UserPrincipal userPrincipal){
        Token token = tokenRepository.findByUserEmail(userPrincipal.getEmail())
                .orElseThrow(InvalidTokenException::new);

        tokenRepository.delete(token);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("유저가 로그아웃 되었습니다.")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    private boolean valid(String refreshToken){
        boolean validateCheck = customTokenProviderService.validateToken(refreshToken);
        DefaultAssert.isTrue(validateCheck, "Token 검증에 실패하였습니다.");

        Optional<Token> token = tokenRepository.findByRefreshToken(refreshToken);
        DefaultAssert.isTrue(token.isPresent(), "탈퇴 처리된 회원입니다.");

        //Authentication authentication = customTokenProviderService.getAuthenticationByEmail(token.get().getUserEmail());
        //DefaultAssert.isTrue(token.get().getUserEmail().equals(authentication.getName()), "사용자 인증에 실패하였습니다.");

        return true;
    }

}