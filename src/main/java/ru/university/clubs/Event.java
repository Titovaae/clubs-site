package ru.university.clubs;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Укажите название")
    @Size(max = 150, message = "Не более 150 символов")
    private String name;

    @NotBlank(message = "Укажите описание")
    @Size(max = 2000, message = "Не более 2000 символов")
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Укажите дату")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate eventDate;

    @NotNull(message = "Укажите время")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime eventTime;

    @Size(max = 2000, message = "Не более 2000 символов")
    @Column(length = 2000)
    private String requirements;

    @NotBlank(message = "Укажите место")
    @Size(max = 255, message = "Не более 255 символов")
    private String place;

    @Size(max = 255, message = "Не более 255 символов")
    private String contacts;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "owner_id")
    private AppUser owner;

    /** Кружок, которому принадлежит мероприятие (может быть пустым). */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "club_id")
    private Club club;

    public Club getClub() { return club; }
    public void setClub(Club club) { this.club = club; }

    public AppUser getOwner() { return owner; }
    public void setOwner(AppUser owner) { this.owner = owner; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }
    public LocalTime getEventTime() { return eventTime; }
    public void setEventTime(LocalTime eventTime) { this.eventTime = eventTime; }
    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
    public String getPlace() { return place; }
    public void setPlace(String place) { this.place = place; }
    public String getContacts() { return contacts; }
    public void setContacts(String contacts) { this.contacts = contacts; }
}
