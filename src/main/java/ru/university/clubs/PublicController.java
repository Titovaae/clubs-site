package ru.university.clubs;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
public class PublicController {
    private final ClubRepository clubs;
    private final EventRepository events;

    public PublicController(ClubRepository clubs, EventRepository events) {
        this.clubs = clubs;
        this.events = events;
    }

    @GetMapping("/")
    public String events(Model model) {
        LocalDate today = LocalDate.now();
        model.addAttribute("upcoming", events.findByEventDateGreaterThanEqualOrderByEventDateAscEventTimeAsc(today));
        model.addAttribute("past", events.findByEventDateLessThanOrderByEventDateDesc(today));
        return "events";
    }

    @GetMapping("/clubs")
    public String clubs(Model model) {
        model.addAttribute("clubs", clubs.findAllByOrderByNameAsc());
        return "clubs";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
