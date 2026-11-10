package ru.university.clubs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/** Раз в сутки в 00:00 удаляет уведомления, которым больше недели. */
@Component
public class NotificationCleanup {
    private static final Logger log = LoggerFactory.getLogger(NotificationCleanup.class);

    private final NotificationRepository notifications;

    public NotificationCleanup(NotificationRepository notifications) {
        this.notifications = notifications;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void cleanup() {
        int removed = notifications.deleteOlderThan(LocalDateTime.now().minusDays(Notification.KEEP_DAYS));
        log.info("Очистка уведомлений: удалено {}", removed);
    }
}
