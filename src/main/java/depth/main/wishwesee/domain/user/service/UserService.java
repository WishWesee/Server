package depth.main.wishwesee.domain.user.service;

import depth.main.wishwesee.domain.user.domain.User;
import depth.main.wishwesee.domain.user.domain.repository.UserRepository;
import depth.main.wishwesee.domain.user.dto.res.UserProfileRes;
import depth.main.wishwesee.global.DefaultAssert;
import depth.main.wishwesee.global.config.security.token.UserPrincipal;
import depth.main.wishwesee.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public ApiResponse getProfile(UserPrincipal userPrincipal) {
        User user = validateUserById(userPrincipal.getId());
        UserProfileRes userProfileRes = UserProfileRes.builder()
                .userId(user.getId())
                .name(user.getName())
                .image(user.getProfile())
                .build();
        return ApiResponse.builder()
                .check(true)
                .information(userProfileRes)
                .build();
    }

    private User validateUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        DefaultAssert.isOptionalPresent(userOptional);
        return userOptional.get();
    }
}
