package ru.university.clubs;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByOrderByEventDateAscEventTimeAscIdAsc();

    List<Event> findByEventDateBetween(LocalDate from, LocalDate to);

    List<Event> findByEventDateOrderByEventTimeAsc(LocalDate date);

    List<Event> findByOwnerIdOrderByEventDateDesc(Long ownerId);

    List<Event> findByClubId(Long clubId);
}
