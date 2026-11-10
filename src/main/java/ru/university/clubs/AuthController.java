package ru.university.clubs;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
    private final UserService users;

    public AuthController(UserService users) {
        this.users = users;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("userForm", new UserForm());
        return "register";
    }

    /** Самостоятельно регистрируются только студенты. */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("userForm") UserForm form, BindingResult result) {
        form.setRole(Role.STUDENT);
        users.validate(form, result);
        if (result.hasErrors()) {
            return "register";
        }
        users.create(form);
        return "redirect:/login?registered";
    }
}
