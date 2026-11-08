package ru.university.clubs;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
public class PublicController {
    private static final int PAGE_SIZE = 10;   // кружков на странице списка
    private static final int PANEL_SIZE = 6;   // изображений в боковой панели

    private final ClubRepository clubs;
    private final EventRepository events;
    private final AppUserRepository users;
    private final ClubRequestRepository requests;

    public PublicController(ClubRepository clubs, EventRepository events,
                            AppUserRepository users, ClubRequestRepository requests) {
        this.clubs = clubs;
        this.events = events;
        this.users = users;
        this.requests = requests;
    }

    @GetMapping("/")
    public String events(Model model) {
        LocalDate today = LocalDate.now();
        model.addAttribute("upcoming", events.findByEventDateGreaterThanEqualOrderByEventDateAscEventTimeAsc(today));
        model.addAttribute("past", events.findByEventDateLessThanOrderByEventDateDesc(today));
        return "events";
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

        // подана ли заявка текущим студентом
        boolean applied = false;
        if (principal != null) {
            AppUser u = users.findByUsername(principal.getName()).orElse(null);
            applied = u != null && u.getRole() == Role.STUDENT && requests.existsByClubIdAndStudentId(id, u.getId());
        }
        model.addAttribute("applied", applied);
        return "club";
    }
}
