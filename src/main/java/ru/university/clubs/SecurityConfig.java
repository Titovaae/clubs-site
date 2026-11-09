package ru.university.clubs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // Добавили AuthenticationProvider в параметры
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, AuthenticationProvider authProvider) throws Exception {
        http
                .authenticationProvider(authProvider) // <--- ВОТ ЭТА СТРОКА ВСЁ ИСПРАВЛЯЕТ
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

    // Этот бин связывает нашу базу данных и шифратор паролей
    @Bean
    public AuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}