package ru.university.clubs;

import jakarta.validation.constraints.*;

/** Форма регистрации студента и создания пользователя администратором. */
public class UserForm {
    @NotBlank(message = "Укажите логин")
    @Size(min = 3, max = 50, message = "От 3 до 50 символов")
    @Pattern(regexp = "[A-Za-z0-9._-]*", message = "Только латинские буквы, цифры и символы . _ -")
    private String username;

    @NotBlank(message = "Укажите пароль")
    @Size(min = 6, max = 100, message = "От 6 до 100 символов")
    private String password;

    @NotBlank(message = "Укажите ФИО")
    @Size(max = 150, message = "Не более 150 символов")
    private String fullName;

    @Size(max = 50, message = "Не более 50 символов")
    private String groupName;

    @Min(value = 1, message = "Курс от 1 до 6")
    @Max(value = 6, message = "Курс от 1 до 6")
    private Integer course;

    @Size(max = 255, message = "Не более 255 символов")
    private String contacts;

    private Role role = Role.STUDENT;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public Integer getCourse() { return course; }
    public void setCourse(Integer course) { this.course = course; }
    public String getContacts() { return contacts; }
    public void setContacts(String contacts) { this.contacts = contacts; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
