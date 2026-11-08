package ru.university.clubs;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** Администратор: создание и просмотр пользователей. */
@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final AppUserRepository users;

    public AdminController(UserService userService, AppUserRepository users) {
        this.userService = userService;
        this.users = users;
    }

    @GetMapping
    public String root() {
        return "redirect:/admin/users";
    }

    @GetMapping("/users")
    public String list(Model model) {
        model.addAttribute("users", users.findAllByOrderByFullNameAsc());
        return "admin/users";
    }

    @GetMapping("/users/new")
    public String form(Model model) {
        model.addAttribute("userForm", new UserForm());
        model.addAttribute("roles", Role.values());
        return "admin/user-form";
    }

    @PostMapping("/users")
    public String create(@Valid @ModelAttribute("userForm") UserForm form, BindingResult result, Model model) {
        userService.validate(form, result);
        if (result.hasErrors()) {
            model.addAttribute("roles", Role.values());
            return "admin/user-form";
        }
        userService.create(form);
        return "redirect:/admin/users";
    }
}
