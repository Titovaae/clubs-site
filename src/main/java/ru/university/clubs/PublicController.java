package ru.university.clubs;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Controller
public class PublicController {
    private static final int PAGE_SIZE = 10;   // кружков на странице списка
    private static final int PANEL_SIZE = 6;   // изображений в боковой панели

    private final ClubRepository clubs;
    private final EventRepository events;
    private final AppUserRepository users;
    private final ClubFollowRepository follows;

    public PublicController(ClubRepository clubs, EventRepository events,
                            AppUserRepository users, ClubFollowRepository follows) {
        this.clubs = clubs;
        this.events = events;
        this.users = users;
        this.follows = follows;
    }

    @GetMapping("/")
    public String events(@RequestParam(required = false) String month, Model model) {
        YearMonth ym;
        try {
            ym = month == null ? YearMonth.now() : YearMonth.parse(month);
        } catch (DateTimeParseException e) {
            ym = YearMonth.now();
        }

        // дни месяца, в которые есть мероприятия
        Set<LocalDate> marked = new HashSet<>();
        for (Event e : events.findByEventDateBetween(ym.atDay(1), ym.atEndOfMonth())) {
            marked.add(e.getEventDate());
        }

        // сетка календаря: недели с понедельника
        LocalDate today = LocalDate.now();
        List<List<CalendarDay>> weeks = new ArrayList<>();
        List<CalendarDay> week = new ArrayList<>();
        int offset = ym.atDay(1).getDayOfWeek().getValue() - 1;
        for (int i = 0; i < offset; i++) {
            week.add(new CalendarDay(null, false, false));
        }
        for (int d = 1; d <= ym.lengthOfMonth(); d++) {
            LocalDate date = ym.atDay(d);
            week.add(new CalendarDay(date, marked.contains(date), date.equals(today)));
            if (week.size() == 7) {
                weeks.add(week);
                week = new ArrayList<>();
            }
        }
        if (!week.isEmpty()) {
            while (week.size() < 7) {
                week.add(new CalendarDay(null, false, false));
            }
            weeks.add(week);
        }

        String monthName = ym.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, Locale.forLanguageTag("ru"));
        model.addAttribute("monthTitle", Character.toUpperCase(monthName.charAt(0)) + monthName.substring(1) + " " + ym.getYear());
        model.addAttribute("prevMonth", ym.minusMonths(1).toString());
        model.addAttribute("nextMonth", ym.plusMonths(1).toString());
        model.addAttribute("weeks", weeks);
        model.addAttribute("events", events.findAllByOrderByEventDateAscEventTimeAscIdAsc());
        return "events";
    }

    @GetMapping("/events/{id}")
    public String event(@PathVariable Long id, Model model) {
        model.addAttribute("event", events.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
        return "event";
    }

    @GetMapping("/events/day/{date}")
    public String eventsOfDay(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, Model model) {
        model.addAttribute("date", date);
        model.addAttribute("month", YearMonth.from(date).toString());
        model.addAttribute("events", events.findByEventDateOrderByEventTimeAsc(date));
        return "events-day";
    }

    @GetMapping("/clubs")
    public String clubs(@RequestParam(defaultValue = "1") int page, Model model) {
        Sort sort = Sort.by("name", "id");
        page = Math.max(page, 1);
        Page<Club> result = clubs.findAll(PageRequest.of(page - 1, PAGE_SIZE, sort));
        if (result.getTotalPages() > 0 && page > result.getTotalPages()) {
            page = result.getTotalPages();
            result = clubs.findAll(PageRequest.of(page - 1, PAGE_SIZE, sort));
        }
        model.addAttribute("clubs", result.getContent());
        model.addAttribute("page", page);
        model.addAttribute("totalPages", result.getTotalPages());
        return "clubs";
    }

    @GetMapping("/clubs/{id}")
    public String club(@PathVariable Long id, Principal principal, Model model) {
        List<Club> all = clubs.findAllByOrderByNameAscIdAsc();
        int index = -1;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(id)) {
                index = i;
                break;
            }
        }
        if (index < 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        // окно из 6 кружков вокруг текущего
        int from = Math.max(0, Math.min(index - PANEL_SIZE / 2 + 1, all.size() - PANEL_SIZE));
        int to = Math.min(all.size(), from + PANEL_SIZE);

        model.addAttribute("club", all.get(index));
        model.addAttribute("panel", all.subList(from, to));
        model.addAttribute("prev", index > 0 ? all.get(index - 1) : null);
        model.addAttribute("next", index < all.size() - 1 ? all.get(index + 1) : null);
        model.addAttribute("listPage", index / PAGE_SIZE + 1);

        // следит ли текущий студент за кружком
        boolean following = false;
        if (principal != null) {
            AppUser u = users.findByUsername(principal.getName()).orElse(null);
            following = u != null && u.getRole() == Role.STUDENT && follows.existsByClubIdAndStudentId(id, u.getId());
        }
        model.addAttribute("following", following);
        return "club";
    }
}
