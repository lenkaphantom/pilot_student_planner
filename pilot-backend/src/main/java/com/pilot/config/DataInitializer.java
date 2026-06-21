package com.pilot.config;

import com.pilot.enums.DailyStudyHours;
import com.pilot.enums.InternshipUrgency;
import com.pilot.enums.PreferredTime;
import com.pilot.enums.ReminderDaysAhead;
import com.pilot.model.CalendarEvent;
import com.pilot.model.StudentInterest;
import com.pilot.model.StudentProfile;
import com.pilot.model.StudentSubject;
import com.pilot.model.User;
import com.pilot.repository.CalendarEventRepository;
import com.pilot.repository.StudentProfileRepository;
import com.pilot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Initializer komponenta - automatski puni bazu sa test podacima
 * pri pokretanju aplikacije (ako je baza prazna).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final CalendarEventRepository calendarEventRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Pokreće inicijalizaciju podataka kada je aplikacija spremna.
     */
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeData() {
        log.info("🚀 Počinjem inicijalizaciju test podataka...");

        // Ako već postoje korisnici, ne inicijalizuj ponovo
        if (userRepository.count() > 0) {
            log.info("✅ Baza sadrži podatke - preskačem inicijalizaciju");
            return;
        }

        try {
            // Kreiraj test korisnike
            List<User> users = createTestUsers();
            log.info("✅ Kreirano {} test korisnika", users.size());

            // Kreiraj profile za korisnike
            List<StudentProfile> profiles = createStudentProfiles(users);
            log.info("✅ Kreirano {} student profila", profiles.size());

            // Kreiraj kalendarase
            createCalendarEvents(users);
            log.info("✅ Kreirani kalendarski događaji");

            log.info("✅ Inicijalizacija test podataka uspešna!");
        } catch (Exception e) {
            log.error("❌ Greška pri inicijalizaciji podataka", e);
        }
    }

    /**
     * Kreira test korisnike sa različitim email adresama i lozinkama.
     */
    private List<User> createTestUsers() {
        List<User> users = new ArrayList<>();

        // Korisnik 1: Ana Marković
        User user1 = User.builder()
                .email("ana.markovic@example.com")
                .password(passwordEncoder.encode("Lozinka123!"))
                .fullName("Ana Marković")
                .role(User.Role.STUDENT)
                .enabled(true)
                .build();
        users.add(userRepository.save(user1));

        // Korisnik 2: Marko Jovanović
        User user2 = User.builder()
                .email("marko.jovanovic@example.com")
                .password(passwordEncoder.encode("Lozinka456!"))
                .fullName("Marko Jovanović")
                .role(User.Role.STUDENT)
                .enabled(true)
                .build();
        users.add(userRepository.save(user2));

        // Korisnik 3: Jelena Nikolić
        User user3 = User.builder()
                .email("jelena.nikolic@example.com")
                .password(passwordEncoder.encode("Lozinka789!"))
                .fullName("Jelena Nikolić")
                .role(User.Role.STUDENT)
                .enabled(true)
                .build();
        users.add(userRepository.save(user3));

        // Korisnik 4: Petar Stevic
        User user4 = User.builder()
                .email("petar.stevic@example.com")
                .password(passwordEncoder.encode("Lozinka000!"))
                .fullName("Petar Stević")
                .role(User.Role.STUDENT)
                .enabled(true)
                .build();
        users.add(userRepository.save(user4));

        return users;
    }

    /**
     * Kreira student profile za korisnike sa svim relevantnim informacijama.
     */
    private List<StudentProfile> createStudentProfiles(List<User> users) {
        List<StudentProfile> profiles = new ArrayList<>();

        // Profil 1: Ana - FTN, Informatika, 2. godina
        StudentProfile profile1 = StudentProfile.builder()
                .user(users.get(0))
                .faculty("Fakultet Tehnijskih Nauka")
                .studyProgram("Informatika")
                .yearOfStudy((short) 2)
                .dailyStudyHours(DailyStudyHours.TWO_TO_FOUR)
                .preferredTime(PreferredTime.AFTERNOON)
                .reminderDaysAhead(ReminderDaysAhead.THREE_DAYS)
                .wantsInternship(true)
                .internshipUrgency(InternshipUrgency.ACTIVE)
                .wantsScholarships(true)
                .wantsExchange(false)
                .wantsCompetitions(true)
                .wantsVolunteering(true)
                .wantsStudentOrgs(true)
                .profileComplete(true)
                .build();
        profile1 = studentProfileRepository.save(profile1);
        profile1.setInterests(createInterests(profile1,
                "Veštačka inteligencija",
                "Web razvoj",
                "Machine learning"
        ));
        profile1.setSubjects(createSubjects(profile1,
                "Programiranje II -> Sve lekcije do kraja semestra",
                "Baze podataka -> Normalizacija i upiti",
                "Algoritmi -> Sortiranje i pretraga"
        ));
        profile1 = studentProfileRepository.save(profile1);
        profiles.add(profile1);

        // Profil 2: Marko - Filozofski fakultet, Filologija, 3. godina
        StudentProfile profile2 = StudentProfile.builder()
                .user(users.get(1))
                .faculty("Filozofski fakultet")
                .studyProgram("Engleska filologija")
                .yearOfStudy((short) 3)
                .dailyStudyHours(DailyStudyHours.ONE_TO_TWO)
                .preferredTime(PreferredTime.MORNING)
                .reminderDaysAhead(ReminderDaysAhead.SEVEN_DAYS)
                .wantsInternship(true)
                .internshipUrgency(InternshipUrgency.MAYBE)
                .wantsScholarships(false)
                .wantsExchange(true)
                .wantsCompetitions(false)
                .wantsVolunteering(true)
                .wantsStudentOrgs(false)
                .profileComplete(true)
                .build();
        profile2 = studentProfileRepository.save(profile2);
        profile2.setInterests(createInterests(profile2,
                "Lingvistika",
                "Književnost",
                "Studije u inostranstvu"
        ));
        profile2.setSubjects(createSubjects(profile2,
                "Engleski jezik -> Nastaviti sa gramatikom",
                "Literatura -> Analiza klasika",
                "Prevođenje -> Stručni tekstovi"
        ));
        profile2 = studentProfileRepository.save(profile2);
        profiles.add(profile2);

        // Profil 3: Jelena - PMF, Matematika, 1. godina
        StudentProfile profile3 = StudentProfile.builder()
                .user(users.get(2))
                .faculty("Prirodno-matematički fakultet")
                .studyProgram("Matematika")
                .yearOfStudy((short) 1)
                .dailyStudyHours(DailyStudyHours.MORE_THAN_FOUR)
                .preferredTime(PreferredTime.EVENING)
                .reminderDaysAhead(ReminderDaysAhead.ONE_DAY)
                .wantsInternship(false)
                .wantsScholarships(true)
                .wantsExchange(false)
                .wantsCompetitions(true)
                .wantsVolunteering(false)
                .wantsStudentOrgs(true)
                .profileComplete(true)
                .build();
        profile3 = studentProfileRepository.save(profile3);
        profile3.setInterests(createInterests(profile3,
                "Teorijska matematika",
                "Olimpijade",
                "Tutoriranje"
        ));
        profile3.setSubjects(createSubjects(profile3,
                "Analiza I -> Sve poglavlje o limitima",
                "Linearna algebra -> Matrice i sistemi",
                "Diskretna matematika -> Kombinatorika"
        ));
        profile3 = studentProfileRepository.save(profile3);
        profiles.add(profile3);

        // Profil 4: Petar - Ekonomski fakultet, Računovodstvo, 4. godina
        StudentProfile profile4 = StudentProfile.builder()
                .user(users.get(3))
                .faculty("Ekonomski fakultet")
                .studyProgram("Računovodstvo")
                .yearOfStudy((short) 4)
                .dailyStudyHours(DailyStudyHours.ONE_TO_TWO)
                .preferredTime(PreferredTime.BEFORE_NOON)
                .reminderDaysAhead(ReminderDaysAhead.FOURTEEN_DAYS)
                .wantsInternship(true)
                .internshipUrgency(InternshipUrgency.ACTIVE)
                .wantsScholarships(false)
                .wantsExchange(false)
                .wantsCompetitions(false)
                .wantsVolunteering(true)
                .wantsStudentOrgs(true)
                .profileComplete(true)
                .build();
        profile4 = studentProfileRepository.save(profile4);
        profile4.setInterests(createInterests(profile4,
                "Poresko pravo",
                "Korporativne finansije",
                "Audit"
        ));
        profile4.setSubjects(createSubjects(profile4,
                "Poresko pravo -> Zakoni o PDV-u",
                "Revizija -> Međunarodni standardi",
                "Korporativne finansije -> Vrednovanje preduzeća"
        ));
        profile4 = studentProfileRepository.save(profile4);
        profiles.add(profile4);

        return profiles;
    }

    /**
     * Kreira kalendarske događaje za korisnike.
     */
    private void createCalendarEvents(List<User> users) {
        LocalDateTime now = LocalDateTime.now();

        // Događaji za Anu
        createEvent(users.get(0), "Ispit iz Programiranja II", "Kompjuterska učionica 101",
                CalendarEvent.EventType.EXAM, now.plusDays(7), now.plusDays(7).plusHours(2));

        createEvent(users.get(0), "Kolokvijum - Baze podataka", "Učionica 205",
                CalendarEvent.EventType.COLLOQUIUM, now.plusDays(3), now.plusDays(3).plusHours(1));

        createEvent(users.get(0), "Rok za projekat", null,
                CalendarEvent.EventType.DEADLINE, now.plusDays(5), null);

        createEvent(users.get(0), "Intervju u Telekomunikacijama", "Beograd",
                CalendarEvent.EventType.PERSONAL, now.plusDays(10), now.plusDays(10).plusHours(1));

        // Događaji za Marka
        createEvent(users.get(1), "Ispit iz Engleskog jezika", "Učionica 301",
                CalendarEvent.EventType.EXAM, now.plusDays(14), now.plusDays(14).plusHours(3));

        createEvent(users.get(1), "Rok za seminarski rad", null,
                CalendarEvent.EventType.DEADLINE, now.plusDays(6), null);

        createEvent(users.get(1), "Studija u inostranstvu konsultacije", "Kancelarija",
                CalendarEvent.EventType.PERSONAL, now.plusDays(2), null);

        // Događaji za Jelenu
        createEvent(users.get(2), "Ispit iz Analize I", "Amfiteatar A",
                CalendarEvent.EventType.EXAM, now.plusDays(9), now.plusDays(9).plusHours(3));

        createEvent(users.get(2), "Matematička olimpijada - kolo", "Prirodno-matematički fakultet",
                CalendarEvent.EventType.PERSONAL, now.plusDays(12), now.plusDays(12).plusHours(4));

        createEvent(users.get(2), "Domaći zadatak iz Linearne algebre", null,
                CalendarEvent.EventType.DEADLINE, now.plusDays(4), null);

        // Događaji za Petra
        createEvent(users.get(3), "Ispit iz Poreskog prava", "Učionica 150",
                CalendarEvent.EventType.EXAM, now.plusDays(11), now.plusDays(11).plusHours(2));

        createEvent(users.get(3), "Prezentacija iz Revizije", "Učionica 201",
                CalendarEvent.EventType.COLLOQUIUM, now.plusDays(5), now.plusDays(5).plusHours(1));

        createEvent(users.get(3), "Završni rad - finalna verzija", null,
                CalendarEvent.EventType.DEADLINE, now.plusDays(30), null);
    }

    /**
     * Pomoćna metoda za kreiranje jednog eventos.
     */
    private void createEvent(User user, String title, String description,
                             CalendarEvent.EventType type, LocalDateTime startTime,
                             LocalDateTime endTime) {
        CalendarEvent event = CalendarEvent.builder()
                .user(user)
                .title(title)
                .description(description)
                .eventType(type)
                .startTime(startTime)
                .endTime(endTime)
                .reminderSent(false)
                .build();
        calendarEventRepository.save(event);
    }

    /**
     * Kreira listu interesovanja studenta.
     */
    private List<StudentInterest> createInterests(StudentProfile profile, String... interestNames) {
        List<StudentInterest> interests = new ArrayList<>();
        for (String name : interestNames) {
            StudentInterest interest = StudentInterest.builder()
                    .profile(profile)
                    .interest(name)
                    .build();
            interests.add(interest);
        }
        return interests;
    }

    /**
     * Kreira listu predmeta sa ciljevima.
     * Koristi lambda izraz sa znakom -> za lakšu sintaksu.
     */
    private List<StudentSubject> createSubjects(StudentProfile profile, String... subjectsWithGoals) {
        List<StudentSubject> subjects = new ArrayList<>();
        for (String subjectWithGoal : subjectsWithGoals) {
            String[] parts = subjectWithGoal.split(" -> ");
            StudentSubject subject = StudentSubject.builder()
                    .profile(profile)
                    .name(parts[0])
                    .goal(parts.length > 1 ? parts[1] : "")
                    .build();
            subjects.add(subject);
        }
        return subjects;
    }
}




