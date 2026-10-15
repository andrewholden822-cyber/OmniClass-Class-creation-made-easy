package com.example.data.local

import com.example.data.models.ClassEntity
import com.example.data.models.FlashcardEntity
import com.example.data.models.ModuleEntity
import com.example.data.models.QuizQuestionEntity
import com.example.data.models.StudentRosterEntity
import com.example.data.models.UserEntity
import org.json.JSONArray

object SampleData {

    val defaultDemoUser = UserEntity(
        email = "alex.rivera@omniclass.edu",
        name = "Alex Rivera",
        passwordHash = "password123",
        role = "student",
        avatarColorHex = "#4F46E5",
        titleOrBio = "Lifelong Learner & Tech Enthusiast",
        isLoggedIn = true
    )

    val starterClasses: List<ClassEntity> = listOf(
        ClassEntity(
            id = "class_android_ai",
            title = "Modern Android & AI Engineering",
            domain = "Tech & Code",
            tagline = "Build intelligent, cloud-synced, edge-to-edge Android apps powered by Gemini models.",
            description = "A comprehensive hands-on curriculum guiding you from Jetpack Compose foundations to building production-grade on-device and cloud generative AI applications. Master modern architecture, coroutines, Room persistence, and model integrations.",
            level = "Intermediate",
            durationText = "6 Weeks",
            instructorName = "Dr. Maya Lin",
            instructorTitle = "Lead Mobile Architect & AI Researcher",
            instructorAvatarColor = "#4F46E5",
            keyTakeawaysJson = JSONArray(listOf(
                "Design fluid declarative UIs using Jetpack Compose & Material 3",
                "Integrate Gemini 3.5 Flash and Pro REST APIs seamlessly",
                "Build reactive local databases using Room & Kotlin Coroutines",
                "Export and deploy cross-platform web companions"
            )).toString(),
            isEnrolled = true,
            progressPercent = 45,
            rating = 4.95f,
            studentCount = 840,
            bannerGradientStart = "#312E81",
            bannerGradientEnd = "#1E1B4B"
        ),
        ClassEntity(
            id = "class_sourdough",
            title = "Artisan Sourdough & Wild Fermentation",
            domain = "Arts & Craft",
            tagline = "The scientific art of cultivating wild yeasts, hydration math, and blistered crusts.",
            description = "Unlock the secrets of traditional French and San Francisco sourdough bread baking. From creating a vigorous sourdough starter from scratch to understanding gluten rheology, autolyse, banneton shaping, and Dutch oven steam baking.",
            level = "All Levels",
            durationText = "3 Weeks",
            instructorName = "Chef Julian Mercier",
            instructorTitle = "Master Baker & Fermentation Specialist",
            instructorAvatarColor = "#D97706",
            keyTakeawaysJson = JSONArray(listOf(
                "Maintain a resilient, highly active sourdough starter",
                "Calculate Baker's Percentages and hydration with precision",
                "Perfect coil folds, bulk fermentation, and cold retard",
                "Score intricate patterns and achieve an open, gelatinized crumb"
            )).toString(),
            isEnrolled = false,
            progressPercent = 0,
            rating = 4.88f,
            studentCount = 612,
            bannerGradientStart = "#78350F",
            bannerGradientEnd = "#451A03"
        ),
        ClassEntity(
            id = "class_neuroscience",
            title = "Neurobiology of Peak Focus & Flow",
            domain = "Science & Academic",
            tagline = "Harness dopamine, acetylcholine, and circadian cycles to achieve deep cognitive output.",
            description = "Explore empirical neuroscience research on human attention, state transitions into deep flow, and physiological protocols that reduce distraction, enhance memory consolidation, and prevent cognitive burnout.",
            level = "Beginner",
            durationText = "4 Weeks",
            instructorName = "Prof. Elena Rostova",
            instructorTitle = "Cognitive Neuroscientist & Author",
            instructorAvatarColor = "#059669",
            keyTakeawaysJson = JSONArray(listOf(
                "The biochemical tripod of focus: Dopamine, Epinephrine & Acetylcholine",
                "Protocols for 90-minute ultradian rhythm work sprints",
                "Non-sleep deep rest (NSDR) and sleep optimization for neuroplasticity",
                "Eliminating cognitive residue and friction during knowledge work"
            )).toString(),
            isEnrolled = true,
            progressPercent = 70,
            rating = 4.97f,
            studentCount = 1290,
            bannerGradientStart = "#064E3B",
            bannerGradientEnd = "#022C22"
        ),
        ClassEntity(
            id = "class_writing",
            title = "Epic Worldbuilding & Speculative Fiction",
            domain = "Arts & Design",
            tagline = "Architect living mythologies, compelling character arcs, and believable speculative worlds.",
            description = "Learn the craft of high-concept fiction writing from world structure (geography, magical/technological systems, sociopolitical dynamics) to pacing, dialogue subtext, and reader emotional investment.",
            level = "Intermediate",
            durationText = "8 Weeks",
            instructorName = "Kaelen Thorne",
            instructorTitle = "Award-Winning Fantasy & Sci-Fi Novelist",
            instructorAvatarColor = "#7C3AED",
            keyTakeawaysJson = JSONArray(listOf(
                "Construct consistent magic systems (Sanderson's Laws of Magic)",
                "Create three-dimensional antiheroes and multifaceted conflicts",
                "Write evocative sensory prose that pulls readers into the setting",
                "Structure 3-act and 5-act narrative frameworks for novels"
            )).toString(),
            isEnrolled = false,
            progressPercent = 0,
            rating = 4.92f,
            studentCount = 430,
            bannerGradientStart = "#581C87",
            bannerGradientEnd = "#3B0764"
        ),
        ClassEntity(
            id = "class_fitness",
            title = "Functional Calisthenics & Joint Longevity",
            domain = "Health & Fitness",
            tagline = "Build gymnastic strength, mobility, and resilient joints with bodyweight mastery.",
            description = "A progressive calisthenics methodology focusing on foundational strength (scapular control, hollow body, push/pull balance) leading up to advanced levers, handstands, and lifetime mobility.",
            level = "All Levels",
            durationText = "6 Weeks",
            instructorName = "Marcus Vance",
            instructorTitle = "Biomechanics Coach & Former Gymnast",
            instructorAvatarColor = "#DC2626",
            keyTakeawaysJson = JSONArray(listOf(
                "Unlock strict pull-ups, ring dips, and controlled handstand balance",
                "Develop bulletproof tendon resilience for elbows, knees, and shoulders",
                "Implement active mobility drills for thoracic spine and hips",
                "Design periodized bodyweight splits tailored to your recovery"
            )).toString(),
            isEnrolled = false,
            progressPercent = 0,
            rating = 4.89f,
            studentCount = 520,
            bannerGradientStart = "#7F1D1D",
            bannerGradientEnd = "#450A0A"
        )
    )

