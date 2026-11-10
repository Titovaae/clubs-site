package ru.university.clubs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    /** Уведомления студента, полученные не раньше указанного момента (новые сверху). */
    List<Notification> findByUserIdAndCreatedAtAfterOrderByCreatedAtDescIdDesc(Long userId, LocalDateTime after);

    void deleteByEventId(Long eventId);

    /** Удаляет уведомления, полученные раньше указанного момента. */
    @Modifying
    @Transactional
    @Query("delete from Notification n where n.createdAt < :limit")
    int deleteOlderThan(@Param("limit") LocalDateTime limit);
}
