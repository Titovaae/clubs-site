package ru.university.clubs;

import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

@Entity
@Table(name = "club_sessions")
public class ClubSession {
    private static final DateTimeFormatter HM = DateTimeFormatter.ofPattern("HH:mm");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "club_id")
    private Club club;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    public static String dayTitle(DayOfWeek d) {
        String s = d.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("ru"));
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public String getDayTitle() { return dayTitle(dayOfWeek); }
    public String getTimeRange() { return startTime.format(HM) + "–" + endTime.format(HM); }

    public Long getId() { return id; }
    public Club getClub() { return club; }
    public void setClub(Club club) { this.club = club; }
    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
}
