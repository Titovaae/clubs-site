package ru.university.clubs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Тестовые данные. Кружки, тренеры, контакты и места взяты со страницы Спортивного клуба
 * на сайте ЧелГУ (csu.ru). Расписания, мероприятия и учётные записи придуманы для проверки сайта.
 * Тренеры создаются как организаторы (пароль organizer123), плюс тестовый студент student / student123.
 * Отключается настройкой app.seed-test-data=false.
 */
@Component
public class TestDataLoader implements CommandLineRunner {
    private static final String CONTACTS = "+7 (351) 799-70-51, marinasport2015@yandex.ru";
    private static final String BASE = "Занятия проходят в форме секционных занятий Спортивного клуба ЧелГУ. "
        + "Студентам, проявившим способности в спорте, предоставляется возможность стать членами сборной команды "
        + "университета и выступать на соревнованиях.";

    private static final String PLACE_1 = "1 учебный корпус, ул. Братьев Кашириных, 129";
    private static final String PLACE_2 = "Универсальный спортзал, 2 учебный корпус, ул. Молодогвардейцев, 70-б";
    private static final String PLACE_3 = "Спортзал, 3 учебный корпус, пр. Победы, 162-в";
    private static final String PLACE_4 = "4 учебный корпус, ул. Молодогвардейцев, 57а";

    private static final String[] DAYS = {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};

    /** ФИО тренера -> логин организатора. */
    private static final Map<String, String> TRAINERS = new LinkedHashMap<>();
    static {
        TRAINERS.put("Потехин Игорь Алексеевич", "potehin");
        TRAINERS.put("Матина Залия Имамдиновна", "matina");
        TRAINERS.put("Полосин Дмитрий Евгеньевич", "polosin");
        TRAINERS.put("Бардина Марина Юрьевна", "bardina");
        TRAINERS.put("Хадиева Разиля Тагировна", "hadieva");
        TRAINERS.put("Алёшкин Вадим Альбертович", "aleshkin");
        TRAINERS.put("Макарьев Иван Валерьевич", "makarev");
        TRAINERS.put("Попков Владимир Ильич", "popkov");
        TRAINERS.put("Маркелов Иван Петрович", "markelov");
        TRAINERS.put("Панькин Виктор Ефимович", "pankin_v");
        TRAINERS.put("Курдаков Сергей Алексеевич", "kurdakov");
        TRAINERS.put("Кооль Мария Викторовна", "kool");
        TRAINERS.put("Байрамов Азат Тагирович", "bayramov");
        TRAINERS.put("Яремин Дмитрий", "yaremin");
        TRAINERS.put("Панькин Иван Валерьевич", "pankin_i");
    }

    /** Тестовое расписание: дни и время «начало-конец». */
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
    private final AppUserRepository users;
    private final ClubService clubService;
    private final EventService eventService;
    private final ClubFollowRepository follows;
    private final PasswordEncoder encoder;
    private final boolean enabled;

    public TestDataLoader(ClubRepository clubs, EventRepository events, AppUserRepository users,
                          ClubService clubService, EventService eventService, ClubFollowRepository follows,
                          PasswordEncoder encoder,
                          @Value("${app.seed-test-data:true}") boolean enabled) {
        this.eventService = eventService;
        this.follows = follows;
        this.clubs = clubs;
        this.events = events;
        this.users = users;
        this.clubService = clubService;
        this.encoder = encoder;
        this.enabled = enabled;
    }

    @Override
    public void run(String... args) {
        if (!enabled) return;
        createTestStudent();
        if (clubs.count() == 0) {
            seedClubs();
        }
        // Расписание добавляется кружкам с тестовыми названиями, у которых его ещё нет
        for (Club c : clubs.findAll()) {
            String spec = SCHEDULE.get(c.getName());
            if (spec != null && c.getSessions().isEmpty()) {
                clubService.setSchedule(c.getId(), parseSchedule(spec));
            }
        }
        if (events.count() == 0) {
            followTestClubs();
            seedEvents();
        }
    }

    private void createTestStudent() {
        if (users.existsByUsername("student")) return;
        AppUser s = new AppUser();
        s.setUsername("student");
        s.setPassword(encoder.encode("student123"));
        s.setRole(Role.STUDENT);
        s.setFullName("Иванов Иван Иванович");
        s.setGroupName("ПМИ-101");
        s.setCourse(1);
        users.save(s);
    }

    private AppUser organizer(String fullName) {
        String username = TRAINERS.get(fullName);
        return users.findByUsername(username).orElseGet(() -> {
            AppUser o = new AppUser();
            o.setUsername(username);
            o.setPassword(encoder.encode("organizer123"));
            o.setRole(Role.ORGANIZER);
            o.setFullName(fullName);
            o.setContacts(CONTACTS);
            return users.save(o);
        });
    }

