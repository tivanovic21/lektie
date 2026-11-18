# Architecture

```
 Students, parents                     Admins, teachers, editors
        |                                        |
 apps/mobile (React Native, Expo)        apps/web (Lektie Studio, React)
        |  HTTPS/JSON                            |  HTTPS/JSON
 services/mobile-api (Spring Boot)       services/web-api (Spring Boot)
        |        \                               |
        |         AI provider (server-side only) |
        \__________________ PostgreSQL __________/
                     schema: db/migrations (Flyway)
```

## Parts

- **Mobile API** (`:8081`): reading plan, chapter quizzes, review of missed questions, final quiz, progress, AI companion proxy, parent view.
- **Web API** (`:8082`): content editing (books, chapters, questions, explanations, chapter links), users and roles, teacher and parent views of student progress.
- **common**: shared JPA entities, enums and DTOs, so both APIs read the same tables the same way.
- **Database**: one PostgreSQL database. Flyway migrations in `db/migrations` own the schema. Both APIs only validate it.

## AI companion (no spoilers)

The Mobile API, not the app, builds the AI request. It sends only the book content up to the student's current chapter, plus the chat. It never sends the student's name, age or school. This keeps the "no spoilers" and child-safety rules on the server.

## API contracts

Put each API's OpenAPI file in `docs/api/` (`mobile-api.yaml`, `web-api.yaml`) and agree on changes in a PR before you code them.

## Decisions

See `docs/decisions/`.