    fun getSampleModules(): List<ModuleEntity> = listOf(
        // Android Modules
        ModuleEntity(
            id = "mod_android_1",
            classId = "class_android_ai",
            orderIndex = 1,
            title = "Foundations of Modern Declarative UI",
            summary = "Explore Compose state management, remember keys, and Material 3 design tokens.",
            durationMinutes = 45,
            lessonContentMarkdown = """
# Foundations of Modern Declarative UI

Jetpack Compose radically simplifies UI development on Android. Unlike imperative View hierarchies, Compose describes what the UI should look like for a given state.

### Key Concepts:
1. **Recomposition**: Compose executes composable functions again when their inputs change. Ensure functions are idempotent and free of side effects.
2. **State Hoisting**: Move state to a caller to make components stateless, testable, and reusable.
3. **DerivedStateOf**: Use `derivedStateOf` to buffer high-frequency state updates like scroll offsets.

```kotlin
@Composable
fun CounterButton(count: Int, onIncrement: () -> Unit) {
    Button(onClick = onIncrement) {
        Text("Count: ${'$'}count")
    }
}
```

### Best Practice Tip:
Always enforce a minimum 48dp touch target for accessibility and leverage `Modifier.testTag` for predictable automated testing.
            """.trimIndent(),
            isCompleted = true
        ),
        ModuleEntity(
            id = "mod_android_2",
            classId = "class_android_ai",
            orderIndex = 2,
            title = "Reactive Persistence with Room & Coroutines",
            summary = "Build local SQLite databases with DAO interfaces and Flow reactive streams.",
            durationMinutes = 60,
            lessonContentMarkdown = """
# Reactive Persistence with Room

Room provides an abstraction layer over SQLite that allows fluent database access while leveraging the full power of SQLite.

### Core Architecture:
- **Entities**: Represent database tables.
- **DAO (Data Access Object)**: Defines methods to access the database. Queries returning `Flow<T>` emit new values whenever the underlying table changes.
- **TypeConverters**: Convert custom objects like lists or timestamps to primitive types stored by SQLite.

```kotlin
@Dao
interface CourseDao {
    @Query("SELECT * FROM courses ORDER BY title ASC")
    fun getAllCourses(): Flow<List<CourseEntity>>
}
```
            """.trimIndent(),
            isCompleted = true
        ),
        ModuleEntity(
            id = "mod_android_3",
            classId = "class_android_ai",
            orderIndex = 3,
            title = "Integrating Gemini 3.5 Flash & Structured JSON",
            summary = "Call Gemini REST APIs asynchronously, enforce JSON schemas, and stream responses.",
            durationMinutes = 55,
            lessonContentMarkdown = """
# Integrating Gemini AI in Android

Modern mobile intelligence begins with fast, multimodal LLMs. Gemini 3.5 Flash offers low latency and high reasoning fidelity for mobile applications.

### Direct REST Implementation:
We communicate with `https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent` using OkHttp and Retrofit.

- Inject API keys securely via `BuildConfig.GEMINI_API_KEY`.
- Specify `responseMimeType = "application/json"` in `generationConfig` when you require strict structured payloads.
- Always execute network calls on `Dispatchers.IO` and handle network offline transitions gracefully.
            """.trimIndent(),
            isCompleted = false
        ),

        // Sourdough Modules
        ModuleEntity(
            id = "mod_sourdough_1",
            classId = "class_sourdough",
            orderIndex = 1,
            title = "The Microbiology of Wild Starter",
            summary = "Capture wild lactic acid bacteria and yeasts using flour, water, and ambient air.",
            durationMinutes = 40,
            lessonContentMarkdown = """
# The Microbiology of Wild Starter

A sourdough starter is a symbiotic culture of lactic acid bacteria (principally *Lactobacillus sanfranciscensis*) and wild yeasts (*Candida humilis*).

### Daily Feeding Schedule:
- Day 1: 50g whole rye flour + 50g water (80°F).
- Day 2-4: Discard half, feed 50g unbleached bread flour + 50g water.
- Day 5+: Feed twice daily until it reliably triples in volume within 4 to 6 hours.
            """.trimIndent(),
            isCompleted = false
        ),
        ModuleEntity(
            id = "mod_sourdough_2",
            classId = "class_sourdough",
            orderIndex = 2,
            title = "Hydration Math & Baker's Percentages",
            summary = "Master flour-relative ratios: hydration, levain inoculations, and salt control.",
            durationMinutes = 50,
            lessonContentMarkdown = """
# Baker's Percentages & Hydration

In baker's percentages, the total flour weight is always designated as 100%. All other ingredients are expressed as a percentage of that total flour weight.

| Ingredient | Weight (g) | Baker's % |
|---|---|---|
| Bread Flour | 500g | 100% |
| Water | 375g | 75% (Hydration) |
| Active Levain | 100g | 20% |
| Fine Sea Salt | 10g | 2% |
            """.trimIndent(),
            isCompleted = false
        ),

        // Neuroscience Modules
        ModuleEntity(
            id = "mod_neuro_1",
            classId = "class_neuroscience",
            orderIndex = 1,
            title = "The Neurochemistry of Focus",
            summary = "Understand how dopamine, epinephrine, and acetylcholine synchronize attention.",
            durationMinutes = 45,
            lessonContentMarkdown = """
# The Neurochemistry of Focus

High focus is not sheer willpower; it is a neurochemical cocktail orchestrated by specific neurotransmitters:
1. **Epinephrine (Adrenaline)**: Provides alertness and energy to the cerebral cortex.
2. **Acetylcholine**: Acts as a neurochemical highlighter, directing attention to a specific focal point.
3. **Dopamine**: Drives anticipation and sustained motivation across friction points.

### The 90-Minute Ultradian Sprint Protocol:
- **0–10 min**: Friction phase. The brain resists cognitive effort. Expect agitation.
- **10–70 min**: Optimal flow groove. Maximum neurochemical coherence.
- **70–90 min**: Fading attention. Time to wind down and rest.
            """.trimIndent(),
            isCompleted = true
        )
    )

