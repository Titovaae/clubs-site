package ru.university.clubs;

public class UserForm {
    private String username;
    private String password;
    private Role role;
    private String fullName;
    private String contacts;

    // Новые поля для студента
    private String groupName;
    private Integer course;

    // Геттеры и сеттеры
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getContacts() { return contacts; }
    public void setContacts(String contacts) { this.contacts = contacts; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public Integer getCourse() { return course; }
    public void setCourse(Integer course) { this.course = course; }
}