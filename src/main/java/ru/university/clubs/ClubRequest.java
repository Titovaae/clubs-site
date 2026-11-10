package ru.university.clubs;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/** Заявка студента на запись в кружок. */
@Entity
@Table(name = "club_requests")
public class ClubRequest {
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

    @Column(columnDefinition = "TEXT")
    private String replyText;

    private LocalDateTime repliedAt;

    public boolean isAnswered() { return repliedAt != null; }

    public Long getId() { return id; }
    public Club getClub() { return club; }
    public void setClub(Club club) { this.club = club; }
    public AppUser getStudent() { return student; }
    public void setStudent(AppUser student) { this.student = student; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getReplyText() { return replyText; }
    public void setReplyText(String replyText) { this.replyText = replyText; }
    public LocalDateTime getRepliedAt() { return repliedAt; }
    public void setRepliedAt(LocalDateTime repliedAt) { this.repliedAt = repliedAt; }
}
