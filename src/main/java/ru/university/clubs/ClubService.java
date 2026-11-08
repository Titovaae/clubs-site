package ru.university.clubs;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClubService {
    private final ClubRepository clubs;
    private final ClubRequestRepository requests;
    private final NotificationRepository notifications;

    public ClubService(ClubRepository clubs, ClubRequestRepository requests, NotificationRepository notifications) {
        this.clubs = clubs;
        this.requests = requests;
        this.notifications = notifications;
    }

    // ---------- Кружки ----------
    /** Кружок текущего организатора (иначе 403). */
    @Transactional(readOnly = true)
    public Club getOwned(Long id, AppUser owner) {
        Club club = clubs.findById(id).orElseThrow();
        checkOwner(club, owner);
        return club;
    }

    /** Сохраняет кружок вместе с расписанием и обновляет дату последнего изменения. */
    @Transactional
    public void save(Club form, List<ClubSession> sessions, AppUser owner) {
        Club target;
        if (form.getId() == null) {
            target = new Club();
            target.setOwner(owner);
        } else {
            target = clubs.findById(form.getId()).orElseThrow();
            checkOwner(target, owner);
        }
        target.setName(form.getName());
        target.setDescription(form.getDescription());
        target.setRequirements(form.getRequirements());
        target.setPlace(form.getPlace());
        target.setAutoReply(form.getAutoReply());
        target.setAutoReplySend(form.isAutoReplySend());
        target.setUpdatedAt(LocalDateTime.now());
        replaceSessions(target, sessions);
        clubs.save(target);
    }

    @Transactional
    public void delete(Long id, AppUser owner) {
        Club club = clubs.findById(id).orElseThrow();
        checkOwner(club, owner);
        requests.deleteByClubId(id);
        clubs.delete(club);
    }

    @Transactional
    public void setSchedule(Long clubId, List<ClubSession> sessions) {
        Club target = clubs.findById(clubId).orElseThrow();
        replaceSessions(target, sessions);
        clubs.save(target);
    }

    // ---------- Заявки ----------
    @Transactional
    public void apply(Long clubId, AppUser student) {
        Club club = clubs.findById(clubId).orElseThrow();
        if (requests.existsByClubIdAndStudentId(clubId, student.getId())) return;
        ClubRequest r = new ClubRequest();
        r.setClub(club);
        r.setStudent(student);
        r.setCreatedAt(LocalDateTime.now());
        requests.save(r);
        notify(student, "Заявка в кружок «" + club.getName() + "» отправлена организатору.");
        if (club.isAutoReplySend() && hasText(club.getAutoReply())) {
            answer(r, club.getAutoReply());
        }
    }

    /** Ответ организатора: свой текст (auto = false) или автоответ кружка (auto = true). */
    @Transactional
    public void reply(Long requestId, AppUser organizer, String text, boolean auto) {
        ClubRequest r = requests.findById(requestId).orElseThrow();
        checkOwner(r.getClub(), organizer);
        if (r.isAnswered()) return;
        String message = auto ? r.getClub().getAutoReply() : text;
        if (!hasText(message)) return;
        answer(r, message.trim());
    }

    private void answer(ClubRequest r, String message) {
        r.setReplyText(message);
        r.setRepliedAt(LocalDateTime.now());
        requests.save(r);
        notify(r.getStudent(), "Ответ организатора кружка «" + r.getClub().getName() + "» ("
            + r.getClub().getOwner().getFullName() + "): " + message);
    }

    private void notify(AppUser user, String text) {
        Notification n = new Notification();
        n.setUser(user);
        n.setText(text);
        n.setCreatedAt(LocalDateTime.now());
        notifications.save(n);
    }

    private static boolean hasText(String s) {
        return s != null && !s.isBlank();
    }

    private void checkOwner(Club club, AppUser user) {
        if (!club.getOwner().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    private void replaceSessions(Club club, List<ClubSession> sessions) {
        club.getSessions().clear();
        for (ClubSession s : sessions) {
            s.setClub(club);
            club.getSessions().add(s);
        }
    }
}
