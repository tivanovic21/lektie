# Lektie: context for Claude Code

Read `README.md` and `docs/architecture.md` first.

## Product

- Users: students in Croatian primary and secondary schools (main group), parents (buyers, see progress), teachers and editors (write and check content in Lektie Studio). Adult readers and community features are future work, out of scope.
- Flow: pick book + exam date -> app plans the chapters -> student reads (paper or e-book) -> short chapter quiz (6-10 items, 2-4 min) -> AI chat about the book -> final quiz (exam rehearsal). Missed questions come back in review.
- Scope for the course: ONE book, *Čudnovate zgode šegrta Hlapića*, content made by hand. Final demo: students solve a quiz or two and chat with the AI.

## Rules that shape the design

These answer the critiques from the project presentation:

1. Not "yet another quiz app": content is personal and high quality; every answer has an explanation and points back to the book.
2. Quizzes link chapter to chapter: questions can reference earlier chapters, and missed questions return later.
3. AI must not spoil: the companion knows the book only up to the student's current chapter. Enforce this on the server (Mobile API), never only in the prompt or the client.
4. AI teaches, does not answer: it guides with questions, does not write homework or reading diaries.
5. Child safety: a parent enables the AI; name, age and school are never sent to the AI provider.
6. Open question: use in school tests vs. only at home.

## Team and ownership

- Mobile app (`apps/mobile`): Za (tivanovic21).
- Web app (`apps/web`): one colleague.
- Backend (`services/`, `db/`): two colleagues, split into Mobile API and Web API.

## Conventions

- Package manager: pnpm (workspace at repo root). Use `npx expo install` style via `pnpm --filter @lektie/mobile exec expo install <pkg>` for Expo packages.
- Java: Gradle (Kotlin DSL) multi-project in `services/`, Spring Boot 4, Java 21, base package `hr.lektie`.
- Database: only Flyway migrations in `db/migrations` change the schema. APIs run with `ddl-auto: validate`.
- Keep the skeleton simple. This is a course project, not production.

## Checks before a PR

```bash
pnpm typecheck && pnpm lint && pnpm build:web
cd services && ./gradlew build
```
