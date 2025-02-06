package depth.main.wishwesee.domain.auth.service;

import depth.main.wishwesee.domain.auth.domain.Token;
import depth.main.wishwesee.domain.auth.domain.repository.TokenRepository;
import depth.main.wishwesee.domain.auth.dto.response.TokenMapping;
import depth.main.wishwesee.domain.auth.exception.ExpiredRefreshTokenException;
import depth.main.wishwesee.global.config.security.token.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;
import java.security.Key;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomTokenProviderService {

    private final CustomUserDetailsService customUserDetailsService;
    private final TokenRepository tokenRepository;

    private Key key;

    @Value("${app.auth.token-secret}")
    private String secret;

    @Value("${app.auth.access-token-expiration-msec}")
    private long accessTokenExpiration;

    @Value("${app.auth.refresh-token-expiration-msec}")
    private long refreshTokenExpiration;

    @PostConstruct
    protected void init() {
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String createAccessToken(String email) {
        Claims claims = Jwts.claims().setSubject(email);
        return Jwts.builder().setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(convertKST(accessTokenExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setExpiration(convertKST(refreshTokenExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Transactional
    public TokenMapping createToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String accessToken = createAccessToken(userPrincipal.getEmail());
        String refreshToken = createRefreshToken(userPrincipal.getEmail());
        saveRefreshToken(userPrincipal.getEmail(), refreshToken);
        return TokenMapping.builder()
                .email(userPrincipal.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private Date convertKST(long expirationMsec) {
        long expirationTime = System.currentTimeMillis() + expirationMsec;
        Instant instant = Instant.ofEpochMilli(expirationTime);
        ZonedDateTime kstDateTime = instant.atZone(ZoneId.of("Asia/Seoul"));
        return Date.from(kstDateTime.toInstant());
    }

    private void saveRefreshToken(String email, String refreshToken) {
        Token token = Token.builder()
                .userEmail(email)
                .refreshToken(refreshToken)
                .build();
        tokenRepository.save(token);
    }

    @Transactional
    public TokenMapping refreshToken(String email) {
        Token token = tokenRepository.findByUserEmail(email).orElseThrow(InvalidParameterException::new);
        isNotExpiredRefreshToken(token.getRefreshToken());
        String accessToken = createAccessToken(email);
        String refreshToken = token.getRefreshToken();
        return TokenMapping.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build().parseClaimsJws(token).getBody().getSubject();
    }

    public UsernamePasswordAuthenticationToken getAuthenticationByEmail(String token) {
        String email = getEmailFromToken(token);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException ex) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException ex) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException ex) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException ex) {
            log.error("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    public void isNotExpiredRefreshToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (ExpiredJwtException ex) {
            log.error("만료된 JWT 토큰입니다.");
            throw new ExpiredRefreshTokenException();
        }
    }
}
