package ru.university.clubs;

import java.time.DayOfWeek;

/** Одна строка формы расписания: день недели и время начала/окончания (пусто = тренировки нет). */
public class ScheduleRow {
    private final DayOfWeek day;
    private final String start;
    private final String end;

    public ScheduleRow(DayOfWeek day, String start, String end) {
        this.day = day;
        this.start = start;
        this.end = end;
    }

    public String getDayTitle() { return ClubSession.dayTitle(day); }
    public String getStart() { return start; }
    public String getEnd() { return end; }
}
