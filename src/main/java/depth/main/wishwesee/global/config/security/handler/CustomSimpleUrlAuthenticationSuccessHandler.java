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
import org.springframework.beans.factory.annotation.Value;
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
    private final CustomAuthorizationRequestRepository customAuthorizationRequestRepository;

    @Value("${custom.success-handler.redirect-uri}")
    private String HOST;

    private static final String BEARER = "Bearer_";
    private static final String AUTHORIZATION = "Authorization";
    private static final String REFRESH_TOKEN = "Refresh_Token";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        DefaultAssert.isAuthentication(!response.isCommitted());

        String targetUrl = determineTargetUrl(request, response, authentication);
        TokenMapping tokenMapping = customTokenProviderService.createToken(authentication);
        CustomCookie.addCookie(response, AUTHORIZATION, BEARER + tokenMapping.getAccessToken(), (int) oAuth2Config.getAuth().getAccessTokenExpirationMsec());
        CustomCookie.addCookie(response, REFRESH_TOKEN, BEARER + tokenMapping.getRefreshToken(), (int) oAuth2Config.getAuth().getRefreshTokenExpirationMsec());

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CustomCookie.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME).map(Cookie::getValue);
        DefaultAssert.isAuthentication(!(redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())));
        //String targetUrl = redirectUri.orElse(getDefaultTargetUrl());
        return UriComponentsBuilder.fromUriString(HOST)
                .build()
                .toUriString();
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
