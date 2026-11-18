# Lektie

> Pročitaj. Zapamti. Položi.

Lektie is a reading companion for Croatian students who read the mandatory books (*lektire*) and prepare for the *matura* exam.
Students read a chapter, solve a short quiz that links back to earlier chapters, and talk about the book with an AI companion that knows the book only up to their current chapter.
Teachers and parents see the progress of the students they manage.

Graduate course project. Scope: **one book**, *Čudnovate zgode šegrta Hlapića*, with hand-made content.

## Repository layout

| Folder | What | Stack | Owner |
| --- | --- | --- | --- |
| `apps/mobile` | App for students and parents | React Native (Expo), TypeScript | mobile |
| `apps/web` | Lektie Studio: backoffice for admins, teachers, editors | React (Vite), TypeScript | web |
| `services/mobile-api` | API for the mobile app | Java 21, Spring Boot | backend |
| `services/web-api` | API for Lektie Studio | Java 21, Spring Boot | backend |
| `services/common` | Shared Java code (entities, DTOs) | Java 21 | backend |
| `db/migrations` | The only place the schema changes | PostgreSQL 17, Flyway | backend |
| `db/seed` | Book content (chapters, quizzes) | SQL / JSON | all |
| `docs` | Architecture, API contracts, decisions | Markdown, OpenAPI | all |

See [docs/architecture.md](docs/architecture.md).

## Requirements

- Node.js 22+ and pnpm 10+ (`corepack enable` installs the pinned pnpm)
- JDK 21 (Gradle comes with the wrapper)
- Docker with Docker Compose
- Expo Go on your phone, or an Android/iOS emulator

## Quick start

```bash
cp .env.example .env

# Database + migrations
docker compose up -d

# Also run both APIs in Docker (for mobile/web work)
docker compose --profile api up -d --build
# Mobile API: http://localhost:8081/api/ping
# Web API:    http://localhost:8082/api/ping

# JavaScript apps
pnpm install
pnpm mobile        # Expo dev server
pnpm web           # Lektie Studio on http://localhost:5173

# Backend from the command line (or open services/ in IntelliJ)
cd services
./gradlew build
./gradlew :mobile-api:bootRun
./gradlew :web-api:bootRun
```

## Ways of working

- `main` is protected. Work on a branch (`feature/<short-name>`, `fix/<short-name>`) and open a pull request.
- One approval is needed. `CODEOWNERS` picks the reviewer.
- There is no CI. Run the checks locally before you open a PR: `pnpm typecheck && pnpm lint && pnpm build:web` and `cd services && ./gradlew build`.
- Never edit a migration that is on `main`. Add a new one.
- Never commit `.env` or secrets.
