package com.aitools.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 100)  // nullable 제거!
    private String oauthId;  // 소셜: "naver_12345", 일반: null

    @Column(length = 20)
    private String provider;  // "naver", "kakao", "local"

    @Column(unique = true, length = 100)  // 이메일은 항상 unique!
    private String email;

    @Column(length = 50)
    private String name;

    @Column(length = 500)
    private String profileImage;

    @Column(length = 100)
    private String password;  // 일반 로그인용 (암호화 저장!)

    @Column(length = 20)
    private String themePreference = "light";

    @Column(nullable = false)
    private Boolean emailVerified = false;  // 이메일 인증 여부 (나중에 사용)

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
