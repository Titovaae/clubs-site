package ru.university.clubs;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    // Этот метод просто открывает HTML-файл login.html
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}