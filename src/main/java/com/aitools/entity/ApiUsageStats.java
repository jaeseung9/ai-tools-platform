package com.aitools.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_usage_stats",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_user_tool_month",
                        columnNames = {"user_id", "tool_type", "year_month"}
                )
        },
        indexes = {
                @Index(name = "idx_stats_user_month",
                        columnList = "user_id, year_month")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiUsageStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 20)
    private String toolType;  // "CHAT", "IMAGE", "GRAMMAR"

    @Column(nullable = false)
    private Integer usageCount = 0;

    @Column(nullable = false)
    private Double totalCost = 0.0;

    @Column(nullable = false, length = 7, name = "`year_month`")  // 백틱 추가
    private String yearMonth;

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