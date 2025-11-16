package com.aitools.repository;

import com.aitools.entity.GrammarHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GrammarHistoryRepository extends JpaRepository<GrammarHistory, Long> {

    /**
     * 사용자별 맞춤법 검사 기록 조회 (최신순)
     */
    List<GrammarHistory> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 특정 기록 삭제 (본인 확인)
     */
    void deleteByIdAndUserId(Long id, Long userId);
}
