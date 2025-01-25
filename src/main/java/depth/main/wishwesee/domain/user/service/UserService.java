package depth.main.wishwesee.domain.user.service;

import depth.main.wishwesee.domain.user.domain.User;
import depth.main.wishwesee.domain.user.domain.repository.UserRepository;
import depth.main.wishwesee.domain.user.dto.response.UserProfileRes;
import depth.main.wishwesee.global.DefaultAssert;
import depth.main.wishwesee.global.config.security.token.UserPrincipal;
import depth.main.wishwesee.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    @Transactional
    public void encryptUserInfoOnDelete(UserPrincipal userPrincipal) {
        User user = validateUserById(userPrincipal.getId());
        user.updateUserInfo(
                encrypt(user.getEmail()),
                encrypt(user.getPassword()),
                encrypt(user.getProfile()),
                encrypt(user.getProviderId())
        );
    }

    private static String encrypt(String input) {
        try {
            // SHA-256 해시 알고리즘 사용
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("암호화 알고리즘을 찾을 수 없습니다.", e);
        }
    }

    private User validateUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        DefaultAssert.isOptionalPresent(userOptional, "사용자가 존재하지 않습니다.");
        return userOptional.get();
    }
}
