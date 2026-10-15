package com.example.data.remote

import com.example.BuildConfig
import com.example.data.models.ClassEntity
import com.example.data.models.FlashcardEntity
import com.example.data.models.ModuleEntity
import com.example.data.models.QuizQuestionEntity
import com.example.data.models.StudentRosterEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

data class GeneratedClassResult(
    val classEntity: ClassEntity,
    val modules: List<ModuleEntity>,
    val quizzes: List<QuizQuestionEntity>,
    val flashcards: List<FlashcardEntity>,
    val roster: List<StudentRosterEntity>
)

object GeminiApiService {

    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/"

    private val client = OkHttpClient.Builder()
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .build()

    private fun hasValidApiKey(): Boolean {
        val key = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }
        return key.isNotBlank() && key != "MY_GEMINI_API_KEY" && !key.startsWith("placeholder")
    }

    suspend fun generateAnyClass(
        userPrompt: String,
        targetLevel: String = "All Levels",
        durationChoice: String = "4 Weeks",
        audience: String = "General Audience",
        tone: String = "Inspiring & Practical"
    ): GeneratedClassResult = withContext(Dispatchers.IO) {
        val classId = "class_" + UUID.randomUUID().toString().take(8)

        if (hasValidApiKey()) {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                val systemPrompt = """
                    You are a world-class university dean, instructional designer, and master instructor.
                    Generate a complete, high-quality, professional online course website structure for ANY topic requested: "$userPrompt".
                    
                    Return ONLY a valid JSON object matching this schema without any markdown formatting or ticks:
                    {
                      "title": "string (Catchy, polished title)",
                      "domain": "string (e.g., Tech & Code, Arts & Crafts, Business, Science & Academic, Health & Fitness, Life Mastery)",
                      "tagline": "string (1 punchy sentence)",
                      "description": "string (2-3 paragraphs describing the course curriculum, philosophy, and practical outcomes)",
                      "instructorName": "string (Realistic expert name)",
                      "instructorTitle": "string (Professional credentials)",
                      "keyTakeaways": ["string", "string", "string", "string"],
                      "modules": [
                        {
                          "orderIndex": 1,
                          "title": "string",
                          "summary": "string",
                          "durationMinutes": 45,
                          "lessonContentMarkdown": "string (comprehensive, well-written Markdown lesson explaining concepts, examples, exercises)"
                        },
                        {
                          "orderIndex": 2,
                          "title": "string",
                          "summary": "string",
                          "durationMinutes": 50,
                          "lessonContentMarkdown": "string (detailed lesson content)"
                        },
                        {
                          "orderIndex": 3,
                          "title": "string",
                          "summary": "string",
                          "durationMinutes": 60,
                          "lessonContentMarkdown": "string (detailed lesson content)"
                        }
                      ],
                      "quizzes": [
                        {
                          "question": "string",
                          "options": ["string", "string", "string", "string"],
                          "correctIndex": 0,
                          "explanation": "string"
                        },
                        {
                          "question": "string",
                          "options": ["string", "string", "string", "string"],
                          "correctIndex": 1,
                          "explanation": "string"
                        }
                      ],
                      "flashcards": [
                        {
                          "front": "string (Term or Key Concept)",
                          "back": "string (Definition and practical significance)"
                        },
                        {
                          "front": "string",
                          "back": "string"
                        }
                      ]
                    }
                """.trimIndent()

                val requestJson = JSONObject().apply {
                    val contentsArray = JSONArray().apply {
                        put(JSONObject().apply {
                            put("role", "user")
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply {
                                    put("text", "Generate a full course on: $userPrompt\nLevel: $targetLevel\nDuration: $durationChoice\nTarget Audience: $audience\nTone: $tone\n\nReturn strictly valid JSON.")
                                })
                            })
                        })
                    }
                    put("contents", contentsArray)
                    put("systemInstruction", JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", systemPrompt) })
                        })
                    })
                    put("generationConfig", JSONObject().apply {
                        put("responseMimeType", "application/json")
                        put("temperature", 0.7)
                    })
                }

                val url = "$BASE_URL$MODEL_NAME:generateContent?key=$apiKey"
                val request = Request.Builder()
                    .url(url)
                    .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: ""

                if (response.isSuccessful && body.isNotBlank()) {
                    val root = JSONObject(body)
                    val candidates = root.optJSONArray("candidates")
                    val contentObj = candidates?.optJSONObject(0)?.optJSONObject("content")
                    val parts = contentObj?.optJSONArray("parts")
                    val text = parts?.optJSONObject(0)?.optString("text") ?: ""

                    if (text.isNotBlank()) {
                        return@withContext parseGeneratedJson(text, classId, userPrompt, targetLevel, durationChoice)
                    }
                }
            } catch (_: Exception) {
                // Fall back gracefully to procedural high-detail generator
            }
        }

        // Procedural generator when API key is not present or network times out
        return@withContext generateProceduralClass(classId, userPrompt, targetLevel, durationChoice, audience, tone)
    }

    suspend fun askAiTutor(
        classContext: ClassEntity,
        syllabusSummary: String,
        userQuestion: String
    ): String = withContext(Dispatchers.IO) {
        if (hasValidApiKey()) {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                val systemPrompt = """
                    You are Professor ${classContext.instructorName}, the AI Teaching Assistant and Instructor for the online class: "${classContext.title}".
                    Class Description: ${classContext.description}
                    Syllabus: $syllabusSummary
                    
                    Answer the student's question clearly, warmly, and helpfully with practical analogies, examples, or step-by-step guidance.
                    Keep answers structured, supportive, and under 3 paragraphs unless asked for detailed explanations.
                """.trimIndent()

                val requestJson = JSONObject().apply {
                    val contentsArray = JSONArray().apply {
                        put(JSONObject().apply {
                            put("role", "user")
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply { put("text", userQuestion) })
                            })
                        })
                    }
                    put("contents", contentsArray)
                    put("systemInstruction", JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", systemPrompt) })
                        })
                    })
                }

                val url = "$BASE_URL$MODEL_NAME:generateContent?key=$apiKey"
                val request = Request.Builder()
                    .url(url)
                    .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: ""
                if (response.isSuccessful && body.isNotBlank()) {
                    val root = JSONObject(body)
                    val candidates = root.optJSONArray("candidates")
                    val contentObj = candidates?.optJSONObject(0)?.optJSONObject("content")
                    val parts = contentObj?.optJSONArray("parts")
                    val text = parts?.optJSONObject(0)?.optString("text") ?: ""
                    if (text.isNotBlank()) return@withContext text
                }
            } catch (_: Exception) {
                // Fall back below
            }
        }

        // Contextual fallback response
        generateFallbackTutorResponse(classContext, userQuestion)
    }

    private fun parseGeneratedJson(
        jsonString: String,
        classId: String,
        userPrompt: String,
        level: String,
        duration: String
    ): GeneratedClassResult {
        val root = JSONObject(jsonString)
        val title = root.optString("title", userPrompt.capitalizeWords())
        val domain = root.optString("domain", detectDomain(userPrompt))
        val tagline = root.optString("tagline", "Master $title with structured hands-on guidance.")
        val description = root.optString("description", "A transformative online course designed to take you from foundational understanding to deep mastery.")
        val instructorName = root.optString("instructorName", "Dr. " + userPrompt.take(5).capitalizeWords() + " Vance")
        val instructorTitle = root.optString("instructorTitle", "Distinguished Specialist & Instructor")

        val takeawaysJsonArray = root.optJSONArray("keyTakeaways") ?: JSONArray()
        val takeawaysList = mutableListOf<String>()
        for (i in 0 until takeawaysJsonArray.length()) {
            takeawaysList.add(takeawaysJsonArray.getString(i))
        }
        if (takeawaysList.isEmpty()) {
            takeawaysList.addAll(listOf(
                "Grasp fundamental principles and historical context",
                "Apply core methodologies through real-world scenarios",
                "Develop practical troubleshooting and intuition",
                "Execute an end-to-end milestone capstone project"
            ))
        }

        val classEntity = ClassEntity(
            id = classId,
            title = title,
            domain = domain,
            tagline = tagline,
            description = description,
            level = level,
            durationText = duration,
            instructorName = instructorName,
            instructorTitle = instructorTitle,
            instructorAvatarColor = pickAvatarColor(domain),
            keyTakeawaysJson = JSONArray(takeawaysList).toString(),
            isEnrolled = true,
            progressPercent = 0,
            rating = 4.95f,
            studentCount = (150..950).random(),
            bannerGradientStart = pickGradientStart(domain),
            bannerGradientEnd = pickGradientEnd(domain)
        )

        val modulesList = mutableListOf<ModuleEntity>()
        val modulesArray = root.optJSONArray("modules")
        if (modulesArray != null && modulesArray.length() > 0) {
            for (i in 0 until modulesArray.length()) {
                val modObj = modulesArray.getJSONObject(i)
                modulesList.add(
                    ModuleEntity(
                        id = "${classId}_mod_${i + 1}",
                        classId = classId,
                        orderIndex = i + 1,
                        title = modObj.optString("title", "Module ${i + 1}"),
                        summary = modObj.optString("summary", "Key concepts and breakdown of core ideas."),
                        durationMinutes = modObj.optInt("durationMinutes", 45),
                        lessonContentMarkdown = modObj.optString("lessonContentMarkdown", "Lesson overview and lecture notes for ${modObj.optString("title")}."),
                        isCompleted = false
                    )
                )
            }
        }

        val quizzesList = mutableListOf<QuizQuestionEntity>()
        val quizzesArray = root.optJSONArray("quizzes")
        if (quizzesArray != null && quizzesArray.length() > 0) {
            for (i in 0 until quizzesArray.length()) {
                val qObj = quizzesArray.getJSONObject(i)
                val optsArray = qObj.optJSONArray("options") ?: JSONArray()
                val optsList = mutableListOf<String>()
                for (j in 0 until optsArray.length()) {
                    optsList.add(optsArray.getString(j))
                }
                quizzesList.add(
                    QuizQuestionEntity(
                        id = "${classId}_quiz_${i + 1}",
                        classId = classId,
                        question = qObj.optString("question", "What is a primary principle of $title?"),
                        optionsJson = JSONArray(optsList).toString(),
                        correctIndex = qObj.optInt("correctIndex", 0),
                        explanation = qObj.optString("explanation", "This principle underpins key operations in this discipline.")
                    )
                )
            }
        }

        val flashcardsList = mutableListOf<FlashcardEntity>()
        val flashcardsArray = root.optJSONArray("flashcards")
        if (flashcardsArray != null && flashcardsArray.length() > 0) {
            for (i in 0 until flashcardsArray.length()) {
                val fcObj = flashcardsArray.getJSONObject(i)
                flashcardsList.add(
                    FlashcardEntity(
                        id = "${classId}_fc_${i + 1}",
                        classId = classId,
                        frontTerm = fcObj.optString("front", "Core Term ${i + 1}"),
                        backDefinition = fcObj.optString("back", "Key definition and practical application.")
                    )
                )
            }
        }

        val roster = generateCohortRoster(classId)

        return GeneratedClassResult(classEntity, modulesList, quizzesList, flashcardsList, roster)
    }

    private fun generateProceduralClass(
        classId: String,
        userPrompt: String,
        targetLevel: String,
        durationChoice: String,
        audience: String,
        tone: String
    ): GeneratedClassResult {
        val cleanTitle = userPrompt.trim().capitalizeWords()
        val domain = detectDomain(userPrompt)
        val instructorName = generateInstructorName(domain)
        val instructorTitle = "Master Fellow & Lead Educator in $domain"

        val takeaways = listOf(
            "Foundations, mental models, and terminology of $cleanTitle",
            "Core mechanics, step-by-step methodologies, and workflow execution",
            "Advanced troubleshooting, common pitfalls, and edge cases",
            "Real-world capstone implementation and sustained mastery habits"
        )

        val classEntity = ClassEntity(
            id = classId,
            title = cleanTitle,
            domain = domain,
            tagline = "The definitive online class on $cleanTitle tailored for $audience.",
            description = """
                Welcome to $cleanTitle. This online class synthesizes years of practical experience, academic research, and real-world case studies into a progressive, step-by-step learning journey.
                
                Whether your goal is professional advancement, personal creative fulfillment, or deep theoretical understanding, each module guides you with clear explanations, practical challenges, interactive quizzes, and instant AI tutor support.
            """.trimIndent(),
            level = targetLevel,
            durationText = durationChoice,
            instructorName = instructorName,
            instructorTitle = instructorTitle,
            instructorAvatarColor = pickAvatarColor(domain),
            keyTakeawaysJson = JSONArray(takeaways).toString(),
            isEnrolled = true,
            progressPercent = 0,
            rating = 4.96f,
            studentCount = (240..1100).random(),
            bannerGradientStart = pickGradientStart(domain),
            bannerGradientEnd = pickGradientEnd(domain)
        )

        val modules = listOf(
            ModuleEntity(
                id = "${classId}_mod_1",
                classId = classId,
                orderIndex = 1,
                title = "Unit 1: Core Fundamentals & Principles",
                summary = "Establish foundational vocabulary, key rules, and essential mechanics of $cleanTitle.",
                durationMinutes = 40,
                lessonContentMarkdown = """
# Unit 1: Foundations of $cleanTitle

Welcome to the opening unit of **$cleanTitle**. Every great practitioner begins by understanding the foundational mechanics rather than merely memorizing recipes or shortcuts.

### 1. The Core Philosophy
To master $cleanTitle, you must cultivate a solid mental model. Consider how experts break down problems:
- **First Principles**: Isolate the irreducible components.
- **Feedback Loops**: Observe the immediate consequences of each technique.
- **Deliberate Repetition**: Practice with high intent over short, focused intervals.

### 2. Essential Vocabulary & Frameworks
In this discipline, precision of thought translates into precision of execution. Familiarize yourself with the core tenets outlined in our syllabus, and note how small adjustments in technique lead to exponential quality gains.

### Practical Exercise:
Take 10 minutes to formulate a personal learning goal for this class. Write down the single biggest question you have about $cleanTitle, then ask our AI Tutor to break it down!
                """.trimIndent(),
                isCompleted = false
            ),
            ModuleEntity(
                id = "${classId}_mod_2",
                classId = classId,
                orderIndex = 2,
                title = "Unit 2: Hands-On Application & Methodology",
                summary = "Deep dive into real-world execution, step-by-step procedures, and case studies.",
                durationMinutes = 55,
                lessonContentMarkdown = """
# Unit 2: Hands-On Execution in $cleanTitle

Theory without practice is inert. In this unit, we move from conceptual understanding to active implementation.

### 1. Step-by-Step Execution Workflow
1. **Preparation**: Gather all prerequisite tools, resources, and data points.
2. **Execution**: Apply the standard protocol methodically without rushing the intermediate stages.
3. **Assessment**: Inspect your output against the benchmark standards provided in the class rubric.

### 2. Common Pitfalls to Avoid:
- **Rushing the initial setup**: Over 80% of errors in $cleanTitle stem from inadequate preparation.
- **Ignoring early feedback**: Address minor discrepancies immediately before they compound into systemic flaws.
- **Overcomplicating the solution**: Seek elegant simplicity rather than unnecessary complexity.
                """.trimIndent(),
                isCompleted = false
            ),
            ModuleEntity(
                id = "${classId}_mod_3",
                classId = classId,
                orderIndex = 3,
                title = "Unit 3: Advanced Optimization & Capstone Project",
                summary = "Tackle complex scenarios, fine-tune efficiency, and construct your final showcase piece.",
                durationMinutes = 60,
                lessonContentMarkdown = """
# Unit 3: Mastery & Capstone Project

In this final unit, you integrate all prior learnings to produce a capstone project demonstrating comprehensive proficiency in $cleanTitle.

### Capstone Brief:
Design and document an end-to-end implementation applying the concepts explored across Units 1 and 2. 
- State your problem hypothesis clearly.
- Detail the methodology and tools utilized.
- Present your finished result and perform a retrospective evaluation.

### Ongoing Growth:
True mastery in $cleanTitle is an iterative journey. Join the student discussion forum, review peer submissions, and test your retention with our interactive quiz!
                """.trimIndent(),
                isCompleted = false
            )
        )

        val quizzes = listOf(
            QuizQuestionEntity(
                id = "${classId}_q1",
                classId = classId,
                question = "What is the most effective approach when beginning a new project in $cleanTitle?",
                optionsJson = JSONArray(listOf(
                    "Break down the challenge to first principles and prepare meticulously",
                    "Skip foundational setup to produce rapid drafts",
                    "Rely exclusively on intuition without establishing metrics",
                    "Copy existing templates without evaluating underlying mechanics"
                )).toString(),
                correctIndex = 0,
                explanation = "Meticulous preparation and understanding first principles prevents over 80% of downstream errors."
            ),
            QuizQuestionEntity(
                id = "${classId}_q2",
                classId = classId,
                question = "How should intermediate feedback be handled during execution?",
                optionsJson = JSONArray(listOf(
                    "Ignore it until the entire project is completed",
                    "Adjust promptly to prevent minor variance from compounding",
                    "Restart the entire class from the beginning",
                    "Assume feedback indicates an unsolvable constraint"
                )).toString(),
                correctIndex = 1,
                explanation = "Addressing variances early ensures high alignment with your final quality targets."
            )
        )

        val flashcards = listOf(
            FlashcardEntity(
                id = "${classId}_fc1",
                classId = classId,
                frontTerm = "First Principles Thinking",
                backDefinition = "The act of boiling a problem down to its most fundamental truths and reasoning upward from there."
            ),
            FlashcardEntity(
                id = "${classId}_fc2",
                classId = classId,
                frontTerm = "Deliberate Practice",
                backDefinition = "Focused practice designed systematically to improve performance, typically targeting specific weaknesses with immediate feedback."
            ),
            FlashcardEntity(
                id = "${classId}_fc3",
                classId = classId,
                frontTerm = "Iteration Velocity",
                backDefinition = "The speed at which you can design, test, analyze feedback, and implement adjustments in $cleanTitle."
            )
        )

        val roster = generateCohortRoster(classId)

        return GeneratedClassResult(classEntity, modules, quizzes, flashcards, roster)
    }

    private fun generateFallbackTutorResponse(classContext: ClassEntity, question: String): String {
        val qLower = question.lowercase()
        return when {
            qLower.contains("quiz") || qLower.contains("test") -> {
                "Here is a quick practice question for you on **${classContext.title}**:\n\n*Question:* What is the single most critical factor for success when executing the core techniques in this class?\n\n*Hint:* Focus on consistency, preparation, and deliberate feedback loops! Try answering, and I'll grade your response."
            }
            qLower.contains("simple") || qLower.contains("explain like") || qLower.contains("eli5") -> {
                "Imagine you're building a grand Lego castle. In **${classContext.title}**, you can't just dump all the pieces together and expect walls to stand. First, you build a super strong flat foundation (Unit 1), then you snap the main walls together securely (Unit 2), and finally you add the flags and towers (Unit 3). That's how we master this step by step!"
            }
            qLower.contains("summary") || qLower.contains("overview") -> {
                "**${classContext.title}** is organized into 3 sequential units:\n1. Foundations & First Principles\n2. Hands-on Execution & Core Protocols\n3. Advanced Mastery & Capstone Portfolio\n\nEach unit comes with readings, quizzes, and flashcards to cement your understanding."
            }
            else -> {
                "That's a thoughtful question regarding **${classContext.title}**! In this subject, always start by grounding yourself in the fundamentals outlined in Unit 1. Pay close attention to how small details compound into mastery, and don't hesitate to test your understanding using our interactive quizzes."
            }
        }
    }

    private fun generateCohortRoster(classId: String): List<StudentRosterEntity> {
        val names = listOf(
            Pair("Elena Rostova", "#3B82F6"),
            Pair("Marcus Vance", "#10B981"),
            Pair("Amina Zahra", "#F59E0B"),
            Pair("David Park", "#8B5CF6"),
            Pair("Sophia Chen", "#EC4899")
        )
        return names.mapIndexed { idx, pair ->
            StudentRosterEntity(
                id = "${classId}_st_${idx + 1}",
                classId = classId,
                name = pair.first,
                email = "${pair.first.lowercase().replace(" ", ".")}@omniclass.edu",
                avatarColorHex = pair.second,
                gradePercent = (85..99).random(),
                assignmentStatus = if (idx % 2 == 0) "Graded" else "Submitted"
            )
        }
    }

    private fun detectDomain(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("code") || lower.contains("python") || lower.contains("android") || lower.contains("web") || lower.contains("software") || lower.contains("ai") || lower.contains("rust") -> "Tech & Code"
            lower.contains("bread") || lower.contains("cook") || lower.contains("bake") || lower.contains("art") || lower.contains("craft") || lower.contains("paint") || lower.contains("photo") -> "Arts & Craft"
            lower.contains("neuro") || lower.contains("physics") || lower.contains("math") || lower.contains("science") || lower.contains("history") || lower.contains("biology") -> "Science & Academic"
            lower.contains("yoga") || lower.contains("fitness") || lower.contains("workout") || lower.contains("health") || lower.contains("muscle") || lower.contains("diet") -> "Health & Fitness"
            lower.contains("business") || lower.contains("finance") || lower.contains("marketing") || lower.contains("startup") || lower.contains("money") -> "Business & Leadership"
            else -> "Open Mastery"
        }
    }

    private fun generateInstructorName(domain: String): String = when (domain) {
        "Tech & Code" -> "Dr. Maya Lin"
        "Arts & Craft" -> "Chef Julian Mercier"
        "Science & Academic" -> "Prof. Elena Rostova"
        "Health & Fitness" -> "Coach Marcus Vance"
        "Business & Leadership" -> "Sarah Sterling, MBA"
        else -> "Dr. Arthur Vance"
    }

    private fun pickAvatarColor(domain: String): String = when (domain) {
        "Tech & Code" -> "#4F46E5"
        "Arts & Craft" -> "#D97706"
        "Science & Academic" -> "#059669"
        "Health & Fitness" -> "#DC2626"
        "Business & Leadership" -> "#2563EB"
        else -> "#7C3AED"
    }

    private fun pickGradientStart(domain: String): String = when (domain) {
        "Tech & Code" -> "#312E81"
        "Arts & Craft" -> "#78350F"
        "Science & Academic" -> "#064E3B"
        "Health & Fitness" -> "#7F1D1D"
        "Business & Leadership" -> "#1E3A8A"
        else -> "#581C87"
    }

    private fun pickGradientEnd(domain: String): String = when (domain) {
        "Tech & Code" -> "#1E1B4B"
        "Arts & Craft" -> "#451A03"
        "Science & Academic" -> "#022C22"
        "Health & Fitness" -> "#450A0A"
        "Business & Leadership" -> "#0F172A"
        else -> "#3B0764"
    }

    private fun String.capitalizeWords(): String =
        split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
}
