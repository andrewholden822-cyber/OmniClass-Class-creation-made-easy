# OmniClass AI: Online Web Learning Portal & Universal Class Creator

A comprehensive online learning management web application and Android app powered by Gemini AI and Jetpack Compose. It allows users to generate **ANY class they want** structured like a full-featured online learning website—complete with interactive **Login, Sign-Up, Forgot Password, Student Learning Portal, Course Landing Pages, Syllabus Modules, Quizzes, AI Tutor, and Standalone Web Portal Exports**.

---

### User Review & Critical Revisions

> [!IMPORTANT]
> **Revised per your feedback**: The app now integrates a complete **online website & portal experience**—including Login, Sign-Up, Forgot Password, Student Profiles, Course Enrollment Landing Pages, and Web Exporter that generates an interactive online course site for iOS, Android, and Desktop browsers.

- **Full Website Authentication & Account Flows (New)**:
  - **Login Page**: Modern email/password login with remember-me, validation, and demo instant-login credentials.
  - **Sign-Up Page**: Student & Instructor registration with role selection (Student Learner, Course Creator/Instructor) and avatar picker.
  - **Forgot Password Page**: 3-step password recovery flow (Email input → Verification code simulation → New password creation with instant feedback).
  - **Guest / One-Tap Access**: Switch seamlessly between guest browsing, student mode, and instructor mode.
- **Website-Styled Course Portals for ANY Generated Class**:
  - **Course Landing / Website Page**: Hero banner, instructor profile, course badges, syllabus breakdown, prerequisites, and "Enroll Now" CTA.
  - **Classroom Learning Management Portal (LMS)**:
    - *Course Overview & Announcements*: Weekly updates and class discussion board.
    - *Lesson Player & Markdown Reader*: Distraction-free lesson text, code snippets, key takeaways, and "Mark as Complete" progression.
    - *Interactive Quizzes & Practice Exams*: Real-time scoring, instant explanations, and certificate of completion.
    - *Flashcard Study Deck*: Flip cards for rapid spaced repetition.
    - *Classroom Roster & Assignments*: Cohort list, student grades, and submitted homework reviews.
    - *AI Class Tutor & Assistant*: Contextual AI chat assistant trained on the specific class curriculum.
- **Universal "ANY Class" Creation Engine**:
  - Freeform natural language prompt ("Create a class on...") with zero subject restrictions (coding, cooking, quantum physics, yoga, business, creative writing, etc.).
  - AI Co-Pilot assistant for brainstorming course names, syllabi, and module schedules.
- **Cross-Platform Website Exporter**:
  - One-tap exporter that compiles any generated class into a **standalone, responsive HTML5/CSS/JS online course website** containing the landing page, login/sign-up forms, interactive syllabus player, and quiz runner. This can be opened directly on **Apple iOS Safari, macOS, Windows, or Android browsers**.
- **Universal Android OS & Device Compatibility**:
  - Built with Jetpack Compose supporting `minSdk 24` (Android 7.0) through `targetSdk 36` (Android 16), ensuring it runs smoothly on any device regardless of OS version.
  - Responsive Web-Like Layout: Top navigation bar, breadcrumbs, responsive grid, and Navigation Rail dual-pane on tablets/foldables.

---

## 1. Overview & Core Concept

### What It Does
OmniClass AI merges the power of generative AI with the structure of leading online learning websites (like Coursera, Udemy, and MasterClass):
1. **Website Authentication**: Users can log in, register new accounts, reset passwords, or enter as guests.
2. **Create Any Course**: Type any prompt, and the AI generates an entire interactive course website with structured modules, lessons, quizzes, assignments, and discussions.
3. **Learn & Interact**: Read lessons, take interactive quizzes, flip flashcards, and chat with an AI teaching assistant.
4. **Export as Website**: Export the entire course as a self-contained web portal that runs anywhere—on iPhones, iPads, Macs, and Android phones.

---

## 2. User Experience & Visual Design

### Key User Flows & Screen Map
1. **Authentication & Gateway**:
   - **Login Screen**: Clean, responsive card with email, password, "Remember Me", "Forgot Password?" link, and "Sign Up" toggle.
   - **Sign-Up Screen**: Name, email, password, role toggle (Student or Instructor), and quick onboarding tags.
   - **Forgot Password Screen**: Input email → Receive simulated 6-digit recovery code → Reset password with success toast and return to login.
