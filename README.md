# Lektie

> Pročitaj. Zapamti. Položi.

A reading companion for Croatian students who read their *lektire*. Students read a chapter, solve a short quiz, and talk about the book with an AI that only knows the book up to their current chapter. Parents and teachers see the progress.

Course project. Scope: one book, *Čudnovate zgode šegrta Hlapića*, with hand-made content.

## What is where

- `apps/mobile` - app for students and parents (React Native, Expo)
- `apps/web` - Lektie Studio, for teachers and editors (React, Vite)
- `services/mobile-api` - API for the mobile app (Spring Boot, port 8081)
- `services/web-api` - API for Lektie Studio (Spring Boot, port 8082)
- `services/common` - shared Java code
- `db/migrations` - database schema (PostgreSQL 17, Flyway)

Both APIs use the same database. Only the migrations change the schema; the APIs just validate it.

## Running it

You need Node.js 22+, pnpm 10+ (`corepack enable`), JDK 21 and Docker.

```bash
cp .env.example .env
docker compose up -d                       # database + migrations
docker compose --profile api up -d --build # also both APIs (check http://localhost:8081/api/ping)

pnpm install
pnpm mobile    # Expo, scan the QR code with Expo Go
pnpm web       # Studio on http://localhost:5173

cd services
./gradlew build
./gradlew :mobile-api:bootRun
```

## AI companion

The Mobile API builds every AI request, never the app. For a student on chapter 4 it sends the summaries of chapters 1-3 and the full text of chapter 4, plus the recent chat. It never sends the student's name, age or school. A parent has to turn the AI on. Voice is turned into text on the phone, so audio never leaves it.

## Working together

- Work on a branch (`feature/...`, `fix/...`) and open a pull request.
- Before the PR, run `pnpm typecheck && pnpm lint && pnpm build:web` and `cd services && ./gradlew build`.
- Never edit a migration that is already on `main`, add a new one.
- Never commit `.env` or secrets.
