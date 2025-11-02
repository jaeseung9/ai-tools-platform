package com.aitools.service;

import com.aitools.entity.User;
import com.aitools.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // ===== 디버깅 로그 =====
        System.out.println("========================================");
        System.out.println("OAuth Login Attempt");
        System.out.println("Provider: " + registrationId);
        System.out.println("Attributes: " + attributes);
        System.out.println("========================================");

        try {
            String oauthId = extractOAuthId(registrationId, attributes);
            System.out.println("✓ OAuth ID: " + oauthId);

            String email = extractEmail(registrationId, attributes);
            System.out.println("✓ Email: " + email);

            String name = extractName(registrationId, attributes);
            System.out.println("✓ Name: " + name);

            String profileImage = extractProfileImage(registrationId, attributes);
            System.out.println("✓ Profile Image: " + profileImage);

            User user = userRepository.findByOauthId(oauthId)
                    .orElseGet(() -> {
                        System.out.println("✓ Creating new user!");
                        User newUser = createNewUser(oauthId, registrationId, email, name, profileImage);
                        System.out.println("✓ User created with ID: " + newUser.getId());
                        return newUser;
                    });

            if (user.getId() != null) {
                System.out.println("✓ Existing user found with ID: " + user.getId());
            }

            System.out.println("========================================");
            return oAuth2User;

        } catch (Exception e) {
            System.err.println("✗ ERROR during OAuth login:");
            System.err.println("✗ Message: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private String extractOAuthId(String provider, Map<String, Object> attributes) {
        if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return provider + "_" + response.get("id");
        } else if ("kakao".equals(provider)) {
            return provider + "_" + attributes.get("id");
        }
        throw new OAuth2AuthenticationException("Unsupported provider: " + provider);
    }

    private String extractEmail(String provider, Map<String, Object> attributes) {
        if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return (String) response.get("email");
        } else if ("kakao".equals(provider)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            return kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
        }
        return null;
    }

    private String extractName(String provider, Map<String, Object> attributes) {
        if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return (String) response.get("name");
        } else if ("kakao".equals(provider)) {
            Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
            return properties != null ? (String) properties.get("nickname") : null;
        }
        return null;
    }

    private String extractProfileImage(String provider, Map<String, Object> attributes) {
        if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return (String) response.get("profile_image");
        } else if ("kakao".equals(provider)) {
            Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
            return properties != null ? (String) properties.get("profile_image") : null;
        }
        return null;
    }

    private User createNewUser(String oauthId, String provider, String email, String name, String profileImage) {
        User user = User.builder()
                .oauthId(oauthId)
                .provider(provider)
                .email(email)
                .name(name)
                .profileImage(profileImage)
                .themePreference("light")
                .emailVerified(true)
                .build();

        return userRepository.save(user);
    }
}