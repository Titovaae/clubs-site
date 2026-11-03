package ru.university.clubs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Заполняет пустую базу тестовыми данными.
 * Кружки взяты со страницы https://www.csu.ru/structurecontacts/structure/sport/
 * Мероприятия придуманы для проверки сайта.
 * Отключить: app.seed-test-data=false в application.properties.
 */
@Component
public class TestDataLoader implements CommandLineRunner {
    private static final String CLUB_CONTACTS = "Спортивный клуб ЧелГУ: +7 (351) 799-70-51, marinasport2015@yandex.ru";
    private static final String GAMES_NOTE = " Сборные команды университета по спортивным играм тренируются в спортзале 3 учебного корпуса (пр. Победы, 162-в).";

    private static final String[] DAYS = {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};

    /** Тестовое расписание (придумано для проверки сайта): дни и время «начало-конец». */
    private static final Map<String, String> SCHEDULE = new LinkedHashMap<>();
    static {
        SCHEDULE.put("Лёгкая атлетика", "Пн Ср 17:00-18:30");
        SCHEDULE.put("Волейбол (мужская команда)", "Вт Чт 18:30-20:30");
        SCHEDULE.put("Волейбол (женская команда)", "Пн Ср 18:30-20:30");
        SCHEDULE.put("Баскетбол", "Вт Чт 16:30-18:30");
        SCHEDULE.put("Мини-футбол (мужская команда)", "Пн Ср 16:30-18:30");
        SCHEDULE.put("Мини-футбол (женская команда)", "Пт 17:00-19:00");
        SCHEDULE.put("Настольный теннис", "Вт Чт 15:30-17:30");
        SCHEDULE.put("Шахматы и шашки", "Ср 16:00-18:00");
        SCHEDULE.put("Атлетическая гимнастика", "Пн Ср Пт 17:00-18:30");
        SCHEDULE.put("Плавание", "Сб 10:00-11:30");
        SCHEDULE.put("Лыжные гонки", "Вт Чт 17:00-18:30");
        SCHEDULE.put("Армспорт", "Пн Ср 18:00-19:30");
        SCHEDULE.put("Прикладная самооборона", "Вт Чт 18:00-19:30");
        SCHEDULE.put("Рукопашный бой", "Пн Ср 18:00-19:30");
        SCHEDULE.put("Бадминтон", "Пт 16:00-18:00");
        SCHEDULE.put("Дартс", "Пн Ср 15:30-17:00");
        SCHEDULE.put("Пауэрлифтинг", "Пн Ср 16:00-17:30");
        SCHEDULE.put("Многоборье ГТО", "Сб 11:00-13:00");
    }

    private final ClubRepository clubs;
    private final EventRepository events;
    private final ClubService clubService;
    private final boolean enabled;

    public TestDataLoader(ClubRepository clubs, EventRepository events, ClubService clubService,
                          @Value("${app.seed-test-data:true}") boolean enabled) {
        this.clubService = clubService;
        this.clubs = clubs;
        this.events = events;
        this.enabled = enabled;
    }

