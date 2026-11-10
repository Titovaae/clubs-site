package ru.university.clubs;

import java.time.LocalDate;

/** Ячейка календаря. date == null — пустая ячейка вне месяца. */
public class CalendarDay {
    private final LocalDate date;
    private final boolean marked;
    private final boolean today;

    public CalendarDay(LocalDate date, boolean marked, boolean today) {
        this.date = date;
        this.marked = marked;
        this.today = today;
    }

    public LocalDate getDate() { return date; }
    public boolean isMarked() { return marked; }
    public boolean isToday() { return today; }
    public int getNumber() { return date == null ? 0 : date.getDayOfMonth(); }
    public String getIso() { return date == null ? "" : date.toString(); }
}
