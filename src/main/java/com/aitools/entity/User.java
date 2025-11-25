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

    @Column(unique = true, length = 100)
    private String oauthId;

    @Column(length = 20)
    private String provider;

    @Column(unique = true, length = 100)
    private String email;

    @Column(length = 50)
    private String name;

    @Column(length = 500)
    private String profileImage;

    @Column(length = 100)
    private String password;

    @Column(length = 20)
    private String themePreference = "light";

    @Column(nullable = false)
    private Boolean emailVerified = false;

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
