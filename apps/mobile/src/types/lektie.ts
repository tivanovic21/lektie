// JSON shapes the Mobile API returns. They mirror db/migrations/V2__schema.sql (snake_case -> camelCase).
// Until the API exists, src/mocks/ serves the same shapes.

export type Id = string;

export type Role = 'student' | 'parent' | 'teacher' | 'editor' | 'admin';

export interface User {
  id: Id;
  role: Role;
  username: string;
  displayName: string;
  aiEnabled: boolean;
}

export interface Book {
  id: Id;
  slug: string;
  title: string;
  author: string;
}

export interface Chapter {
  id: Id;
  bookId: Id;
  number: number;
  title: string;
  readingMinutes: number | null;
}

// ── Questions ──────────────────────────────────────────────────────────
// Every answer explains itself (rule 1): if the student picks B and B is wrong, B's explanation says why.

export interface Item {
  id: string;
  text: string;
  explanation: string;
}

export interface Option extends Item {
  correct: boolean;
}

/** questions.type + questions.body */
export type QuestionBody =
  | { type: 'choice'; multiple: boolean; options: Option[] }
  | { type: 'who_said'; quote: string; options: Option[] }
  | { type: 'true_false'; statements: (Item & { isTrue: boolean })[] }
  | { type: 'order'; events: Item[] } // stored in the correct order; shuffle before showing
  | { type: 'match_pairs'; pairs: { id: string; left: string; right: string; explanation: string }[] }
  | { type: 'fill_blank'; text: string; blanks: { id: string; options: Option[] }[] }; // text: "... {{b1}} ..."

export type QuestionType = QuestionBody['type'];

/** Where the answer is in the book ("Ponovno pročitaj: 2. poglavlje"). */
export interface QuestionRef {
  chapterId: Id;
  chapterNumber: number;
  quote: string | null;
}

export type Question = QuestionBody & {
  id: Id;
  chapterId: Id | null; // null = final quiz only
  prompt: string;
  explanation: string; // shown after the task ("Bravo!" / "Skoro!")
  refs: QuestionRef[];
};

/** attempt_answers.answer */
export type Answer =
  | { selected: string[] } // choice, who_said
  | { values: Record<string, boolean> } // true_false: statementId -> answer
  | { order: string[] } // order: event ids
  | { pairs: Record<string, string> } // match_pairs: id of left side -> id of the pair whose right side was picked
  | { blanks: Record<string, string> }; // fill_blank: blankId -> optionId

// ── Progress ───────────────────────────────────────────────────────────

export interface Enrollment {
  id: Id;
  book: Book;
  examDate: string; // YYYY-MM-DD
  startedAt: string; // ISO timestamp
  readChapters: number[]; // "Čitao/la sam"; the highest one is the AI's spoiler wall
  masteredChapters: number[]; // chapter quiz passed
}

export interface ReviewItem {
  question: Question;
  box: number;
  dueAt: string;
}

export interface Note {
  id: Id;
  chapterId: Id | null;
  body: string;
  updatedAt: string;
}

export interface ChatMessage {
  id: Id;
  role: 'student' | 'assistant';
  content: string;
  contextChapter: number; // "zna do 1. poglavlja"
  createdAt: string;
}
