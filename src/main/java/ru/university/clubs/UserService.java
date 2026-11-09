package ru.university.clubs;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import java.util.List;

@Service
public class UserService {
    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(AppUserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<AppUser> findAll() {
        return repository.findAll();
    }

    public void validate(UserForm form, BindingResult result) {
        if (form.getUsername() == null || form.getUsername().trim().isEmpty()) {
            result.rejectValue("username", "error.username", "Логин не может быть пустым");
        } else if (repository.findByUsername(form.getUsername()).isPresent()) {
            result.rejectValue("username", "error.username", "Пользователь с таким логином уже существует");
        }

        if (form.getPassword() == null || form.getPassword().trim().isEmpty()) {
            result.rejectValue("password", "error.password", "Пароль не может быть пустым");
        } else if (form.getPassword().length() < 5) {
            result.rejectValue("password", "error.password", "Пароль должен быть не менее 5 символов");
        }

        if (form.getFullName() == null || form.getFullName().trim().isEmpty()) {
            result.rejectValue("fullName", "error.fullName", "ФИО не может быть пустым");
        }

        // Валидация для студентов
        if (form.getGroupName() == null || form.getGroupName().trim().isEmpty()) {
            result.rejectValue("groupName", "error.groupName", "Укажите вашу группу");
        }
        if (form.getCourse() == null || form.getCourse() < 1) {
            result.rejectValue("course", "error.course", "Укажите ваш курс (от 1 до 6)");
        }
    }

    // Создание пользователя Админом
    public void create(UserForm form) {
        AppUser user = new AppUser();
        user.setUsername(form.getUsername());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setRole(form.getRole());
        user.setFullName(form.getFullName());
        user.setContacts(form.getContacts());
        user.setGroupName(form.getGroupName());
        user.setCourse(form.getCourse());
        repository.save(user);
    }

    // Регистрация студента
    public void registerStudent(UserForm form) {
        AppUser user = new AppUser();
        user.setUsername(form.getUsername());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setFullName(form.getFullName());
        user.setRole(Role.STUDENT);

        // Сохраняем новые поля
        user.setGroupName(form.getGroupName());
        user.setCourse(form.getCourse());

        repository.save(user);
    }
}