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
        // 1. 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3. User 생성
        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .name(request.getName())
                .provider("local")
                .emailVerified(false)  // 이메일 인증 안 함
                .themePreference("light")
                .build();

        userRepository.save(user);

        return new SignupDto.Response("회원가입 성공!", request.getEmail());
    }

    public User login(LoginDto.Request request) {
        // 1. 이메일로 사용자 찾기
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("이메일 또는 비밀번호가 잘못되었습니다."));

        // 2. 소셜 로그인 계정 체크
        if (!"local".equals(user.getProvider())) {
            throw new RuntimeException("소셜 로그인 계정입니다. " + user.getProvider() + "로 로그인해주세요.");
        }

        // 3. 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("이메일 또는 비밀번호가 잘못되었습니다.");
        }

        return user;
    }
}