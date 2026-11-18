// Placeholder data in the Mobile API's shapes, so screens can be built before the API exists.
// Content comes from the project presentation and is NOT checked against the book; editors replace it.
import type { Book, Chapter, ChatMessage, Enrollment, Question, User } from '../types/lektie';

export const student: User = {
  id: 'user-ana',
  role: 'student',
  username: 'ana',
  displayName: 'Ana',
  aiEnabled: true,
};

export const book: Book = {
  id: 'book-hlapic',
  slug: 'hlapic',
  title: 'Čudnovate zgode šegrta Hlapića',
  author: 'Ivana Brlić-Mažuranić',
};

export const chapters: Chapter[] = [
  { id: 'ch-1', bookId: book.id, number: 1, title: 'Kod majstora Mrkonje', readingMinutes: 12 },
  { id: 'ch-2', bookId: book.id, number: 2, title: 'Prvi dan putovanja', readingMinutes: 15 },
];

export const enrollment: Enrollment = {
  id: 'enr-ana-hlapic',
  book,
  examDate: '2026-12-11',
  startedAt: '2026-11-18T08:00:00Z',
  readChapters: [1],
  masteredChapters: [],
};

export const questions: Question[] = [
  {
    id: 'q-1',
    chapterId: 'ch-1',
    type: 'choice',
    multiple: false,
    prompt: 'Zašto je Hlapić odlučio pobjeći od majstora Mrkonje?',
    explanation: 'Majstor Mrkonja bio je strog i nepravedan prema Hlapiću.',
    refs: [{ chapterId: 'ch-1', chapterNumber: 1, quote: null }],
    options: [
      { id: 'a', text: 'Majstor ga je nepravedno istukao zbog pretijesnih čizmica', correct: true, explanation: 'Točno, to je bila kap koja je prelila čašu.' },
      { id: 'b', text: 'Majstorica ga je otjerala iz kuće', correct: false, explanation: 'Majstorica je bila dobra prema Hlapiću.' },
      { id: 'c', text: 'Drugi šegrti su mu se rugali', correct: false, explanation: 'Hlapić je bio jedini šegrt.' },
      { id: 'd', text: 'Htio je postati vojnik', correct: false, explanation: 'Hlapić nije pobjegao zbog vojske.' },
    ],
  },
  {
    id: 'q-2',
    chapterId: 'ch-1',
    type: 'true_false',
    prompt: 'Točno ili netočno?',
    explanation: 'Sluškinja i Hlapić razmijene obećanja, a Bundaš sam pronađe Hlapića.',
    refs: [{ chapterId: 'ch-1', chapterNumber: 1, quote: null }],
    statements: [
      { id: 's1', text: 'Sluškinja je obećala da će odsada sama silaziti po mlijeko.', isTrue: true, explanation: 'Zasramila se što se sama toga nije sjetila.' },
      { id: 's2', text: 'Hlapić je obećao sluškinji cvijeće s putovanja.', isTrue: true, explanation: 'To joj je obećao zauzvrat.' },
      { id: 's3', text: 'Bundaš je ostao kod majstora Mrkonje.', isTrue: false, explanation: 'Bundaš je pobjegao za Hlapićem i njuškao dok ga nije našao u travi.' },
    ],
  },
  {
    id: 'q-3',
    chapterId: 'ch-2',
    type: 'choice',
    multiple: false,
    prompt: 'Tko je pronašao Markove guske?',
    explanation: 'Bundaš je preplivao veliku vodu i dotjerao guske iz šiblja na drugoj obali.',
    refs: [{ chapterId: 'ch-2', chapterNumber: 2, quote: null }],
    options: [
      { id: 'a', text: 'Hlapić', correct: false, explanation: 'Hlapić je tražio po grmlju, ali guske su bile preko vode.' },
      { id: 'b', text: 'Marko', correct: false, explanation: 'Marko je plakao jer ih nije mogao naći.' },
      { id: 'c', text: 'Markova majka', correct: false, explanation: 'Ona je u to vrijeme bila izvan kuće.' },
      { id: 'd', text: 'Bundaš', correct: true, explanation: 'Hlapić mu je zato obećao kobasicu „za deset kruna” kad se obogati.' },
    ],
  },
  {
    id: 'q-4',
    chapterId: 'ch-2',
    type: 'who_said',
    prompt: 'Tko je to rekao?',
    quote: 'Kupit ću ti kobasicu za deset kruna kad se obogatim!', // placeholder, not a verbatim quote
    explanation: 'Hlapić je tako zahvalio Bundašu što je pronašao guske.',
    refs: [{ chapterId: 'ch-2', chapterNumber: 2, quote: null }],
    options: [
      { id: 'a', text: 'Hlapić', correct: true, explanation: 'Hlapić je to obećao Bundašu.' },
      { id: 'b', text: 'Marko', correct: false, explanation: 'Marko je bio sretan što su guske pronađene, ali nije on to obećao.' },
      { id: 'c', text: 'Majstor Mrkonja', correct: false, explanation: 'Majstor Mrkonja nije bio s Hlapićem na putu.' },
    ],
  },
  {
    id: 'q-5',
    chapterId: 'ch-2',
    type: 'order',
    prompt: 'Poredaj događaje.',
    explanation: 'Hlapić je pobjegao, Bundaš ga je sustigao, a zatim su zajedno pomogli Marku.',
    refs: [
      { chapterId: 'ch-1', chapterNumber: 1, quote: null },
      { chapterId: 'ch-2', chapterNumber: 2, quote: null },
    ],
    events: [
      { id: 'e1', text: 'Majstor istuče Hlapića zbog pretijesnih čizmica.', explanation: 'To se dogodi u 1. poglavlju, prije bijega.' },
      { id: 'e2', text: 'Hlapić i sluškinja razmijene obećanja.', explanation: 'Oprostio se sa sluškinjom prije nego što je otišao.' },
      { id: 'e3', text: 'Bundaš pronađe Hlapića u travi.', explanation: 'Bundaš ga je sustigao kad je Hlapić već otišao.' },
      { id: 'e4', text: 'Bundaš dotjera Markove guske.', explanation: 'To se dogodi tek u 2. poglavlju.' },
    ],
  },
  {
    id: 'q-6',
    chapterId: 'ch-2',
    type: 'match_pairs',
    prompt: 'Spoji likove s opisom.',
    explanation: 'U prva dva poglavlja upoznajemo Hlapićeve prve prijatelje i protivnike.',
    refs: [
      { chapterId: 'ch-1', chapterNumber: 1, quote: null },
      { chapterId: 'ch-2', chapterNumber: 2, quote: null },
    ],
    pairs: [
      { id: 'p1', left: 'Mrkonja', right: 'strogi majstor postolar', explanation: 'Hlapić je bio njegov šegrt.' },
      { id: 'p2', left: 'Bundaš', right: 'pas koji je pobjegao za Hlapićem', explanation: 'Pronašao je Hlapića u travi.' },
      { id: 'p3', left: 'Marko', right: 'dječak koji je izgubio guske', explanation: 'Plakao je jer ih nije mogao naći.' },
    ],
  },
  {
    id: 'q-7',
    chapterId: 'ch-2',
    type: 'fill_blank',
    prompt: 'Dopuni rečenicu.',
    text: 'Hlapić je bio šegrt kod majstora {{b1}}, a za njim je pobjegao pas {{b2}}.',
    explanation: 'Mrkonja je Hlapićev majstor, a Bundaš njegov vjerni pas.',
    refs: [{ chapterId: 'ch-1', chapterNumber: 1, quote: null }],
    blanks: [
      {
        id: 'b1',
        options: [
          { id: 'a', text: 'Mrkonje', correct: true, explanation: 'Majstor Mrkonja bio je postolar.' },
          { id: 'b', text: 'Marka', correct: false, explanation: 'Marko je dječak s guskama iz 2. poglavlja.' },
        ],
      },
      {
        id: 'b2',
        options: [
          { id: 'a', text: 'Bundaš', correct: true, explanation: 'Bundaš je njuškao dok nije našao Hlapića.' },
          { id: 'b', text: 'Šarko', correct: false, explanation: 'U knjizi nema psa Šarka.' },
        ],
      },
    ],
  },
];

export const chat: ChatMessage[] = [
  { id: 'm1', role: 'student', content: 'Zašto je majstor Mrkonja bio tako strog prema Hlapiću?', contextChapter: 1, createdAt: '2026-11-18T18:00:00Z' },
  { id: 'm2', role: 'assistant', content: 'Dobro pitanje! Majstora je nekoć snašla velika nesreća o kojoj ćeš više saznati kasnije. Kako takvo što mijenja čovjeka?', contextChapter: 1, createdAt: '2026-11-18T18:00:05Z' },
  { id: 'm3', role: 'student', content: 'Možda je postao tužan pa se ljuti na sve?', contextChapter: 1, createdAt: '2026-11-18T18:01:00Z' },
  { id: 'm4', role: 'assistant', content: 'Zanimljiva ideja! Zapamti je, vratit ćemo se na nju kad pročitaš dalje.', contextChapter: 1, createdAt: '2026-11-18T18:01:04Z' },
];
