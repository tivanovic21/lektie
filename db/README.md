# Database

PostgreSQL 17. **This folder owns the schema.** The APIs never change the schema; they run with `ddl-auto: validate`.

## Migrations (`migrations/`)

- Tool: [Flyway](https://documentation.red-gate.com/flyway). Docker Compose applies them on `docker compose up`.
- Name: `V<number>__<what>.sql`, for example `V2__create_books.sql`.
- Never edit a migration that is on `main`. Add a new one.
- Both backend developers review every migration (see `.github/CODEOWNERS`).

## Seed content (`seed/`)

Hand-made content for the one book in scope (*Čudnovate zgode šegrta Hlapića*): chapters, quizzes, answer explanations, links between chapters.
Format and loading are decided when the schema exists.
