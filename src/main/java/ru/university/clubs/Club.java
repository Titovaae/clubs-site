package ru.university.clubs;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
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
    private String name;

    @NotBlank(message = "Укажите описание")
    @Size(max = 2000, message = "Не более 2000 символов")
    @Column(columnDefinition = "TEXT")
    private String description;

    @Size(max = 2000, message = "Не более 2000 символов")
    @Column(length = 2000)
    private String requirements;

    @Size(max = 255, message = "Не более 255 символов")
    private String place;

    /** Организатор: его ФИО и контакты показываются в карточке кружка. */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "owner_id")
    private AppUser owner;

    /** Дата и время последнего успешного редактирования. */
    private LocalDateTime updatedAt;

    @Size(max = 1000, message = "Не более 1000 символов")
    @Column(length = 1000)
    private String autoReply;

    /** true — автоответ отправляется сразу при поступлении заявки. */
    private boolean autoReplySend;

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
    public AppUser getOwner() { return owner; }
    public void setOwner(AppUser owner) { this.owner = owner; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getAutoReply() { return autoReply; }
    public void setAutoReply(String autoReply) { this.autoReply = autoReply; }
    public boolean isAutoReplySend() { return autoReplySend; }
    public void setAutoReplySend(boolean autoReplySend) { this.autoReplySend = autoReplySend; }
}
