package ru.university.clubs;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final ClubRepository clubs;
    private final EventRepository events;
    private final ClubService clubService;

    public AdminController(ClubRepository clubs, EventRepository events, ClubService clubService) {
        this.clubs = clubs;
        this.events = events;
        this.clubService = clubService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("clubs", clubs.findAllByOrderByNameAsc());
        model.addAttribute("events", events.findAllByOrderByEventDateDesc());
        return "admin/dashboard";
    }

    // ---------- Кружки ----------
    @GetMapping("/clubs/new")
    public String newClub(Model model) {
        Club club = new Club();
        model.addAttribute("club", club);
        model.addAttribute("rows", rowsFrom(club));
        return "admin/club-form";
    }

    @GetMapping("/clubs/{id}/edit")
    public String editClub(@PathVariable Long id, Model model) {
        Club club = clubs.findById(id).orElseThrow();
        model.addAttribute("club", club);
        model.addAttribute("rows", rowsFrom(club));
        return "admin/club-form";
    }

    @PostMapping("/clubs")
    public String saveClub(@Valid @ModelAttribute("club") Club club, BindingResult result,
                           @RequestParam(name = "start", required = false) List<String> start,
                           @RequestParam(name = "end", required = false) List<String> end,
                           Model model) {
        List<ScheduleRow> rows = new ArrayList<>();
        List<ClubSession> sessions = new ArrayList<>();
        boolean scheduleOk = readSchedule(start, end, rows, sessions);
        if (result.hasErrors() || !scheduleOk) {
            model.addAttribute("rows", rows);
            if (!scheduleOk) {
                model.addAttribute("scheduleError",
                    "Для выбранного дня укажите и начало, и окончание, причём окончание должно быть позже начала.");
            }
            return "admin/club-form";
        }
        clubService.save(club, sessions);
        return "redirect:/admin";
    }

    @PostMapping("/clubs/{id}/delete")
    public String deleteClub(@PathVariable Long id) {
        clubs.deleteById(id);
        return "redirect:/admin";
    }

    // ---------- Мероприятия ----------
    @GetMapping("/events/new")
    public String newEvent(Model model) {
        model.addAttribute("event", new Event());
        return "admin/event-form";
    }

    @GetMapping("/events/{id}/edit")
    public String editEvent(@PathVariable Long id, Model model) {
        model.addAttribute("event", events.findById(id).orElseThrow());
        return "admin/event-form";
    }

    @PostMapping("/events")
    public String saveEvent(@Valid @ModelAttribute("event") Event event, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/event-form";
        }
        events.save(event);
        return "redirect:/admin";
    }

    @PostMapping("/events/{id}/delete")
    public String deleteEvent(@PathVariable Long id) {
        events.deleteById(id);
        return "redirect:/admin";
    }

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
