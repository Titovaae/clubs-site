package ru.university.clubs;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clubs")
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Укажите название")
    @Size(max = 150, message = "Не более 150 символов")
    @Column(length = 150)
    private String name;

    @NotBlank(message = "Укажите описание")
    @Size(max = 2000, message = "Не более 2000 символов")
    @Column(length = 2000)
    private String description;

    @Size(max = 2000, message = "Не более 2000 символов")
    @Column(length = 2000)
    private String requirements;

    @Size(max = 255, message = "Не более 255 символов")
    private String place;

    @NotBlank(message = "Укажите организатора")
    @Size(max = 150, message = "Не более 150 символов")
    @Column(length = 150)
    private String organizer;

    @NotBlank(message = "Укажите контакты")
    @Size(max = 255, message = "Не более 255 символов")
    private String contacts;

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("dayOfWeek ASC, startTime ASC")
    private List<ClubSession> sessions = new ArrayList<>();

    public List<ClubSession> getSessions() { return sessions; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
    public String getPlace() { return place; }
    public void setPlace(String place) { this.place = place; }
    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }
    public String getContacts() { return contacts; }
    public void setContacts(String contacts) { this.contacts = contacts; }
}
