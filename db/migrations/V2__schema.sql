-- Lektie data model. Rules referenced below are in AGENTS.md.
-- Fixed value lists use text + CHECK (not Postgres enums): easier to map in JPA and to extend.

-- ── People ──────────────────────────────────────────────────────────────

CREATE TABLE schools (
    id   uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name text NOT NULL
);

CREATE TABLE users (
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    role          text NOT NULL CHECK (role IN ('student', 'parent', 'teacher', 'editor', 'admin')),
    username      text NOT NULL UNIQUE,
    password_hash text NOT NULL,
    display_name  text NOT NULL,                  -- never sent to the AI provider (rule 5)
    -- ponytail: a teacher sees every student of their school; add classes when a school has >1 teacher
    school_id     uuid REFERENCES schools,
    ai_enabled    boolean NOT NULL DEFAULT false, -- students only; only a linked parent may change it (rule 5)
    created_at    timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE parent_links (
    parent_id  uuid REFERENCES users ON DELETE CASCADE,
    student_id uuid REFERENCES users ON DELETE CASCADE,
    PRIMARY KEY (parent_id, student_id)
);

-- ── Content (written in Lektie Studio) ──────────────────────────────────

CREATE TABLE books (
    id        uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    slug      text NOT NULL UNIQUE,
    title     text NOT NULL,
    author    text NOT NULL,
    published boolean NOT NULL DEFAULT false
);

CREATE TABLE chapters (
    id              uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    book_id         uuid NOT NULL REFERENCES books ON DELETE CASCADE,
    number          int  NOT NULL CHECK (number > 0),
    title           text NOT NULL,
    summary         text NOT NULL,   -- AI context for chapters before the current one; never shown to students
    body            text,            -- full chapter text (public domain); AI context for the current chapter
    reading_minutes int,             -- for the reading plan
    UNIQUE (book_id, number)
);

CREATE TABLE questions (
    id          uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    book_id     uuid NOT NULL REFERENCES books ON DELETE CASCADE,
    chapter_id  uuid REFERENCES chapters ON DELETE CASCADE,  -- NULL = final quiz only
    type        text NOT NULL CHECK (type IN ('choice', 'who_said', 'true_false', 'order', 'match_pairs', 'fill_blank')),
    prompt      text NOT NULL,
    body        jsonb NOT NULL,      -- shape per type, see apps/mobile/src/types/lektie.ts; validated by Web API
    explanation text NOT NULL,       -- shown after the task (rule 1); each answer also has its own in body
    status      text NOT NULL DEFAULT 'draft' CHECK (status IN ('draft', 'in_review', 'published')),
    approved_by uuid REFERENCES users,
    position    int  NOT NULL
);
CREATE INDEX questions_chapter_idx ON questions (chapter_id);

-- Where in the book the answer is ("Ponovno pročitaj: 2. poglavlje") and links to earlier chapters (rules 1, 2).
-- Web API checks that a question only references chapters up to its own (no spoilers).
CREATE TABLE question_refs (
    question_id uuid REFERENCES questions ON DELETE CASCADE,
    chapter_id  uuid REFERENCES chapters ON DELETE CASCADE,
    quote       text,
    PRIMARY KEY (question_id, chapter_id)
);

-- ── Progress (visible to the student, their parents, their school's teacher, and admins) ──

CREATE TABLE enrollments (
    id          uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id  uuid NOT NULL REFERENCES users ON DELETE CASCADE,
    book_id     uuid NOT NULL REFERENCES books,
    exam_date   date NOT NULL,       -- the reading plan is computed from this
    started_at  timestamptz NOT NULL DEFAULT now(),
    finished_at timestamptz,
    UNIQUE (student_id, book_id)
);

-- "Čitao/la sam". The highest read chapter is the spoiler wall (rule 3).
CREATE TABLE chapter_reads (
    enrollment_id uuid REFERENCES enrollments ON DELETE CASCADE,
    chapter_id    uuid REFERENCES chapters,
    read_at       timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (enrollment_id, chapter_id)
);

CREATE TABLE notes (
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    enrollment_id uuid NOT NULL REFERENCES enrollments ON DELETE CASCADE,
    chapter_id    uuid REFERENCES chapters,
    body          text NOT NULL,
    updated_at    timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX notes_enrollment_idx ON notes (enrollment_id);

CREATE TABLE quiz_attempts (
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    enrollment_id uuid NOT NULL REFERENCES enrollments ON DELETE CASCADE,
    kind          text NOT NULL CHECK (kind IN ('chapter', 'review', 'final')),
    chapter_id    uuid REFERENCES chapters,
    started_at    timestamptz NOT NULL DEFAULT now(),
    finished_at   timestamptz,
    score         int,
    max_score     int,
    CHECK ((kind = 'chapter') = (chapter_id IS NOT NULL))
);
CREATE INDEX quiz_attempts_enrollment_idx ON quiz_attempts (enrollment_id);

CREATE TABLE attempt_answers (
    attempt_id  uuid REFERENCES quiz_attempts ON DELETE CASCADE,
    question_id uuid REFERENCES questions,  -- no cascade: answered questions are unpublished, not deleted
    answer      jsonb NOT NULL,
    is_correct  boolean NOT NULL,
    PRIMARY KEY (attempt_id, question_id)
);

-- Missed questions come back (Leitner boxes): wrong -> box 1, due tomorrow; right -> next box, due later.
CREATE TABLE review_items (
    enrollment_id uuid REFERENCES enrollments ON DELETE CASCADE,
    question_id   uuid REFERENCES questions ON DELETE CASCADE,
    box           smallint NOT NULL DEFAULT 1,
    due_at        timestamptz NOT NULL,
    PRIMARY KEY (enrollment_id, question_id)
);

-- ── AI companion ────────────────────────────────────────────────────────

-- Text only: voice is transcribed on the device, audio is never stored or sent.
-- Usage columns are filled on assistant rows from the provider's response, for cost and per-student limits.
CREATE TABLE chat_messages (
    id                uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    enrollment_id     uuid NOT NULL REFERENCES enrollments ON DELETE CASCADE,
    role              text NOT NULL CHECK (role IN ('student', 'assistant')),
    content           text NOT NULL,
    context_chapter   int  NOT NULL,  -- spoiler wall used for this message (rule 3)
    model             text,           -- e.g. 'claude-haiku-5-5'
    input_tokens      int,
    output_tokens     int,
    cache_read_tokens int,
    stop_reason       text,           -- 'end_turn', 'max_tokens', 'refusal', ...
    created_at        timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX chat_messages_enrollment_idx ON chat_messages (enrollment_id, created_at);
