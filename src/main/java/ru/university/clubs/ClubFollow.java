package ru.university.clubs;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/** Подписка студента на кружок: студент получает уведомления о новых мероприятиях кружка. */
@Entity
@Table(name = "club_follows", uniqueConstraints = @UniqueConstraint(columnNames = {"club_id", "student_id"}))
public class ClubFollow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "club_id")
    private Club club;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "student_id")
    private AppUser student;

    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public Club getClub() { return club; }
    public void setClub(Club club) { this.club = club; }
    public AppUser getStudent() { return student; }
    public void setStudent(AppUser student) { this.student = student; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
