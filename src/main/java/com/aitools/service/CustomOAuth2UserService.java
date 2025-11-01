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

        String oauthId = extractOAuthId(registrationId, attributes);
        String email = extractEmail(registrationId, attributes);
        String name = extractName(registrationId, attributes);
        String profileImage = extractProfileImage(registrationId, attributes);

        User user = userRepository.findByOauthId(oauthId)
                .orElseGet(() -> createNewUser(oauthId, registrationId, email, name, profileImage));

        return oAuth2User;
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