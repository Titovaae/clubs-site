package ru.university.clubs;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClubService {
    private final ClubRepository clubs;

    public ClubService(ClubRepository clubs) {
        this.clubs = clubs;
    }

    /** Сохраняет кружок вместе с расписанием (старое расписание заменяется новым). */
    @Transactional
    public void save(Club form, List<ClubSession> sessions) {
        Club target = form.getId() == null ? new Club() : clubs.findById(form.getId()).orElseThrow();
        target.setName(form.getName());
        target.setDescription(form.getDescription());
        target.setRequirements(form.getRequirements());
        target.setOrganizer(form.getOrganizer());
        target.setContacts(form.getContacts());
        target.setPlace(form.getPlace());
        replaceSessions(target, sessions);
        clubs.save(target);
    }

    @Transactional
    public void setSchedule(Long clubId, List<ClubSession> sessions) {
        Club target = clubs.findById(clubId).orElseThrow();
        replaceSessions(target, sessions);
        clubs.save(target);
    }

    private void replaceSessions(Club club, List<ClubSession> sessions) {
        club.getSessions().clear();
        for (ClubSession s : sessions) {
            s.setClub(club);
            club.getSessions().add(s);
        }
    }
}