    fun getSampleQuizzes(): List<QuizQuestionEntity> = listOf(
        QuizQuestionEntity(
            id = "quiz_android_1",
            classId = "class_android_ai",
            question = "Which dispatcher is appropriate for executing Room database writes and Gemini REST API calls?",
            optionsJson = JSONArray(listOf("Dispatchers.Main", "Dispatchers.IO", "Dispatchers.Default", "Dispatchers.Unconfined")).toString(),
            correctIndex = 1,
            explanation = "Dispatchers.IO is optimized for disk and network I/O operations outside the UI main thread."
        ),
        QuizQuestionEntity(
            id = "quiz_android_2",
            classId = "class_android_ai",
            question = "What does Jetpack Compose use to avoid re-running unnecessary composable functions?",
            optionsJson = JSONArray(listOf("Smart Recomposition", "Manual invalidate() calls", "Fragment Transactions", "DOM Virtualization")).toString(),
            correctIndex = 0,
            explanation = "Jetpack Compose checks parameter equality to perform Smart Recomposition, skipping unchanged functions."
        ),
        QuizQuestionEntity(
            id = "quiz_sourdough_1",
            classId = "class_sourdough",
            question = "In Baker's Percentages, which ingredient is always defined as 100%?",
            optionsJson = JSONArray(listOf("Total Water", "Total Flour", "Combined Dough Weight", "Levain")).toString(),
            correctIndex = 1,
            explanation = "Total flour weight is always designated as 100%, and all other ingredients are calculated relative to it."
        ),
        QuizQuestionEntity(
            id = "quiz_neuro_1",
            classId = "class_neuroscience",
            question = "Which neurotransmitter acts as the brain's 'spotlight' to direct specific focal attention?",
            optionsJson = JSONArray(listOf("Serotonin", "GABA", "Acetylcholine", "Melatonin")).toString(),
            correctIndex = 2,
            explanation = "Acetylcholine is released from the basal forebrain and acts like a neural highlighter for sensory signals."
        )
    )