2. **Website Portal Dashboard**:
   - Web-style header with branding, user profile avatar, search bar, and "Create New Class" action.
   - Course discovery tabs: *Enrolled Courses*, *All Catalog*, *Instructor Studio*.
   - Filter chips: *Academic, Tech & Code, Health & Fitness, Arts & Craft, Business*.
3. **AI Class Studio ("Create ANY Class")**:
   - Freeform prompt input + AI brainstorming ideas.
   - Customizer: Course level (Beginner to Expert), Duration (1-day crash course to 12-week semester), Tone (Academic, Casual, Hands-on bootcamp).
   - Instant generation animation showing real-time course structuring.
4. **Class Web Portal & Classroom Player**:
   - **Course Home / Landing Page**: Marketing overview, instructor bio, course metrics, enroll button.
   - **Module & Lesson Viewer**: Collapsible syllabus sidebar (drawer on mobile, permanent column on tablet) and rich lesson content.
   - **Interactive Quizzes**: Multiple-choice assessments with immediate feedback and score badge.
   - **Interactive Flashcards**: 3D flip card animation for study terms.
   - **Class Discussion & Roster**: Simulated peer discussions and student roster.
   - **AI Class Assistant**: Live interactive chat anchored to the active class syllabus.
5. **Universal Web Export Dialog**:
   - Preview and export the standalone responsive HTML/CSS/JS website for viewing on Apple iOS Safari and desktop browsers.

### Visual Identity & Theme
- **Theme Direction**: *Modern SaaS Educational Web Portal* — sleek indigo/violet primary (`#4F46E5`), dark slate background (`#0F172A`) with high-contrast chalk card surfaces (`#1E293B`), emerald accent badges (`#10B981`), and subtle border outlines (`#334155`).
- **Web Navigation Elements**:
  - Top navigation bar with logo, search, and profile badge.
  - Clean breadcrumbs navigation (`Home > Classes > AI Engineering > Lesson 2`).
  - Tablet/Desktop Navigation Rail for spacious dual-pane learning.
- **Accessibility**: 48dp touch targets, full keyboard/input accessibility, edge-to-edge window insets.

---

## 3. Key Product Decisions & Trade-Offs

| Decision | Chosen Approach | Rationale | Alternatives Considered |
|---|---|---|---|
| **Website Auth Experience** | Local Secure Session Manager with Mock Authentication & Full UI Flow (Login, Register, Forgot Password) | Gives the complete online website authentication experience immediately without forcing external cloud setup or blocking user access with API keys. | Firebase Auth (would require live external Google project provisioning and network dependency). |
| **Online Website Look & Feel** | Web-inspired Compose Components: TopBar, Breadcrumbs, Tabbed Portals, Course Landing Pages | Fulfills the user's explicit request for an online website structure with login, sign-up, and course portal. | Standard basic list views (rejected: lacks the web portal feel). |
| **Cross-Platform / Apple Compatibility** | Responsive HTML5/CSS/JS Self-Contained Web Exporter | Allows the generated class to be viewed and used as a complete website on Apple iOS Safari, desktop browsers, or Android Chrome. | Mocking an Apple binary (impossible in Android environment). |
| **Limitless Class Generation** | Gemini 3.5 Flash REST Client with Open-Domain Prompt Engine | Creates authentic, detailed curricula for literally ANY topic without rigid templates. | Hardcoded canned topics only (rejected: user emphasized "ANY class"). |
| **Local Offline Persistence** | Room Database (SQLite) with Reactive StateFlow | Fast, 100% offline access to all courses, modules, user notes, and test scores. | In-memory only (losing data on restart would destroy user creations). |

---

## 4. Technical Architecture & Data Strategy

### System Architecture

