# 0001: One repository for all parts

Date: 2026-11-18. Status: accepted.

## Context

Team of 4: mobile app, web app, and two backend services. Both services use one PostgreSQL database and the same book content.

## Decision

- One repository (monorepo) with `apps/`, `services/`, `db/`, `docs/`.
- pnpm workspace for the JavaScript apps. Gradle multi-project for the Java services.
- Flyway migrations in `db/migrations` own the schema. The APIs run with `ddl-auto: validate`.
- CI runs per folder (path filters), so a change in one part does not block the others.

## Consequences

- One PR can change an API contract and its client together.
- Schema changes are reviewed in one place.
- Each person must keep to their folder; `CODEOWNERS` enforces review.