    @Override
    public void run(String... args) {
        if (!enabled) return;
        if (clubs.count() == 0) {
            club("Лёгкая атлетика", "Секционные занятия по лёгкой атлетике. Один из самых востребованных у студентов видов спорта в Спортивном клубе ЧелГУ.", "Бардина Марина Юрьевна");
            club("Волейбол (мужская команда)", "Секционные занятия и тренировки мужской сборной ЧелГУ по волейболу." + GAMES_NOTE, "Макарьев Иван Валерьевич");
            club("Волейбол (женская команда)", "Секционные занятия и тренировки женской сборной ЧелГУ по волейболу." + GAMES_NOTE, "Попков Владимир Ильич");
            club("Баскетбол", "Секционные занятия и тренировки мужской и женской сборных ЧелГУ по баскетболу." + GAMES_NOTE, "Маркелов Иван Петрович");
            club("Мини-футбол (мужская команда)", "Секционные занятия и тренировки мужской сборной ЧелГУ по мини-футболу." + GAMES_NOTE, "Курдаков Сергей Алексеевич");
            club("Мини-футбол (женская команда)", "Секционные занятия и тренировки женской сборной ЧелГУ по мини-футболу." + GAMES_NOTE, "Байрамов Азат Тагирович");
            club("Настольный теннис", "Секционные занятия по настольному теннису. Сектор для игры оборудован в манеже 4 учебного корпуса (ул. Молодогвардейцев, 57а).", "Матина Залия Имамдиновна");
            club("Шахматы и шашки", "Занятия шахматного клуба ЧелГУ по шахматам и шашкам. Клуб находится в 1 учебном корпусе (ул. Братьев Кашириных, 129).", "Полосин Дмитрий Евгеньевич");
            club("Атлетическая гимнастика", "Занятия в тренажёрном зале 4 учебного корпуса (ул. Молодогвардейцев, 57а): силовая подготовка и атлетическая гимнастика.", "Хадиева Разиля Тагировна");
            club("Плавание", "Секционные занятия по плаванию в Спортивном клубе ЧелГУ.", "Хадиева Разиля Тагировна");
            club("Лыжные гонки", "Секционные занятия по лыжной подготовке. В 1 учебном корпусе есть лыжероллерная трасса протяжённостью 2115 м.", "Панькин Виктор Ефимович");
            club("Армспорт", "Секция армрестлинга «Урал» в 4 учебном корпусе. Занимаются в том числе студенты с инвалидностью.", "Алёшкин Вадим Альбертович");
            club("Прикладная самооборона", "Секционные занятия по прикладной самообороне в универсальном зале 2 учебного корпуса (ул. Молодогвардейцев, 70-б).", "Потехин Игорь Алексеевич");
            club("Рукопашный бой", "Секционные занятия по рукопашному бою в универсальном зале 2 учебного корпуса (ул. Молодогвардейцев, 70-б).", "Яремин Дмитрий");
            club("Бадминтон", "Секционные занятия по бадминтону в Спортивном клубе ЧелГУ.", "Кооль Мария Викторовна");
            club("Дартс", "Секционные занятия по дартсу в Спортивном клубе ЧелГУ.", "Матина Залия Имамдиновна");
            club("Пауэрлифтинг", "Секционные занятия по пауэрлифтингу в Спортивном клубе ЧелГУ.", "Макарьев Иван Валерьевич");
            club("Многоборье ГТО", "Подготовка к выполнению нормативов комплекса ГТО.", "Панькин Иван Валерьевич");
        }
        // Расписание добавляется кружкам с тестовыми названиями, у которых его ещё нет
        for (Club c : clubs.findAll()) {
            String spec = SCHEDULE.get(c.getName());
            if (spec != null && c.getSessions().isEmpty()) {
                clubService.setSchedule(c.getId(), parseSchedule(spec));
            }
        }
        if (events.count() == 0) {
            LocalDate today = LocalDate.now();
            event("Открытая тренировка по волейболу (тест)", "Пробное занятие для всех желающих. Можно прийти в спортивной форме и сменной обуви.", today.plusDays(3), LocalTime.of(18, 0), "Спортзал, 3 учебный корпус, пр. Победы, 162-в");
            event("Турнир по настольному теннису (тест)", "Внутривузовский турнир среди студентов. Регистрация перед началом игр.", today.plusDays(10), LocalTime.of(15, 30), "Манеж, 4 учебный корпус, ул. Молодогвардейцев, 57а");
            event("Шахматный турнир (тест)", "Турнир по быстрым шахматам среди студентов первого и второго курсов.", today.plusDays(17), LocalTime.of(14, 0), "1 учебный корпус, ул. Братьев Кашириных, 129");
            event("Кросс ЧелГУ (тест)", "Осенний кросс на лыжероллерной трассе. Итоги подведут в тот же день.", today.minusDays(14), LocalTime.of(12, 0), "1 учебный корпус, ул. Братьев Кашириных, 129");
        }
    }

    private static List<ClubSession> parseSchedule(String spec) {
        String[] parts = spec.split(" ");
        String[] time = parts[parts.length - 1].split("-");
        List<ClubSession> list = new ArrayList<>();
        for (int i = 0; i < parts.length - 1; i++) {
            ClubSession s = new ClubSession();
            s.setDayOfWeek(DayOfWeek.of(Arrays.asList(DAYS).indexOf(parts[i]) + 1));
            s.setStartTime(LocalTime.parse(time[0]));
            s.setEndTime(LocalTime.parse(time[1]));
            list.add(s);
        }
        return list;
    }

    private void club(String name, String description, String organizer) {
        Club c = new Club();
        c.setName(name);
        c.setDescription(description);
        c.setOrganizer(organizer);
        c.setContacts(CLUB_CONTACTS);
        c.setPlace(placeFor(name));
        clubs.save(c);
    }

    private static String placeFor(String name) {
        if (name.startsWith("Волейбол") || name.startsWith("Баскетбол") || name.startsWith("Мини-футбол"))
            return "Спортзал, 3 учебный корпус, пр. Победы, 162-в";
        if (name.startsWith("Настольный теннис") || name.startsWith("Армспорт"))
            return "4 учебный корпус, ул. Молодогвардейцев, 57а";
        if (name.startsWith("Атлетическая"))
            return "Тренажёрный зал, 4 учебный корпус, ул. Молодогвардейцев, 57а";
        if (name.startsWith("Шахматы") || name.startsWith("Лыжные"))
            return "1 учебный корпус, ул. Братьев Кашириных, 129";
        if (name.startsWith("Прикладная") || name.startsWith("Рукопашный"))
            return "Универсальный зал, 2 учебный корпус, ул. Молодогвардейцев, 70-б";
        return null;
    }

    private void event(String name, String description, LocalDate date, LocalTime time, String place) {
        Event e = new Event();
        e.setName(name);
        e.setDescription(description);
        e.setEventDate(date);
        e.setEventTime(time);
        e.setPlace(place);
        e.setContacts(CLUB_CONTACTS);
        events.save(e);
    }
}
