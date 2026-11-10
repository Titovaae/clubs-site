package ru.university.clubs;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.security.Principal;

@Service
public class UserService implements UserDetailsService {
    private final AppUserRepository users;
    private final PasswordEncoder encoder;

    public UserService(AppUserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser u = users.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        return User.withUsername(u.getUsername()).password(u.getPassword()).roles(u.getRole().name()).build();
    }

    public AppUser current(Principal principal) {
        return users.findByUsername(principal.getName()).orElseThrow();
    }

    /** Проверки, зависящие от роли, и уникальность логина. */
    public void validate(UserForm f, BindingResult result) {
        if (f.getUsername() != null && users.existsByUsername(f.getUsername())) {
            result.rejectValue("username", "duplicate", "Такой логин уже занят");
        }
        if (f.getRole() == Role.STUDENT) {
            if (f.getGroupName() == null || f.getGroupName().isBlank()) {
                result.rejectValue("groupName", "required", "Укажите группу");
            }
            if (f.getCourse() == null) {
                result.rejectValue("course", "required", "Укажите курс");
            }
        }
        if (f.getRole() == Role.ORGANIZER && (f.getContacts() == null || f.getContacts().isBlank())) {
            result.rejectValue("contacts", "required", "Укажите контакты организатора");
        }
    }

    @Transactional
    public AppUser create(UserForm f) {
        AppUser u = new AppUser();
        u.setUsername(f.getUsername());
        u.setPassword(encoder.encode(f.getPassword()));
        u.setRole(f.getRole());
        u.setFullName(f.getFullName());
        u.setGroupName(f.getRole() == Role.STUDENT ? f.getGroupName() : null);
        u.setCourse(f.getRole() == Role.STUDENT ? f.getCourse() : null);
        u.setContacts(f.getRole() == Role.ORGANIZER ? f.getContacts() : null);
        return users.save(u);
    }
}
