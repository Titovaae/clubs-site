package ru.university.clubs;

public enum Role {
    STUDENT("Студент"),
    ORGANIZER("Организатор"),
    ADMIN("Администратор");

    private final String title;

    Role(String title) { this.title = title; }

    public String getTitle() { return title; }
}
