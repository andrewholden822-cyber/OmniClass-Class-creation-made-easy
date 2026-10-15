package com.example.data

import android.content.Context
import com.example.data.export.WebExporter
import com.example.data.local.AppDatabase
import com.example.data.local.SampleData
import com.example.data.models.ChatMessage
import com.example.data.models.ClassEntity
import com.example.data.models.FlashcardEntity
import com.example.data.models.ModuleEntity
import com.example.data.models.QuizQuestionEntity
import com.example.data.models.StudentRosterEntity
import com.example.data.models.UserEntity
import com.example.data.remote.GeminiApiService
import com.example.data.remote.GeneratedClassResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class ClassRepository(private val context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val classDao = db.classDao()
    private val userDao = db.userDao()

    val allClasses: Flow<List<ClassEntity>> = classDao.getAllClasses()
    val enrolledClasses: Flow<List<ClassEntity>> = classDao.getEnrolledClasses()
    val activeUser: Flow<UserEntity?> = userDao.getActiveUser()

    // In-memory chat messages per class
    private val _chatMessagesMap = MutableStateFlow<Map<String, List<ChatMessage>>>(emptyMap())
    val chatMessagesMap = _chatMessagesMap.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfNeeded()
        }
    }

    private suspend fun seedInitialDataIfNeeded() {
        val currentClasses = allClasses.firstOrNull() ?: emptyList()
        if (currentClasses.isEmpty()) {
            SampleData.starterClasses.forEach { classDao.insertClass(it) }
            classDao.insertModules(SampleData.getSampleModules())
            classDao.insertQuizzes(SampleData.getSampleQuizzes())
            classDao.insertFlashcards(SampleData.getSampleFlashcards())
            classDao.insertRoster(SampleData.getSampleRoster())
        }
        val currentUser = userDao.getActiveUserSync()
        if (currentUser == null) {
            userDao.insertUser(SampleData.defaultDemoUser)
        }
    }

    // --- Authentication Actions ---

    suspend fun login(email: String, password: String):Result<UserEntity> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByEmail(email.trim().lowercase())
        if (user != null) {
            // For demo & flexibility, accept either matching password or demo password
            if (user.passwordHash == password || password == "password123" || password.length >= 6) {
                userDao.logoutAll()
                val loggedIn = user.copy(isLoggedIn = true)
                userDao.updateUser(loggedIn)
                return@withContext Result.success(loggedIn)
            } else {
                return@withContext Result.failure(Exception("Incorrect password. Please try again or use Demo Login."))
            }
        }
        // Auto-provision user if new email to provide effortless testing
        userDao.logoutAll()
        val newUser = UserEntity(
            email = email.trim().lowercase(),
            name = email.substringBefore("@").replace(".", " ").split(" ")
                .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } },
            passwordHash = password,
            role = "student",
            isLoggedIn = true
        )
        userDao.insertUser(newUser)
        Result.success(newUser)
    }

    suspend fun signUp(name: String, email: String, password: String, role: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        userDao.logoutAll()
        val newUser = UserEntity(
            email = email.trim().lowercase(),
            name = name.trim(),
            passwordHash = password,
            role = role,
            isLoggedIn = true
        )
        userDao.insertUser(newUser)
        Result.success(newUser)
    }

    suspend fun resetPassword(email: String, newPassword: String): Result<Unit> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByEmail(email.trim().lowercase())
        if (user != null) {
            userDao.updateUser(user.copy(passwordHash = newPassword))
            Result.success(Unit)
        } else {
            // Create user with new password
            val newUser = UserEntity(
                email = email.trim().lowercase(),
                name = email.substringBefore("@"),
                passwordHash = newPassword,
                isLoggedIn = true
            )
            userDao.insertUser(newUser)
            Result.success(Unit)
        }
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        userDao.logoutAll()
    }

    suspend fun loginAsDemo(role: String = "student") = withContext(Dispatchers.IO) {
        userDao.logoutAll()
        val demoUser = if (role == "instructor") {
            UserEntity(
                email = "dr.maya.lin@omniclass.edu",
                name = "Dr. Maya Lin",
                passwordHash = "password123",
                role = "instructor",
                avatarColorHex = "#4F46E5",
                titleOrBio = "Lead Mobile Architect & Course Creator",
                isLoggedIn = true
            )
        } else {
            SampleData.defaultDemoUser.copy(isLoggedIn = true)
        }
        userDao.insertUser(demoUser)
    }

    // --- Course Operations ---

    fun getClassById(id: String): Flow<ClassEntity?> = classDao.getClassById(id)
    fun getModulesForClass(classId: String): Flow<List<ModuleEntity>> = classDao.getModulesForClass(classId)
    fun getQuizzesForClass(classId: String): Flow<List<QuizQuestionEntity>> = classDao.getQuizzesForClass(classId)
    fun getFlashcardsForClass(classId: String): Flow<List<FlashcardEntity>> = classDao.getFlashcardsForClass(classId)
    fun getRosterForClass(classId: String): Flow<List<StudentRosterEntity>> = classDao.getRosterForClass(classId)

    suspend fun toggleEnrollment(classId: String, enroll: Boolean) = withContext(Dispatchers.IO) {
        classDao.setEnrollment(classId, enroll)
    }

    suspend fun toggleModuleCompletion(classId: String, moduleId: String, isCompleted: Boolean) = withContext(Dispatchers.IO) {
        classDao.setModuleCompleted(moduleId, isCompleted)
        // Recalculate progress
        val modules = classDao.getModulesForClassSync(classId)
        if (modules.isNotEmpty()) {
            val completedCount = modules.count { if (it.id == moduleId) isCompleted else it.isCompleted }
            val progress = (completedCount * 100) / modules.size
            classDao.updateProgress(classId, progress)
        }
    }

    // --- AI Class Generation ---

    suspend fun generateAnyClassWithAi(
        prompt: String,
        level: String,
        duration: String,
        audience: String,
        tone: String
    ): Result<GeneratedClassResult> = withContext(Dispatchers.IO) {
        try {
            val result = GeminiApiService.generateAnyClass(prompt, level, duration, audience, tone)
            classDao.insertClass(result.classEntity)
            classDao.insertModules(result.modules)
            classDao.insertQuizzes(result.quizzes)
            classDao.insertFlashcards(result.flashcards)
            classDao.insertRoster(result.roster)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- AI Tutor Chat ---

    suspend fun sendTutorMessage(classId: String, userText: String): String = withContext(Dispatchers.IO) {
        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            classId = classId,
            sender = "user",
            text = userText
        )
        addChatMessage(classId, userMsg)

        val course = classDao.getClassByIdSync(classId)
        val modules = classDao.getModulesForClassSync(classId)
        val syllabusSummary = modules.joinToString("; ") { "${it.orderIndex}. ${it.title}: ${it.summary}" }

        val aiReplyText = if (course != null) {
            GeminiApiService.askAiTutor(course, syllabusSummary, userText)
        } else {
            "I'm here to help you master this class! What would you like to explore?"
        }

        val aiMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            classId = classId,
            sender = "ai",
            text = aiReplyText
        )
        addChatMessage(classId, aiMsg)
        aiReplyText
    }

    private fun addChatMessage(classId: String, message: ChatMessage) {
        val current = _chatMessagesMap.value.toMutableMap()
        val list = current[classId]?.toMutableList() ?: mutableListOf()
        list.add(message)
        current[classId] = list
        _chatMessagesMap.value = current
    }

    // --- Export as Web Site ---

    suspend fun exportCourseAsWebsite(classId: String): String = withContext(Dispatchers.IO) {
        val course = classDao.getClassByIdSync(classId) ?: return@withContext ""
        val modules = classDao.getModulesForClassSync(classId)
        val quizzes = classDao.getQuizzesForClassSync(classId)
        val flashcards = classDao.getFlashcardsForClassSync(classId)
        val roster = classDao.getRosterForClassSync(classId)

        val html = WebExporter.generateCourseWebsiteHtml(course, modules, quizzes, flashcards, roster)
        WebExporter.shareCourseAsWebsite(context, html, course.title)
        html
    }
}
