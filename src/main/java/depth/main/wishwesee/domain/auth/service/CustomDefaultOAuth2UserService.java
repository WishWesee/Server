package depth.main.wishwesee.domain.auth.service;

import depth.main.wishwesee.domain.user.domain.Provider;
import depth.main.wishwesee.domain.user.domain.Role;
import depth.main.wishwesee.domain.user.domain.User;
import depth.main.wishwesee.domain.user.domain.repository.UserRepository;
import depth.main.wishwesee.global.DefaultAssert;
import depth.main.wishwesee.global.config.security.auth.OAuth2UserInfo;
import depth.main.wishwesee.global.config.security.auth.OAuth2UserInfoFactory;
import depth.main.wishwesee.global.config.security.token.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomDefaultOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (Exception e) {
            log.error("Error processing OAuth2 user: {}", e.getMessage(), e);
            DefaultAssert.isAuthentication(e.getMessage());
        }
        return null;
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
        DefaultAssert.isAuthentication(!oAuth2UserInfo.getEmail().isEmpty());

        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;
        if(userOptional.isPresent()) {
            user = userOptional.get();
            DefaultAssert.isAuthentication(user.getProvider().equals(Provider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId())));
        } else {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }
        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = User.builder()
                .provider(Provider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))
                .providerId(oAuth2UserInfo.getId())
                .email(oAuth2UserInfo.getEmail())
                .name(oAuth2UserInfo.getName())
                .profile(oAuth2UserInfo.getImageUrl())
                .password(encodePassword(oAuth2UserInfo.getId()))
                .role(Role.USER)
                .build();
        User savedUser = userRepository.save(user);
        return savedUser;
    }

    private String encodePassword(String password) {
        return customPasswordEncoder().encode(password);
    }

    @Bean
    public PasswordEncoder customPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
