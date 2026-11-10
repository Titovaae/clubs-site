package ru.university.clubs;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EventService {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final EventRepository events;
    private final ClubRepository clubs;
    private final ClubFollowRepository follows;
    private final NotificationRepository notifications;

    public EventService(EventRepository events, ClubRepository clubs,
                        ClubFollowRepository follows, NotificationRepository notifications) {
        this.events = events;
        this.clubs = clubs;
        this.follows = follows;
        this.notifications = notifications;
    }

    /** Мероприятие текущего организатора (иначе 403). */
    @Transactional(readOnly = true)
    public Event getOwned(Long id, AppUser owner) {
        Event e = events.findById(id).orElseThrow();
        checkOwner(e, owner);
        return e;
    }

    /**
     * Сохраняет мероприятие. При СОЗДАНИИ мероприятия кружка уведомление
     * автоматически получают все студенты, следящие за этим кружком.
     */
    @Transactional
    public void save(Event form, Long clubId, AppUser owner) {
        boolean isNew = form.getId() == null;
        Event target;
        if (isNew) {
            target = new Event();
            target.setOwner(owner);
        } else {
            target = events.findById(form.getId()).orElseThrow();
            checkOwner(target, owner);
        }
        target.setName(form.getName());
        target.setDescription(form.getDescription());
        target.setEventDate(form.getEventDate());
        target.setEventTime(form.getEventTime());
        target.setRequirements(form.getRequirements());
        target.setPlace(form.getPlace());
        target.setContacts(form.getContacts());

        Club club = null;
        if (clubId != null) {
            club = clubs.findById(clubId).orElseThrow();
            if (!club.getOwner().getId().equals(owner.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        }
        target.setClub(club);
        events.save(target);

        if (isNew && club != null) {
            notifyFollowers(target);
        }
    }

    @Transactional
    public void delete(Long id, AppUser owner) {
        Event e = events.findById(id).orElseThrow();
        checkOwner(e, owner);
        notifications.deleteByEventId(id);
        events.delete(e);
    }

    /** Рассылает уведомление всем студентам, следящим за кружком мероприятия. */
    @Transactional
    public void notifyFollowers(Event event) {
        if (event.getClub() == null) return;
        String text = "Новое мероприятие «" + event.getName() + "» кружка «" + event.getClub().getName()
            + "», " + event.getEventDate().format(DATE);
        for (ClubFollow f : follows.findByClubId(event.getClub().getId())) {
            Notification n = new Notification();
            n.setUser(f.getStudent());
            n.setEvent(event);
            n.setText(text);
            n.setCreatedAt(LocalDateTime.now());
            notifications.save(n);
        }
    }

    private void checkOwner(Event e, AppUser user) {
        if (!e.getOwner().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}
