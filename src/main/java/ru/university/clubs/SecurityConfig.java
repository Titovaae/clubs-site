package ru.university.clubs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(a -> a
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/organizer/**").hasRole("ORGANIZER")
                .requestMatchers("/student/**").hasRole("STUDENT")
                .anyRequest().permitAll())
            .formLogin(f -> f
                .loginPage("/login")
                .successHandler((request, response, auth) -> {
                    String target = "/";
                    for (var authority : auth.getAuthorities()) {
                        if (authority.getAuthority().equals("ROLE_ADMIN")) target = "/admin/users";
                        if (authority.getAuthority().equals("ROLE_ORGANIZER")) target = "/organizer";
                        if (authority.getAuthority().equals("ROLE_STUDENT")) target = "/clubs";
                    }
                    response.sendRedirect(request.getContextPath() + target);
                })
                .permitAll())
            .logout(l -> l.logoutSuccessUrl("/"));
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
