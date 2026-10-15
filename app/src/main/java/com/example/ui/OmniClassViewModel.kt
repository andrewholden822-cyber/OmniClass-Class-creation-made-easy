package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ClassRepository
import com.example.data.models.ChatMessage
import com.example.data.models.ClassEntity
import com.example.data.models.FlashcardEntity
import com.example.data.models.ModuleEntity
import com.example.data.models.QuizQuestionEntity
import com.example.data.models.StudentRosterEntity
import com.example.data.models.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    AUTH,
    DASHBOARD,
    CLASS_LANDING,
    CLASSROOM_LMS,
    CREATE_STUDIO,
    LIVE_WEBSITE
}

enum class AuthScreenMode {
    LOGIN,
    SIGNUP,
    FORGOT_PASSWORD
}

enum class ClassroomTab {
    SYLLABUS,
    QUIZ,
    FLASHCARDS,
    ROSTER,
    AI_TUTOR
}

sealed class GenerationStatus {
    object Idle : GenerationStatus()
    data class Generating(val step: String) : GenerationStatus()
    data class Success(val createdClassId: String, val title: String) : GenerationStatus()
    data class Error(val message: String) : GenerationStatus()
}

data class UiState(
    val currentScreen: AppScreen = AppScreen.DASHBOARD,
    val authMode: AuthScreenMode = AuthScreenMode.LOGIN,
    val selectedDomainFilter: String = "All",
    val searchQuery: String = "",
    val activeCourseId: String? = null,
    val selectedModuleId: String? = null,
    val classroomTab: ClassroomTab = ClassroomTab.SYLLABUS,
    val generationStatus: GenerationStatus = GenerationStatus.Idle,
    val isTutorTyping: Boolean = false,
    val quizAnswers: Map<String, Int> = emptyMap(),
    val isQuizSubmitted: Boolean = false,
    val exportedHtml: String? = null,
    val activeWebsiteCourseTitle: String = "OmniClass Portal",
    val showExportDialog: Boolean = false,
    val toastMessage: String? = null
)

class OmniClassViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ClassRepository(application.applicationContext)

    val activeUser: StateFlow<UserEntity?> = repository.activeUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allClasses: StateFlow<List<ClassEntity>> = repository.allClasses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val enrolledClasses: StateFlow<List<ClassEntity>> = repository.enrolledClasses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Dynamic data for active course
    private val _activeModules = MutableStateFlow<List<ModuleEntity>>(emptyList())
    val activeModules: StateFlow<List<ModuleEntity>> = _activeModules.asStateFlow()

    private val _activeQuizzes = MutableStateFlow<List<QuizQuestionEntity>>(emptyList())
    val activeQuizzes: StateFlow<List<QuizQuestionEntity>> = _activeQuizzes.asStateFlow()

    private val _activeFlashcards = MutableStateFlow<List<FlashcardEntity>>(emptyList())
    val activeFlashcards: StateFlow<List<FlashcardEntity>> = _activeFlashcards.asStateFlow()

    private val _activeRoster = MutableStateFlow<List<StudentRosterEntity>>(emptyList())
    val activeRoster: StateFlow<List<StudentRosterEntity>> = _activeRoster.asStateFlow()

    private val _activeChatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val activeChatMessages: StateFlow<List<ChatMessage>> = _activeChatMessages.asStateFlow()

    // --- Navigation & Flow Controls ---

    fun navigateTo(screen: AppScreen) {
        _uiState.value = _uiState.value.copy(currentScreen = screen)
    }

    fun setAuthMode(mode: AuthScreenMode) {
        _uiState.value = _uiState.value.copy(authMode = mode, currentScreen = AppScreen.AUTH)
    }

    fun setDomainFilter(domain: String) {
        _uiState.value = _uiState.value.copy(selectedDomainFilter = domain)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun setClassroomTab(tab: ClassroomTab) {
        _uiState.value = _uiState.value.copy(classroomTab = tab)
    }

    fun selectModule(moduleId: String) {
        _uiState.value = _uiState.value.copy(selectedModuleId = moduleId)
    }

    fun selectCourse(courseId: String, openDirectToClassroom: Boolean = false) {
        _uiState.value = _uiState.value.copy(
            activeCourseId = courseId,
            currentScreen = if (openDirectToClassroom) AppScreen.CLASSROOM_LMS else AppScreen.CLASS_LANDING,
            classroomTab = ClassroomTab.SYLLABUS,
            selectedModuleId = null,
            quizAnswers = emptyMap(),
            isQuizSubmitted = false
        )
        loadActiveCourseData(courseId)
    }

    private fun loadActiveCourseData(courseId: String) {
        viewModelScope.launch {
            repository.getModulesForClass(courseId).collect { modules ->
                _activeModules.value = modules
                if (_uiState.value.selectedModuleId == null && modules.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(selectedModuleId = modules.first().id)
                }
            }
        }
        viewModelScope.launch {
            repository.getQuizzesForClass(courseId).collect { _activeQuizzes.value = it }
        }
        viewModelScope.launch {
            repository.getFlashcardsForClass(courseId).collect { _activeFlashcards.value = it }
        }
        viewModelScope.launch {
            repository.getRosterForClass(courseId).collect { _activeRoster.value = it }
        }
        viewModelScope.launch {
            repository.chatMessagesMap.collect { map ->
                _activeChatMessages.value = map[courseId] ?: emptyList()
            }
        }
    }

    // --- Auth Actions ---

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            val result = repository.login(email, pass)
            result.fold(
                onSuccess = { user ->
                    _uiState.value = _uiState.value.copy(
                        currentScreen = AppScreen.DASHBOARD,
                        toastMessage = "Welcome back, ${user.name}!"
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(toastMessage = error.message ?: "Login failed")
                }
            )
        }
    }

    fun signUp(name: String, email: String, pass: String, role: String) {
        viewModelScope.launch {
            val result = repository.signUp(name, email, pass, role)
            result.fold(
                onSuccess = { user ->
                    _uiState.value = _uiState.value.copy(
                        currentScreen = AppScreen.DASHBOARD,
                        toastMessage = "Account created! Welcome, ${user.name}."
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(toastMessage = error.message ?: "Sign up failed")
                }
            )
        }
    }

    fun forgotPassword(email: String, newPass: String) {
        viewModelScope.launch {
            repository.resetPassword(email, newPass)
            _uiState.value = _uiState.value.copy(
                authMode = AuthScreenMode.LOGIN,
                toastMessage = "Password updated! You can now log in."
            )
        }
    }

    fun demoLogin(role: String = "student") {
        viewModelScope.launch {
            repository.loginAsDemo(role)
            _uiState.value = _uiState.value.copy(
                currentScreen = AppScreen.DASHBOARD,
                toastMessage = "Logged in as Demo ${role.replaceFirstChar { it.uppercase() }}"
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _uiState.value = _uiState.value.copy(
                currentScreen = AppScreen.AUTH,
                authMode = AuthScreenMode.LOGIN,
                toastMessage = "Logged out successfully"
            )
        }
    }

    // --- Course Interactions ---

    fun toggleEnrollment(courseId: String) {
        val course = allClasses.value.find { it.id == courseId } ?: return
        val newEnrolled = !course.isEnrolled
        viewModelScope.launch {
            repository.toggleEnrollment(courseId, newEnrolled)
            _uiState.value = _uiState.value.copy(
                toastMessage = if (newEnrolled) "Enrolled in ${course.title}!" else "Unenrolled from course."
            )
        }
    }

    fun toggleModuleCompletion(moduleId: String) {
        val courseId = _uiState.value.activeCourseId ?: return
        val mod = _activeModules.value.find { it.id == moduleId } ?: return
        val newCompleted = !mod.isCompleted
        viewModelScope.launch {
            repository.toggleModuleCompletion(courseId, moduleId, newCompleted)
        }
    }

    // --- Quizzes ---

    fun selectQuizAnswer(questionId: String, optionIndex: Int) {
        if (_uiState.value.isQuizSubmitted) return
        val updated = _uiState.value.quizAnswers.toMutableMap()
        updated[questionId] = optionIndex
        _uiState.value = _uiState.value.copy(quizAnswers = updated)
    }

    fun submitQuiz() {
        _uiState.value = _uiState.value.copy(isQuizSubmitted = true)
    }

    fun resetQuiz() {
        _uiState.value = _uiState.value.copy(quizAnswers = emptyMap(), isQuizSubmitted = false)
    }

    // --- AI Class Generation ("ANY Class") ---

    fun generateAnyClass(
        prompt: String,
        level: String,
        duration: String,
        audience: String,
        tone: String
    ) {
        if (prompt.isBlank()) {
            _uiState.value = _uiState.value.copy(toastMessage = "Please describe the class you'd like to create.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                generationStatus = GenerationStatus.Generating("Consulting AI curriculum engine...")
            )

            // Dynamic progress indicators
            kotlinx.coroutines.delay(600)
            _uiState.value = _uiState.value.copy(
                generationStatus = GenerationStatus.Generating("Synthesizing modules, syllabus & learning outcomes...")
            )

            val result = repository.generateAnyClassWithAi(prompt, level, duration, audience, tone)
            result.fold(
                onSuccess = { created ->
                    _uiState.value = _uiState.value.copy(
                        generationStatus = GenerationStatus.Success(created.classEntity.id, created.classEntity.title),
                        toastMessage = "✨ '${created.classEntity.title}' class generated successfully!"
                    )
                    selectCourse(created.classEntity.id, openDirectToClassroom = true)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        generationStatus = GenerationStatus.Error(error.message ?: "Generation failed. Please try again.")
                    )
                }
            )
        }
    }

    fun resetGenerationStatus() {
        _uiState.value = _uiState.value.copy(generationStatus = GenerationStatus.Idle)
    }

    // --- AI Tutor Chat ---

    fun sendTutorMessage(text: String) {
        val courseId = _uiState.value.activeCourseId ?: return
        if (text.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isTutorTyping = true)
            repository.sendTutorMessage(courseId, text)
            _uiState.value = _uiState.value.copy(isTutorTyping = false)
        }
    }

    // --- Web Website Export ---

    fun exportCourseAsWebsite(courseId: String) {
        viewModelScope.launch {
            val html = repository.exportCourseAsWebsite(courseId)
            _uiState.value = _uiState.value.copy(
                exportedHtml = html,
                showExportDialog = true
            )
        }
    }

    fun openLiveWebsite(courseId: String? = null) {
        viewModelScope.launch {
            val targetId = courseId ?: _uiState.value.activeCourseId ?: allClasses.value.firstOrNull()?.id
            if (targetId != null) {
                val html = repository.exportCourseAsWebsite(targetId)
                val course = allClasses.value.find { it.id == targetId }
                _uiState.value = _uiState.value.copy(
                    currentScreen = AppScreen.LIVE_WEBSITE,
                    exportedHtml = html,
                    activeWebsiteCourseTitle = course?.title ?: "OmniClass Portal"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    currentScreen = AppScreen.LIVE_WEBSITE,
                    exportedHtml = null,
                    activeWebsiteCourseTitle = "OmniClass Portal"
                )
            }
        }
    }

    fun closeExportDialog() {
        _uiState.value = _uiState.value.copy(showExportDialog = false)
    }

    fun dismissToast() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }
}
