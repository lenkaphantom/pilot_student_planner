-- =============================================
-- V1__init_schema.sql
-- PILOT - inicijalna shema baze podataka
-- =============================================

-- Tabela korisnika (osnova autentifikacije)
CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,   -- BCrypt hash
    full_name   VARCHAR(255) NOT NULL,
    role        VARCHAR(50)  NOT NULL DEFAULT 'STUDENT',
    enabled     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Profil studenta (popunjava se tokom onboardinga)
CREATE TABLE student_profiles (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT       NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    faculty         VARCHAR(255),
    study_program   VARCHAR(255),
    year_of_study   SMALLINT CHECK (year_of_study BETWEEN 1 AND 7),
    -- stil ucenja (Korak 5/5 onboarding) - Enum vrednosti
    daily_study_hours   VARCHAR(30),   -- LESS_THAN_ONE, ONE_TO_TWO, TWO_TO_FOUR, MORE_THAN_FOUR
    preferred_time      VARCHAR(30),   -- MORNING, BEFORE_NOON, AFTERNOON, EVENING
    reminder_days_ahead VARCHAR(20) DEFAULT 'THREE_DAYS',  -- ONE_DAY, THREE_DAYS, SEVEN_DAYS, FOURTEEN_DAYS
    -- prilike (Korak 4/5)
    wants_internship        BOOLEAN DEFAULT FALSE,
    internship_urgency      VARCHAR(30),  -- ACTIVE, MAYBE, NOT_YET
    wants_scholarships      BOOLEAN DEFAULT FALSE,
    wants_exchange          BOOLEAN DEFAULT FALSE,
    wants_competitions      BOOLEAN DEFAULT FALSE,
    wants_volunteering      BOOLEAN DEFAULT FALSE,
    wants_student_orgs      BOOLEAN DEFAULT FALSE,
    profile_complete        BOOLEAN DEFAULT FALSE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Interesovanja studenta (Korak 2/5 - vise opcija, fleksibilno)
CREATE TABLE student_interests (
    id          BIGSERIAL PRIMARY KEY,
    profile_id  BIGINT       NOT NULL REFERENCES student_profiles(id) ON DELETE CASCADE,
    interest    VARCHAR(255) NOT NULL,  -- Korisnik sam unosi - fleksibilno
    UNIQUE(profile_id, interest)
);

-- Predmeti studenta (Korak 3/5, fleksibilno)
CREATE TABLE student_subjects (
    id          BIGSERIAL PRIMARY KEY,
    profile_id  BIGINT      NOT NULL REFERENCES student_profiles(id) ON DELETE CASCADE,
    name        VARCHAR(255) NOT NULL,
    goal        VARCHAR(255),   -- Korisnik sam unosi - fleksibilno
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Dogadjaji u kalendaru (ispiti, kolokvijumi, licne obaveze)
CREATE TABLE calendar_events (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    event_type  VARCHAR(50)  NOT NULL,  -- 'EXAM', 'COLLOQUIUM', 'PERSONAL', 'DEADLINE'
    start_time  TIMESTAMP    NOT NULL,
    end_time    TIMESTAMP,
    subject_id  BIGINT REFERENCES student_subjects(id) ON DELETE SET NULL,
    reminder_sent BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Refresh tokeni (za bezbedno obnavljanje JWT access tokena)
CREATE TABLE refresh_tokens (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token       VARCHAR(512) NOT NULL UNIQUE,
    expires_at  TIMESTAMP    NOT NULL,
    revoked     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Indeksi za ceste upite
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_calendar_events_user_id ON calendar_events(user_id);
CREATE INDEX idx_calendar_events_start_time ON calendar_events(start_time);
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
