package ru.university.clubs;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/organizer")
public class OrganizerController {
    private final UserService users;
    private final ClubRepository clubs;
    private final EventRepository events;
    private final ClubRequestRepository requests;
    private final ClubService clubService;
    private final EventService eventService;

    public OrganizerController(UserService users, ClubRepository clubs, EventRepository events,
                               ClubRequestRepository requests, ClubService clubService, EventService eventService) {
        this.users = users;
        this.clubs = clubs;
        this.events = events;
        this.requests = requests;
        this.clubService = clubService;
        this.eventService = eventService;
    }

    @GetMapping
    public String dashboard(Principal principal, Model model) {
        AppUser me = users.current(principal);
        model.addAttribute("clubs", clubs.findByOwnerIdOrderByNameAsc(me.getId()));
        model.addAttribute("events", events.findByOwnerIdOrderByEventDateDesc(me.getId()));
        return "organizer/dashboard";
    }

    // ---------- Кружки ----------
    @GetMapping("/clubs/new")
    public String newClub(Model model) {
        Club club = new Club();
        model.addAttribute("club", club);
        model.addAttribute("rows", rowsFrom(club));
        return "organizer/club-form";
    }

    @GetMapping("/clubs/{id}/edit")
    public String editClub(@PathVariable Long id, Principal principal, Model model) {
        Club club = clubService.getOwned(id, users.current(principal));
        model.addAttribute("club", club);
        model.addAttribute("rows", rowsFrom(club));
        return "organizer/club-form";
    }

    @PostMapping("/clubs")
    public String saveClub(@Valid @ModelAttribute("club") Club club, BindingResult result, Principal principal,
                           @RequestParam(name = "start", required = false) List<String> start,
                           @RequestParam(name = "end", required = false) List<String> end,
                           Model model) {
        // ВРЕМЕННО ОТКЛЮЧЕНО: автоответ
        /*
        if (club.isAutoReplySend() && (club.getAutoReply() == null || club.getAutoReply().isBlank())) {
            result.rejectValue("autoReply", "required", "Укажите текст автоответа или снимите галочку");
        }
        */
        List<ScheduleRow> rows = new ArrayList<>();
        List<ClubSession> sessions = new ArrayList<>();
        boolean scheduleOk = readSchedule(start, end, rows, sessions);
        if (result.hasErrors() || !scheduleOk) {
            model.addAttribute("rows", rows);
            if (!scheduleOk) {
                model.addAttribute("scheduleError",
                    "Для выбранного дня укажите и начало, и окончание, причём окончание должно быть позже начала.");
            }
            return "organizer/club-form";
        }
        clubService.save(club, sessions, users.current(principal));
        return "redirect:/organizer";
    }

    @PostMapping("/clubs/{id}/delete")
    public String deleteClub(@PathVariable Long id, Principal principal) {
        clubService.delete(id, users.current(principal));
        return "redirect:/organizer";
    }

    // ---------- Мероприятия ----------
    @GetMapping("/events/new")
    public String newEvent(Principal principal, Model model) {
        model.addAttribute("event", new Event());
        addClubChoices(model, users.current(principal), null);
        return "organizer/event-form";
    }

    @GetMapping("/events/{id}/edit")
    public String editEvent(@PathVariable Long id, Principal principal, Model model) {
        AppUser me = users.current(principal);
        Event event = eventService.getOwned(id, me);
        model.addAttribute("event", event);
        addClubChoices(model, me, event.getClub() == null ? null : event.getClub().getId());
        return "organizer/event-form";
    }

    @PostMapping("/events")
    public String saveEvent(@Valid @ModelAttribute("event") Event event, BindingResult result,
                            @RequestParam(required = false) Long clubId, Principal principal, Model model) {
        AppUser me = users.current(principal);
        if (result.hasErrors()) {
            addClubChoices(model, me, clubId);
            return "organizer/event-form";
        }
        eventService.save(event, clubId, me);
        return "redirect:/organizer";
    }

    @PostMapping("/events/{id}/delete")
    public String deleteEvent(@PathVariable Long id, Principal principal) {
        eventService.delete(id, users.current(principal));
        return "redirect:/organizer";
    }

    private void addClubChoices(Model model, AppUser me, Long selectedClubId) {
        model.addAttribute("myClubs", clubs.findByOwnerIdOrderByNameAsc(me.getId()));
        model.addAttribute("selectedClubId", selectedClubId);
    }

    // ---------- Заявки студентов ----------
    @GetMapping("/requests")
    public String requests(Principal principal, Model model) {
        model.addAttribute("requests", requests.findByClubOwnerIdOrderByCreatedAtDesc(users.current(principal).getId()));
        return "organizer/requests";
    }

    @PostMapping("/requests/{id}/reply")
    public String reply(@PathVariable Long id, @RequestParam String text, Principal principal) {
        clubService.reply(id, users.current(principal), text, false);
        return "redirect:/organizer/requests";
    }

    // ВРЕМЕННО ОТКЛЮЧЕНО: автоответ
    /*
    @PostMapping("/requests/{id}/auto-reply")
    public String autoReply(@PathVariable Long id, Principal principal) {
        clubService.reply(id, users.current(principal), null, true);
        return "redirect:/organizer/requests";
    }
    */

    // ---------- Расписание ----------
    private List<ScheduleRow> rowsFrom(Club club) {
        List<ScheduleRow> rows = new ArrayList<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            ClubSession found = club.getSessions().stream()
                .filter(x -> x.getDayOfWeek() == day).findFirst().orElse(null);
            rows.add(found == null
                ? new ScheduleRow(day, "", "")
                : new ScheduleRow(day, found.getStartTime().toString(), found.getEndTime().toString()));
        }
        return rows;
    }

    /** Читает 7 строк формы (пн–вс). Возвращает false, если время заполнено неверно. */
    private boolean readSchedule(List<String> start, List<String> end, List<ScheduleRow> rows, List<ClubSession> sessions) {
        boolean ok = true;
        DayOfWeek[] days = DayOfWeek.values();
        for (int i = 0; i < days.length; i++) {
            String s = value(start, i);
            String e = value(end, i);
            rows.add(new ScheduleRow(days[i], s, e));
            if (s.isEmpty() && e.isEmpty()) continue;
            try {
                LocalTime st = LocalTime.parse(s);
                LocalTime en = LocalTime.parse(e);
                if (!en.isAfter(st)) { ok = false; continue; }
                ClubSession session = new ClubSession();
                session.setDayOfWeek(days[i]);
                session.setStartTime(st);
                session.setEndTime(en);
                sessions.add(session);
            } catch (DateTimeParseException ex) {
                ok = false;
            }
        }
        return ok;
    }

    private String value(List<String> list, int i) {
        return list != null && i < list.size() && list.get(i) != null ? list.get(i).trim() : "";
    }
}
