# Lektie (mobile)

App for students and parents: reading plan, chapter quizzes, review, final quiz, AI companion, parent corner.

React Native + Expo + TypeScript. Talks to the Mobile API (`http://localhost:8081`; on a real phone use your computer's LAN IP).

```bash
pnpm install          # from the repo root
pnpm mobile           # Expo dev server, scan the QR code with Expo Go
pnpm --filter @lektie/mobile exec expo install <package>   # add Expo-compatible packages
```

See `AGENTS.md` for Expo rules (use Expo Router, do not edit `ios/` or `android/`).
