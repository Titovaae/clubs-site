package ru.university.clubs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ClubsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClubsApplication.class, args);
    }
}
