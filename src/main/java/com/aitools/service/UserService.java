package com.aitools.service;

import com.aitools.dto.LoginDto;
import com.aitools.dto.SignupDto;
import com.aitools.entity.User;
import com.aitools.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public SignupDto.Response signup(SignupDto.Request request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        
        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .name(request.getName())
                .provider("local")
                .emailVerified(false)
                .themePreference("light")
                .build();

        userRepository.save(user);

        return new SignupDto.Response("회원가입 성공!", request.getEmail());
    }

    public User login(LoginDto.Request request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("이메일 또는 비밀번호가 잘못되었습니다."));


        if (!"local".equals(user.getProvider())) {
            throw new RuntimeException("소셜 로그인 계정입니다. " + user.getProvider() + "로 로그인해주세요.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("이메일 또는 비밀번호가 잘못되었습니다.");
        }

        return user;
    }
}