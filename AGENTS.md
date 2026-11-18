# Notes for coding agents

Read `README.md` first. This is a student course project: keep code and docs simple, no extra Markdown files.

## Product

- Users: students in Croatian primary and secondary schools, parents (see progress, turn the AI on), teachers and editors (write and check content in Lektie Studio). For now: one school with one teacher.
- Flow: pick book + exam date -> app plans the chapters -> student reads (paper or e-book) -> short chapter quiz (6-10 items, 2-4 min) -> AI chat about the book -> final quiz (chapter questions + final-only questions). Missed questions come back in review.
- Progress is stored in the database. The student, their parents, their school's teacher and admins can see it.
- Scope: ONE book, *Čudnovate zgode šegrta Hlapića*. Final demo: students solve a quiz or two and chat with the AI.

## Rules

1. Every answer has its own explanation (if B is wrong, say why B is wrong) and points back to the chapter in the book.
2. Quizzes link chapter to chapter: questions may reference earlier chapters, missed questions return later.
3. AI must not spoil: it knows the book only up to the student's current chapter. Enforce this in the Mobile API, never only in the prompt or the app.
4. AI teaches, does not answer: it guides with questions, does not write homework or reading diaries.
5. Child safety: a parent turns the AI on; name, age and school are never sent to the AI provider; voice is transcribed on the phone and audio is never stored.

## AI chat turn (Mobile API)

1. Stop if `users.ai_enabled` is false or the student hit the daily limit (count today's `chat_messages`).
2. `N` = highest chapter in `chapter_reads` for the enrollment.
3. Request, stable parts first so it caches: tutor rules (system prompt in code) -> `chapters.summary` of chapters `1..N-1` + full `chapters.body` of chapter `N` (cache breakpoint here) -> last ~20 chat messages as plain text -> new message.
4. Save both messages with `context_chapter = N`; on the assistant row also `model`, token counts and `stop_reason`.

Model: `claude-haiku-5-5` via the Anthropic Java SDK, model name in config, `ANTHROPIC_API_KEY` only on the server. Do not send `temperature`/`top_p`/`top_k` (400 on this model); use `effort: low`. Do not store or replay thinking blocks. On `stop_reason = refusal` show a friendly message.

## Code

- Mobile app (`apps/mobile`): tivanovic21. Web app (`apps/web`): one colleague. Backend (`services/`, `db/`): two colleagues.
- pnpm workspace at the root. Gradle (Kotlin DSL) in `services/`, Spring Boot 4, Java 21, package `hr.lektie`.
- Schema changes only through new Flyway migrations in `db/migrations`; APIs run with `ddl-auto: validate`.
- Question content is `questions.body` (JSONB). Its shapes and the API JSON shapes are in `apps/mobile/src/types/lektie.ts`; placeholder data in `apps/mobile/src/mocks/`.
- Expo changes every SDK release: check the docs for the `expo` version in `apps/mobile/package.json` before using an Expo API. Add packages with `pnpm --filter @lektie/mobile exec expo install <pkg>`. Use Expo Router (screens in `apps/mobile/src/app/`). Never edit `ios/` or `android/`. Packages with native code need a development build, not Expo Go.

Checks before a PR:

```bash
pnpm typecheck && pnpm lint && pnpm build:web
cd services && ./gradlew build
```
