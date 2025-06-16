package com.talaty.repository;

import com.talaty.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Notification> findByUserIdAndReadFalseOrderByCreatedAtDesc(Long userId);
    List<Notification> findByUserIdAndSentFalse(Long userId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.read = false")
    Long countUnreadByUserId(@Param("userId") Long userId);

    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.createdAt >= :since")
    List<Notification> findRecentByUserId(@Param("userId") Long userId,
                                          @Param("since") LocalDateTime since);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true, n.readAt = :readAt WHERE n.userId = :userId AND n.read = false")
    void markAllAsReadByUserId(@Param("userId") Long userId, @Param("readAt") LocalDateTime readAt);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.createdAt < :cutoffDate")
    void deleteOldNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);
}