    private void seedClubs() {
        club("Прикладная самооборона", "Потехин Игорь Алексеевич",
            "Курс прикладной самообороны: базовые приёмы защиты, безопасное поведение и физическая подготовка.", PLACE_2);
        club("Настольный теннис", "Матина Залия Имамдиновна",
            "Секция настольного тенниса для студентов любого уровня подготовки: от первых ударов до участия в турнирах.", PLACE_4);
        club("Шахматы и шашки", "Полосин Дмитрий Евгеньевич",
            "Занятия шахматами и шашками в шахматном клубе университета: разбор партий, тренировочные игры и подготовка к турнирам.", PLACE_1 + " (шахматный клуб)");
        club("Лёгкая атлетика", "Бардина Марина Юрьевна",
            "Бег, прыжки и метания: общая физическая подготовка и подготовка к соревнованиям по лёгкой атлетике.", null);
        club("Атлетическая гимнастика", "Хадиева Разиля Тагировна",
            "Силовая и общая физическая подготовка в тренажёрном зале университета.", "Тренажёрный зал, " + PLACE_4);
        club("Армспорт", "Алёшкин Вадим Альбертович",
            "Секция армрестлинга «Урал». Занимаются в том числе студенты с инвалидностью, среди воспитанников есть призёры российских и международных соревнований.", "Тренажёрный зал, " + PLACE_4);
        club("Волейбол (мужская команда)", "Макарьев Иван Валерьевич",
            "Тренировки мужской сборной команды университета по волейболу.", PLACE_3);
        club("Волейбол (женская команда)", "Попков Владимир Ильич",
            "Тренировки женской сборной команды университета по волейболу.", PLACE_3);
        club("Баскетбол", "Маркелов Иван Петрович",
            "Тренировки мужской и женской сборных команд университета по баскетболу.", PLACE_3);
        club("Лыжные гонки", "Панькин Виктор Ефимович",
            "Лыжная подготовка: зимой на лыжне, в тёплое время года на лыжероллерной трассе протяжённостью 2115 м.", "Лыжероллерная трасса, " + PLACE_1);
        club("Плавание", "Хадиева Разиля Тагировна",
            "Секция плавания для студентов: обучение и совершенствование техники.", null);
        club("Мини-футбол (мужская команда)", "Курдаков Сергей Алексеевич",
            "Тренировки мужской сборной команды университета по мини-футболу.", PLACE_3);
        club("Бадминтон", "Кооль Мария Викторовна",
            "Секция бадминтона для начинающих и опытных игроков.", PLACE_2);
        club("Мини-футбол (женская команда)", "Байрамов Азат Тагирович",
            "Тренировки женской сборной команды университета по мини-футболу.", PLACE_3);
        club("Рукопашный бой", "Яремин Дмитрий",
            "Секция рукопашного боя: техника ударов и захватов, физическая подготовка.", PLACE_2);
        club("Пауэрлифтинг", "Макарьев Иван Валерьевич",
            "Силовое троеборье: приседания, жим лёжа и становая тяга под руководством тренера.", "Тяжелоатлетический зал, " + PLACE_4);
        club("Многоборье ГТО", "Панькин Иван Валерьевич",
            "Подготовка к выполнению нормативов комплекса ГТО.", null);
        club("Дартс", "Матина Залия Имамдиновна",
            "Секция дартса: техника броска и тренировочные матчи.", null);
    }

    private void club(String name, String trainer, String about, String place) {
        Club c = new Club();
        c.setName(name);
        c.setOwner(organizer(trainer));
        c.setDescription(about + " " + BASE);
        c.setPlace(place);
        c.setUpdatedAt(LocalDateTime.now());
        // ВРЕМЕННО ОТКЛЮЧЕНО: тестовый автоответ
        // c.setAutoReply("Спасибо за заявку в кружок «" + name + "»! Приходите на ближайшую тренировку по расписанию, "
        //     + "при себе иметь спортивную форму и сменную обувь.");
        // c.setAutoReplySend(name.equals("Лёгкая атлетика"));
        clubs.save(c);
    }

    /** Тестовый студент следит за двумя кружками, чтобы сразу увидеть уведомления. */
    private void followTestClubs() {
        AppUser student = users.findByUsername("student").orElse(null);
        if (student == null) return;
        for (String name : new String[]{"Волейбол (мужская команда)", "Настольный теннис"}) {
            Club club = findClub(name);
            if (club != null && !follows.existsByClubIdAndStudentId(club.getId(), student.getId())) {
                ClubFollow f = new ClubFollow();
                f.setClub(club);
                f.setStudent(student);
                f.setCreatedAt(LocalDateTime.now());
                follows.save(f);
            }
        }
    }

    private Club findClub(String name) {
        for (Club c : clubs.findAll()) {
            if (c.getName().equals(name)) return c;
        }
        return null;
    }

    private void seedEvents() {
        LocalDate today = LocalDate.now();
        event("Волейбол (мужская команда)", "Открытая тренировка по волейболу (тест)",
            "Приглашаем всех желающих попробовать себя в волейболе. Тренировку проведёт тренер сборной.",
            today.plusDays(3), LocalTime.of(18, 30), PLACE_3);
        event("Настольный теннис", "Турнир по настольному теннису (тест)",
            "Внутривузовский турнир среди студентов. Игры проходят по олимпийской системе.",
            today.plusDays(10), LocalTime.of(15, 30), PLACE_4);
        event("Шахматы и шашки", "Шахматный турнир среди студентов (тест)",
            "Турнир в шахматном клубе университета, контроль времени — 15 минут на партию.",
            today.plusDays(17), LocalTime.of(16, 0), PLACE_1 + " (шахматный клуб)");
        event("Лыжные гонки", "День здоровья на лыжероллерной трассе (тест)",
            "Совместная тренировка и забеги для всех желающих.",
            today.minusDays(14), LocalTime.of(12, 0), "Лыжероллерная трасса, " + PLACE_1);
    }

    private void event(String clubName, String name, String about, LocalDate date, LocalTime time, String place) {
        Club club = findClub(clubName);
        Event e = new Event();
        e.setClub(club);
        e.setOwner(club != null ? club.getOwner() : organizer("Бардина Марина Юрьевна"));
        e.setName(name);
        e.setDescription(about);
        e.setEventDate(date);
        e.setEventTime(time);
        e.setRequirements("Спортивная форма и сменная обувь.");
        e.setPlace(place);
        e.setContacts(CONTACTS);
        events.save(e);
        eventService.notifyFollowers(e);   // подписанные студенты получают уведомление
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
}
