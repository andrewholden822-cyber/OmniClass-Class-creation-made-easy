package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.TextButton
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ChatMessage
import com.example.data.models.ClassEntity
import com.example.data.models.FlashcardEntity
import com.example.data.models.ModuleEntity
import com.example.data.models.QuizQuestionEntity
import com.example.data.models.StudentRosterEntity
import com.example.ui.ClassroomTab

@Composable
fun ClassroomPortalScreen(
    course: ClassEntity,
    modules: List<ModuleEntity>,
    selectedModuleId: String?,
    currentTab: ClassroomTab,
    quizzes: List<QuizQuestionEntity>,
    flashcards: List<FlashcardEntity>,
    roster: List<StudentRosterEntity>,
    chatMessages: List<ChatMessage>,
    isTutorTyping: Boolean,
    quizAnswers: Map<String, Int>,
    isQuizSubmitted: Boolean,
    onTabSelect: (ClassroomTab) -> Unit,
    onSelectModule: (String) -> Unit,
    onToggleModuleComplete: (String) -> Unit,
    onSelectQuizAnswer: (String, Int) -> Unit,
    onSubmitQuiz: () -> Unit,
    onResetQuiz: () -> Unit,
    onSendTutorMessage: (String) -> Unit,
    onExportWebsite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // LMS Navigation Tabs
        ScrollableTabRow(
            selectedTabIndex = currentTab.ordinal,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = currentTab == ClassroomTab.SYLLABUS,
                onClick = { onTabSelect(ClassroomTab.SYLLABUS) },
                text = { Text("Lessons (${modules.size})") },
                icon = { Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_lessons")
            )
            Tab(
                selected = currentTab == ClassroomTab.QUIZ,
                onClick = { onTabSelect(ClassroomTab.QUIZ) },
                text = { Text("Quizzes (${quizzes.size})") },
                icon = { Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_quizzes")
            )
            Tab(
                selected = currentTab == ClassroomTab.FLASHCARDS,
                onClick = { onTabSelect(ClassroomTab.FLASHCARDS) },
                text = { Text("Cards (${flashcards.size})") },
                icon = { Icon(Icons.Default.ListAlt, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_flashcards")
            )
            Tab(
                selected = currentTab == ClassroomTab.ROSTER,
                onClick = { onTabSelect(ClassroomTab.ROSTER) },
                text = { Text("Roster") },
                icon = { Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_roster")
            )
            Tab(
                selected = currentTab == ClassroomTab.AI_TUTOR,
                onClick = { onTabSelect(ClassroomTab.AI_TUTOR) },
                text = { Text("AI Tutor") },
                icon = { Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_tutor")
            )
        }

        // Active Tab Content
        Box(modifier = Modifier.weight(1f)) {
            when (currentTab) {
                ClassroomTab.SYLLABUS -> {
                    SyllabusLessonReaderTab(
                        modules = modules,
                        selectedModuleId = selectedModuleId,
                        onSelectModule = onSelectModule,
                        onToggleComplete = onToggleModuleComplete,
                        onExportWebsite = onExportWebsite
                    )
                }
                ClassroomTab.QUIZ -> {
                    QuizPlayerTab(
                        quizzes = quizzes,
                        userAnswers = quizAnswers,
                        isSubmitted = isQuizSubmitted,
                        onSelectAnswer = onSelectQuizAnswer,
                        onSubmit = onSubmitQuiz,
                        onReset = onResetQuiz
                    )
                }
                ClassroomTab.FLASHCARDS -> {
                    FlashcardsDeckTab(flashcards = flashcards)
                }
                ClassroomTab.ROSTER -> {
                    RosterTab(roster = roster)
                }
                ClassroomTab.AI_TUTOR -> {
                    AiTutorChatTab(
                        instructorName = course.instructorName,
                        chatMessages = chatMessages,
                        isTyping = isTutorTyping,
                        onSendMessage = onSendTutorMessage
                    )
                }
            }
        }
    }
}

@Composable
private fun SyllabusLessonReaderTab(
    modules: List<ModuleEntity>,
    selectedModuleId: String?,
    onSelectModule: (String) -> Unit,
    onToggleComplete: (String) -> Unit,
    onExportWebsite: () -> Unit
) {
    val activeModule = modules.find { it.id == selectedModuleId } ?: modules.firstOrNull()

    Column(modifier = Modifier.fillMaxSize()) {
        // Horizontal Module Selector
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                .padding(vertical = 10.dp, horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(modules) { mod ->
                val isSelected = mod.id == activeModule?.id
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .clickable { onSelectModule(mod.id) }
                        .testTag("module_chip_${mod.orderIndex}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (mod.isCompleted) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Completed", tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = "Unit ${mod.orderIndex}",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Selected Lesson Reader Pane
        if (activeModule != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Module ${activeModule.orderIndex}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = activeModule.title,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Button(
                        onClick = { onToggleComplete(activeModule.id) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (activeModule.isCompleted) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("mark_complete_button")
                    ) {
                        Icon(if (activeModule.isCompleted) Icons.Default.Check else Icons.Default.RadioButtonUnchecked, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (activeModule.isCompleted) "Completed" else "Complete")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(14.dp))

                // Lesson Content Markdown Formatter
                RenderMarkdownContent(activeModule.lessonContentMarkdown)

                Spacer(modifier = Modifier.height(30.dp))

                // Lesson Footer
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Estimated Time: ${activeModule.durationMinutes} mins",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedButton(
                            onClick = onExportWebsite,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export Web Page")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RenderMarkdownContent(markdown: String) {
    val lines = markdown.lines()
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        lines.forEach { line ->
            when {
                line.startsWith("# ") -> {
                    Text(
                        text = line.removePrefix("# "),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                    )
                }
                line.startsWith("## ") -> {
                    Text(
                        text = line.removePrefix("## "),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                    )
                }
                line.startsWith("### ") -> {
                    Text(
                        text = line.removePrefix("### "),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                line.startsWith("- ") || line.startsWith("* ") -> {
                    Row(modifier = Modifier.padding(start = 8.dp, top = 2.dp, bottom = 2.dp)) {
                        Text("• ", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Text(
                            text = line.substring(2),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                line.startsWith("```") -> {
                    // Code fence indicator
                }
                line.contains("val ") || line.contains("fun ") || line.contains("import ") || line.contains("class ") -> {
                    Surface(
                        color = Color(0xFF0F172A),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = line,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFA5B4FC),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
                line.isNotBlank() -> {
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun QuizPlayerTab(
    quizzes: List<QuizQuestionEntity>,
    userAnswers: Map<String, Int>,
    isSubmitted: Boolean,
    onSelectAnswer: (String, Int) -> Unit,
    onSubmit: () -> Unit,
    onReset: () -> Unit
) {
    if (quizzes.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No quizzes formulated for this course yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    val correctCount = quizzes.count { q -> userAnswers[q.id] == q.correctIndex }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (isSubmitted) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Quiz Results",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Score: $correctCount / ${quizzes.size} (${(correctCount * 100) / quizzes.size}%)",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(onClick = onReset) {
                            Text("Retake Quiz")
                        }
                    }
                }
            }
        }

        items(quizzes, key = { it.id }) { q ->
            val selectedOption = userAnswers[q.id]
            val optionsList = mutableListOf<String>()
            try {
                val arr = org.json.JSONArray(q.optionsJson)
                for (i in 0 until arr.length()) optionsList.add(arr.getString(i))
            } catch (_: Exception) {}

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = q.question,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    optionsList.forEachIndexed { optIdx, optText ->
                        val isChosen = selectedOption == optIdx
                        val isCorrect = optIdx == q.correctIndex

                        val bgColor = when {
                            !isSubmitted && isChosen -> MaterialTheme.colorScheme.primaryContainer
                            isSubmitted && isCorrect -> Color(0xFF10B981).copy(alpha = 0.2f)
                            isSubmitted && isChosen && !isCorrect -> Color(0xFFEF4444).copy(alpha = 0.2f)
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = bgColor,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isChosen) MaterialTheme.colorScheme.primary else Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable(enabled = !isSubmitted) { onSelectAnswer(q.id, optIdx) }
                                .testTag("quiz_${q.id}_option_$optIdx")
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${('A'.code + optIdx).toChar()}.",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = optText,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    if (isSubmitted) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Explanation: ${q.explanation}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        if (!isSubmitted) {
            item {
                Button(
                    onClick = onSubmit,
                    enabled = userAnswers.size == quizzes.size,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_quiz_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Submit Answers (${userAnswers.size}/${quizzes.size})", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun FlashcardsDeckTab(flashcards: List<FlashcardEntity>) {
    if (flashcards.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No flashcards for this course.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    val currentCard = flashcards.getOrNull(currentIndex) ?: flashcards.first()
    val rotation by animateFloatAsState(targetValue = if (isFlipped) 180f else 0f, label = "card_flip")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Card ${currentIndex + 1} of ${flashcards.size}",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(14.dp))

        // Flip Card Surface
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12f * density
                }
                .clickable { isFlipped = !isFlipped }
                .testTag("interactive_flashcard"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (rotation > 90f) Color(0xFF1E1B4B) else MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                if (rotation <= 90f) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "TERM",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = currentCard.frontTerm,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "(Tap to reveal definition)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.graphicsLayer { rotationY = 180f }
                    ) {
                        Text(
                            text = "DEFINITION",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFA5B4FC)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = currentCard.backDefinition,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFFE0E7FF),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Next / Previous Navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (currentIndex > 0) {
                        currentIndex--
                        isFlipped = false
                    }
                },
                enabled = currentIndex > 0
            ) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous")
            }

            TextButton(onClick = { isFlipped = !isFlipped }) {
                Text(if (isFlipped) "Show Term" else "Flip to Definition")
            }

            IconButton(
                onClick = {
                    if (currentIndex < flashcards.size - 1) {
                        currentIndex++
                        isFlipped = false
                    }
                },
                enabled = currentIndex < flashcards.size - 1
            ) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Next")
            }
        }
    }
}

@Composable
private fun RosterTab(roster: List<StudentRosterEntity>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Classroom Cohort & Gradebook",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Monitor peer progress, grades, and homework submissions.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(roster, key = { it.id }) { st ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(st.avatarColorHex))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = st.name.take(1),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(st.name, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                            Text(st.email, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${st.gradePercent}%",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = st.assignmentStatus,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (st.assignmentStatus == "Graded") Color(0xFF10B981) else Color(0xFFF59E0B)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AiTutorChatTab(
    instructorName: String,
    chatMessages: List<ChatMessage>,
    isTyping: Boolean,
    onSendMessage: (String) -> Unit
) {
    var userMessageText by remember { mutableStateOf("") }

    val quickPrompts = listOf(
        "Explain this concept simpler",
        "Give me a real-world example",
        "Ask me a practice question",
        "Summarize the main takeaways"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Chat Header
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "AI Instructor Assistant: $instructorName",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Ask questions, get feedback, and request study breakdowns.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Messages List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (chatMessages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Hello! I'm your AI Class Tutor.",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "What part of the class would you like to explore today?",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            items(chatMessages, key = { it.id }) { msg ->
                val isUser = msg.sender == "user"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 14.dp,
                            topEnd = 14.dp,
                            bottomStart = if (isUser) 14.dp else 2.dp,
                            bottomEnd = if (isUser) 2.dp else 14.dp
                        ),
                        color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.widthIn(max = 300.dp)
                    ) {
                        Text(
                            text = msg.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            if (isTyping) {
                item {
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tutor is thinking...",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Quick Suggestion Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickPrompts) { prompt ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable { onSendMessage(prompt) }
                ) {
                    Text(
                        text = prompt,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // Chat Input Row
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = userMessageText,
                    onValueChange = { userMessageText = it },
                    placeholder = { Text("Ask your AI tutor anything...") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("tutor_chat_input"),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (userMessageText.isNotBlank()) {
                            onSendMessage(userMessageText)
                            userMessageText = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .testTag("send_tutor_message_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
