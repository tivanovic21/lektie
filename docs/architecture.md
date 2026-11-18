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

How one chat turn works (Mobile API):

1. Refuse if `users.ai_enabled` is false or the student hit the daily message limit (count of today's `chat_messages`).
2. Spoiler wall `N` = highest chapter in `chapter_reads` for the enrollment.
3. Build the request, most stable part first so the provider can cache it:
   - system: the tutor rules (rule 4: guide with questions, no homework or reading diaries; Croatian; child-appropriate), kept in code;
   - book: `chapters.summary` for chapters `1..N-1`, then the full `chapters.body` of chapter `N` (the AI knows the current chapter in detail and the earlier ones in outline). This is the same for every student on chapter `N`, so put the cache breakpoint after it;
   - the last ~20 `chat_messages` of the enrollment as plain text, then the new message.
4. Save both messages with `context_chapter = N`, and the assistant row with `model`, token counts and `stop_reason` from the response.

Provider: Claude Haiku 5.5 (`claude-haiku-5-5`) through the Anthropic Java SDK, with `ANTHROPIC_API_KEY` only in the Mobile API's environment. The model name is configuration, and the provider call lives in one class, so another model or provider is a small change.

- Replay history as plain text and store no thinking blocks. Haiku 5.5 rejects replayed thinking blocks after earlier context changes, and the book context grows every time `N` grows.
- Do not send `temperature`/`top_p`/`top_k` (400 on this model). Use `effort: low` for chat.
- Summaries go to the AI, so a chapter's summary must not mention anything after that chapter. Editors check this in Studio.
- Haiku 5.5 bills $0.10 / $0.50 per million input/output tokens while the prompt is at most 100K tokens, and 5x more above that. Summaries plus one chapter stay far below that.
- `stop_reason = 'refusal'` has no automatic fallback on Haiku: show a friendly message.
- Voice: speech-to-text on the device; only text reaches the API and the database.

## API contracts

Put each API's OpenAPI file in `docs/api/` (`mobile-api.yaml`, `web-api.yaml`) and agree on changes in a PR before you code them.

## Decisions

See `docs/decisions/`.
