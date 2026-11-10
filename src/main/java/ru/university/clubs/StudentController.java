package ru.university.clubs;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/student")
public class StudentController {
    private final UserService users;
    private final FollowService followService;
    private final ClubFollowRepository follows;
    private final NotificationRepository notifications;

    public StudentController(UserService users, FollowService followService,
                             ClubFollowRepository follows, NotificationRepository notifications) {
        this.users = users;
        this.followService = followService;
        this.follows = follows;
        this.notifications = notifications;
    }

    @GetMapping("/notifications")
    public String notifications(Principal principal, Model model) {
        AppUser me = users.current(principal);
        LocalDateTime since = LocalDateTime.now().minusDays(Notification.KEEP_DAYS);
        model.addAttribute("notifications",
            notifications.findByUserIdAndCreatedAtAfterOrderByCreatedAtDescIdDesc(me.getId(), since));
        model.addAttribute("followed", follows.findByStudentIdOrderByClubNameAsc(me.getId()));
        return "student/notifications";
    }

    @PostMapping("/clubs/{id}/follow")
    public String follow(@PathVariable Long id, Principal principal) {
        followService.follow(id, users.current(principal));
        return "redirect:/clubs/" + id;
    }

    @PostMapping("/clubs/{id}/unfollow")
    public String unfollow(@PathVariable Long id, Principal principal) {
        followService.unfollow(id, users.current(principal));
        return "redirect:/clubs/" + id;
    }
}
