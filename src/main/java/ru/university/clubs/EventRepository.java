package ru.university.clubs;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByEventDateGreaterThanEqualOrderByEventDateAscEventTimeAsc(LocalDate date);

    List<Event> findByEventDateLessThanOrderByEventDateDesc(LocalDate date);

    List<Event> findAllByOrderByEventDateDesc();
}
