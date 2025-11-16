package com.aitools.repository;

import com.aitools.entity.ImageHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ImageHistoryRepository extends JpaRepository<ImageHistory, Long> {

    List<ImageHistory> findByUserIdOrderByCreatedAtDesc(Long userId);

    void deleteByIdAndUserId(Long id, Long userId);
}