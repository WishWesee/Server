package depth.main.wishwesee.global.config.security.handler;

import depth.main.wishwesee.domain.auth.domain.Token;
import depth.main.wishwesee.domain.auth.domain.repository.CustomAuthorizationRequestRepository;
import depth.main.wishwesee.domain.auth.domain.repository.TokenRepository;
import depth.main.wishwesee.domain.auth.dto.response.TokenMapping;
import depth.main.wishwesee.domain.auth.service.CustomTokenProviderService;
import depth.main.wishwesee.global.DefaultAssert;
import depth.main.wishwesee.global.config.security.OAuth2Config;
import depth.main.wishwesee.global.config.security.token.UserPrincipal;
import depth.main.wishwesee.global.config.security.util.CustomCookie;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static depth.main.wishwesee.domain.auth.domain.repository.CustomAuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@RequiredArgsConstructor
@Component
public class CustomSimpleUrlAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final CustomTokenProviderService customTokenProviderService;
    private final OAuth2Config oAuth2Config;
    private final TokenRepository tokenRepository;
    private final CustomAuthorizationRequestRepository customAuthorizationRequestRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        DefaultAssert.isAuthentication(!response.isCommitted());

        String targetUrl = determineTargetUrl(request, response, authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        TokenMapping token = customTokenProviderService.createToken(userPrincipal.getEmail());
        CustomCookie.addCookie(response, "Authorization", "Bearer_" + token.getAccessToken(), (int) oAuth2Config.getAuth().getAccessTokenExpirationMsec());
        CustomCookie.addCookie(response, "Refresh_Token", "Bearer_" + token.getRefreshToken(), (int) oAuth2Config.getAuth().getRefreshTokenExpirationMsec());

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CustomCookie.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME).map(Cookie::getValue);

        DefaultAssert.isAuthentication( !(redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) );

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        TokenMapping tokenMapping = customTokenProviderService.createToken(userPrincipal.getEmail());
        Token token = Token.builder()
                .userEmail(tokenMapping.getEmail())
                .refreshToken(tokenMapping.getRefreshToken())
                .build();
        tokenRepository.save(token);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", tokenMapping.getAccessToken())
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        customAuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);

        return oAuth2Config.getOauth2().getAuthorizedRedirectUris()
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    if(authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort()) {
                        return true;
                    }
                    return false;
                });
    }
}
