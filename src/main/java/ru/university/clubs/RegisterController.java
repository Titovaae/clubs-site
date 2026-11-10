package ru.university.clubs;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    // Открываем страницу регистрации
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("userForm", new UserForm());
        return "register"; // Имя HTML файла
    }

    // Обрабатываем данные формы
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userForm") UserForm form, BindingResult result) {
        userService.validate(form, result);

        if (result.hasErrors()) {
            return "register"; // Если есть ошибки, возвращаем на ту же страницу
        }

        userService.registerStudent(form); // Сохраняем студента
        // Перенаправляем на страницу входа с параметром ?registered
        return "redirect:/login?registered";
    }
}