```
┌────────────────────────────────────────────────────────────────────────┐
│                        MainActivity (Compose Edge-to-Edge)             │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
    ┌───────────────────────────────┴──────────────────────────────┐
    ▼                                                              ▼
┌─────────────────────────────────┐              ┌─────────────────────────────────┐
│     Auth Flow (Web-Style)       │              │    Portal Dashboard (Web-Style) │
│ - Login Page                    │              │ - Top Navigation & Breadcrumbs  │
│ - Sign-Up Page                  │              │ - Course Catalog & My Courses   │
│ - Forgot Password Page          │              │ - Responsive Layout (Phone/Tab) │
└───────────────┬─────────────────┘              └────────────────┬────────────────┘
                │                                                 │
                └───────────────────────┬─────────────────────────┘
                                        │
           ┌────────────────────────────┼───────────────────────────┐
           ▼                            ▼                           ▼
┌─────────────────────┐      ┌─────────────────────┐     ┌─────────────────────┐
│  AI Creator Studio  │      │ Course Landing Page │     │ Classroom Portal    │
│  - "Create ANY Class│      │ - Hero & Syllabus   │     │ - Lesson Reader     │
│  - AI Co-Pilot      │      │ - Instructor Bio    │     │ - Quizzes & Cards   │
│  - Customizer       │      │ - "Enroll" CTA      │     │ - AI Chat Tutor     │
│                     │      │                     │     │ - Web HTML Exporter │
└──────────┬──────────┘      └──────────┬──────────┘     └──────────┬──────────┘
           │                            │                           │
           └────────────────────────────┼───────────────────────────┘
                                        ▼
                       ┌─────────────────────────────────┐
                       │       OmniClassViewModel        │
                       │ - Auth State & User Session     │
                       │ - Classes, Lessons & Quizzes    │
                       │ - Active Course & Tutor State   │
                       └────────────────┬────────────────┘
                                        │
                 ┌──────────────────────┴──────────────────────┐
                 ▼                                             ▼
┌─────────────────────────────────┐           ┌─────────────────────────────────┐
│         ClassRepository         │           │        GeminiApiService         │
│ - Room Database (AppDatabase)   │           │ - gemini-3.5-flash REST Client  │
│ - Local Auth & Profile Store    │           │ - Open-Domain Structured Schema │
│ - Instant Pre-loaded Courses    │           │ - Interactive Tutor Prompting   │
└─────────────────────────────────┘           └─────────────────────────────────┘
```

### Data Entities
1. **`UserEntity`**: `email`, `name`, `passwordHash`, `role` (Student / Instructor), `avatarColor`, `isLoggedIn`.
2. **`ClassEntity`**: `id`, `title`, `domain`, `tagline`, `description`, `level`, `duration`, `instructorName`, `keyTakeawaysJson`, `isEnrolled`, `progressPercent`.
3. **`ModuleEntity`**: `id`, `classId`, `orderIndex`, `title`, `summary`, `contentMarkdown`, `durationMinutes`, `isCompleted`.
4. **`QuizQuestionEntity`**: `id`, `classId`, `question`, `optionsJson`, `correctIndex`, `explanation`.
5. **`FlashcardEntity`**: `id`, `classId`, `front`, `back`.
6. **`ChatMessage`**: `id`, `classId`, `sender` (User / AI Tutor), `content`, `timestamp`.

### Execution Plan & Next Steps
1. **App Identity**: Set app name to `OmniClass Portal` in `strings.xml` and `metadata.json`. Enable `INTERNET` permission and configure `.env.example`.
2. **Data & Auth Layer**: Implement Room Database with user entities, class models, starter courses, and persistent session state.
3. **Authentication Screens**: Build the web-styled Login, Sign-Up, and Forgot Password screens with error states, password toggles, and demo credentials.
4. **Gemini AI Service**: Implement Retrofit REST client using `gemini-3.5-flash` for open-ended class synthesis and interactive study tutoring.
5. **Classroom Web Portal UI**:
   - Top navigation bar and breadcrumb trail.
   - Course Landing page with syllabus preview and enrollment action.
   - Lesson player with markdown rendering and progress tracking.
   - Quiz runner with score recap and flashcard deck.
   - Live AI Tutor chat sheet.
6. **Standalone HTML Website Exporter**: Generate portable, self-contained HTML5/CSS course web pages that can be opened or shared to Apple devices and web browsers.
7. **Verification**: Compile with `compile_applet` and verify stability.
