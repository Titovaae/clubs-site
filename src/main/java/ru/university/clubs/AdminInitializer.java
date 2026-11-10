package ru.university.clubs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Создаёт первого администратора из application.properties, если его ещё нет. */
@Component
public class AdminInitializer implements CommandLineRunner {
    private final AppUserRepository users;
    private final PasswordEncoder encoder;
    private final String username;
    private final String password;

    public AdminInitializer(AppUserRepository users, PasswordEncoder encoder,
                            @Value("${app.admin.username}") String username,
                            @Value("${app.admin.password}") String password) {
        this.users = users;
        this.encoder = encoder;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run(String... args) {
        if (users.existsByUsername(username)) return;
        AppUser admin = new AppUser();
        admin.setUsername(username);
        admin.setPassword(encoder.encode(password));
        admin.setRole(Role.ADMIN);
        admin.setFullName("Администратор");
        users.save(admin);
    }
}