    fun getSampleFlashcards(): List<FlashcardEntity> = listOf(
        FlashcardEntity(
            id = "fc_android_1",
            classId = "class_android_ai",
            frontTerm = "State Hoisting",
            backDefinition = "A programming pattern in Compose where state is moved to a caller to make components stateless, testable, and reusable."
        ),
        FlashcardEntity(
            id = "fc_android_2",
            classId = "class_android_ai",
            frontTerm = "Flow<T>",
            backDefinition = "A cold asynchronous reactive stream from Kotlin Coroutines that emits values sequentially and completes normally or with an exception."
        ),
        FlashcardEntity(
            id = "fc_sourdough_1",
            frontTerm = "Autolyse",
            classId = "class_sourdough",
            backDefinition = "Resting mixed flour and water before adding levain or salt to let enzymes break down starches and develop gluten passively."
        ),
        FlashcardEntity(
            id = "fc_neuro_1",
            frontTerm = "Ultradian Rhythm",
            classId = "class_neuroscience",
            backDefinition = "A biological cycle recurrent throughout a 24-hour day with a frequency of approximately 90 minutes of peak alertness followed by rest."
        )
    )

    fun getSampleRoster(): List<StudentRosterEntity> = listOf(
        StudentRosterEntity(
            id = "st_1",
            classId = "class_android_ai",
            name = "Sophia Chen",
            email = "sophia.c@example.com",
            avatarColorHex = "#3B82F6",
            gradePercent = 96,
            assignmentStatus = "Graded"
        ),
        StudentRosterEntity(
            id = "st_2",
            classId = "class_android_ai",
            name = "Liam O'Connor",
            email = "liam.oc@example.com",
            avatarColorHex = "#10B981",
            gradePercent = 88,
            assignmentStatus = "Submitted"
        ),
        StudentRosterEntity(
            id = "st_3",
            classId = "class_android_ai",
            name = "Amina Zahra",
            email = "amina.z@example.com",
            avatarColorHex = "#F59E0B",
            gradePercent = 94,
            assignmentStatus = "Pending Review"
        ),
        StudentRosterEntity(
            id = "st_4",
            classId = "class_android_ai",
            name = "David Park",
            email = "david.p@example.com",
            avatarColorHex = "#8B5CF6",
            gradePercent = 91,
            assignmentStatus = "Graded"
        )
    )
}
