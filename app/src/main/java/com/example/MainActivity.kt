package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppScreen
import com.example.ui.AuthScreenMode
import com.example.ui.OmniClassViewModel
import com.example.ui.components.WebExportDialog
import com.example.ui.components.WebHeader
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ClassroomPortalScreen
import com.example.ui.screens.CourseLandingScreen
import com.example.ui.screens.CreateClassStudioScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.theme.OmniClassTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OmniClassTheme {
                OmniClassApp()
            }
        }
    }
}

@Composable
fun OmniClassApp(viewModel: OmniClassViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activeUser by viewModel.activeUser.collectAsStateWithLifecycle()
    val allClasses by viewModel.allClasses.collectAsStateWithLifecycle()
    val enrolledClasses by viewModel.enrolledClasses.collectAsStateWithLifecycle()

    val activeModules by viewModel.activeModules.collectAsStateWithLifecycle()
    val activeQuizzes by viewModel.activeQuizzes.collectAsStateWithLifecycle()
    val activeFlashcards by viewModel.activeFlashcards.collectAsStateWithLifecycle()
    val activeRoster by viewModel.activeRoster.collectAsStateWithLifecycle()
    val activeChatMessages by viewModel.activeChatMessages.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Toast notification bridge
    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissToast()
        }
    }

    val activeCourse = allClasses.find { it.id == uiState.activeCourseId }

    // Back handling
    BackHandler(enabled = uiState.currentScreen != AppScreen.DASHBOARD) {
        when (uiState.currentScreen) {
            AppScreen.CLASSROOM_LMS -> viewModel.navigateTo(AppScreen.CLASS_LANDING)
            AppScreen.CLASS_LANDING -> viewModel.navigateTo(AppScreen.DASHBOARD)
            AppScreen.CREATE_STUDIO -> viewModel.navigateTo(AppScreen.DASHBOARD)
            AppScreen.AUTH -> viewModel.navigateTo(AppScreen.DASHBOARD)
            else -> {}
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isTablet = maxWidth >= 700.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                if (uiState.currentScreen != AppScreen.AUTH) {
                    WebHeader(
                        activeUser = activeUser,
                        currentScreen = uiState.currentScreen,
                        courseTitle = activeCourse?.title,
                        onNavigateHome = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                        onNavigateCreate = { viewModel.navigateTo(AppScreen.CREATE_STUDIO) },
                        onLoginClick = { viewModel.setAuthMode(AuthScreenMode.LOGIN) },
                        onLogoutClick = { viewModel.logout() },
                        onBackClick = if (uiState.currentScreen != AppScreen.DASHBOARD) {
                            {
                                when (uiState.currentScreen) {
                                    AppScreen.CLASSROOM_LMS -> viewModel.navigateTo(AppScreen.CLASS_LANDING)
                                    else -> viewModel.navigateTo(AppScreen.DASHBOARD)
                                }
                            }
                        } else null
                    )
                }
            },
            bottomBar = {
                // Bottom navigation bar for mobile
                if (!isTablet && uiState.currentScreen != AppScreen.AUTH && uiState.currentScreen != AppScreen.CLASSROOM_LMS) {
                    NavigationBar(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .testTag("mobile_bottom_nav"),
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        NavigationBarItem(
                            selected = uiState.currentScreen == AppScreen.DASHBOARD,
                            onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Courses") },
                            label = { Text("Courses") }
                        )
                        NavigationBarItem(
                            selected = uiState.currentScreen == AppScreen.CREATE_STUDIO,
                            onClick = { viewModel.navigateTo(AppScreen.CREATE_STUDIO) },
                            icon = { Icon(Icons.Default.AddCircle, contentDescription = "Create Class") },
                            label = { Text("AI Studio") }
                        )
                        NavigationBarItem(
                            selected = false,
                            onClick = {
                                if (activeCourse != null) {
                                    viewModel.exportCourseAsWebsite(activeCourse.id)
                                } else if (allClasses.isNotEmpty()) {
                                    viewModel.exportCourseAsWebsite(allClasses.first().id)
                                }
                            },
                            icon = { Icon(Icons.Default.Language, contentDescription = "Export Web") },
                            label = { Text("Web Export") }
                        )
                        NavigationBarItem(
                            selected = false,
                            onClick = {
                                if (activeUser != null) {
                                    viewModel.logout()
                                } else {
                                    viewModel.setAuthMode(AuthScreenMode.LOGIN)
                                }
                            },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Account") },
                            label = { Text(if (activeUser != null) "Logout" else "Sign In") }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Tablet Navigation Rail
                if (isTablet && uiState.currentScreen != AppScreen.AUTH) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .fillMaxHeight()
                            .testTag("tablet_nav_rail")
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        NavigationRailItem(
                            selected = uiState.currentScreen == AppScreen.DASHBOARD,
                            onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Courses") },
                            label = { Text("Catalog") }
                        )
                        NavigationRailItem(
                            selected = uiState.currentScreen == AppScreen.CREATE_STUDIO,
                            onClick = { viewModel.navigateTo(AppScreen.CREATE_STUDIO) },
                            icon = { Icon(Icons.Default.AddCircle, contentDescription = "AI Studio") },
                            label = { Text("AI Create") }
                        )
                        NavigationRailItem(
                            selected = false,
                            onClick = {
                                if (activeCourse != null) {
                                    viewModel.exportCourseAsWebsite(activeCourse.id)
                                } else if (allClasses.isNotEmpty()) {
                                    viewModel.exportCourseAsWebsite(allClasses.first().id)
                                }
                            },
                            icon = { Icon(Icons.Default.Language, contentDescription = "Web Site") },
                            label = { Text("Export") }
                        )
                    }
                }

                // Main Content View
                Box(modifier = Modifier.weight(1f)) {
                    when (uiState.currentScreen) {
                        AppScreen.AUTH -> {
                            AuthScreen(
                                currentMode = uiState.authMode,
                                onModeChange = { viewModel.setAuthMode(it) },
                                onLogin = { email, pass -> viewModel.login(email, pass) },
                                onSignUp = { name, email, pass, role -> viewModel.signUp(name, email, pass, role) },
                                onForgotPassword = { email, newPass -> viewModel.forgotPassword(email, newPass) },
                                onDemoLogin = { role -> viewModel.demoLogin(role) },
                                onContinueAsGuest = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                            )
                        }
                        AppScreen.DASHBOARD -> {
                            DashboardScreen(
                                allClasses = allClasses,
                                enrolledClasses = enrolledClasses,
                                selectedDomain = uiState.selectedDomainFilter,
                                searchQuery = uiState.searchQuery,
                                onDomainSelect = { viewModel.setDomainFilter(it) },
                                onSearchChange = { viewModel.setSearchQuery(it) },
                                onSelectCourse = { courseId, directToClassroom ->
                                    viewModel.selectCourse(courseId, directToClassroom)
                                },
                                onToggleEnrollment = { viewModel.toggleEnrollment(it) },
                                onOpenCreateStudio = { viewModel.navigateTo(AppScreen.CREATE_STUDIO) },
                                onQuickGenerate = { prompt ->
                                    viewModel.generateAnyClass(prompt, "All Levels", "4 Weeks", "General Audience", "Practical Hands-on Bootcamp")
                                }
                            )
                        }
                        AppScreen.CLASS_LANDING -> {
                            if (activeCourse != null) {
                                CourseLandingScreen(
                                    course = activeCourse,
                                    modules = activeModules,
                                    onEnterClassroom = { viewModel.navigateTo(AppScreen.CLASSROOM_LMS) },
                                    onToggleEnrollment = { viewModel.toggleEnrollment(activeCourse.id) },
                                    onExportWebsite = { viewModel.exportCourseAsWebsite(activeCourse.id) }
                                )
                            }
                        }
                        AppScreen.CLASSROOM_LMS -> {
                            if (activeCourse != null) {
                                ClassroomPortalScreen(
                                    course = activeCourse,
                                    modules = activeModules,
                                    selectedModuleId = uiState.selectedModuleId,
                                    currentTab = uiState.classroomTab,
                                    quizzes = activeQuizzes,
                                    flashcards = activeFlashcards,
                                    roster = activeRoster,
                                    chatMessages = activeChatMessages,
                                    isTutorTyping = uiState.isTutorTyping,
                                    quizAnswers = uiState.quizAnswers,
                                    isQuizSubmitted = uiState.isQuizSubmitted,
                                    onTabSelect = { viewModel.setClassroomTab(it) },
                                    onSelectModule = { viewModel.selectModule(it) },
                                    onToggleModuleComplete = { viewModel.toggleModuleCompletion(it) },
                                    onSelectQuizAnswer = { qId, optIdx -> viewModel.selectQuizAnswer(qId, optIdx) },
                                    onSubmitQuiz = { viewModel.submitQuiz() },
                                    onResetQuiz = { viewModel.resetQuiz() },
                                    onSendTutorMessage = { viewModel.sendTutorMessage(it) },
                                    onExportWebsite = { viewModel.exportCourseAsWebsite(activeCourse.id) }
                                )
                            }
                        }
                        AppScreen.CREATE_STUDIO -> {
                            CreateClassStudioScreen(
                                generationStatus = uiState.generationStatus,
                                onGenerate = { prompt, level, dur, aud, tone ->
                                    viewModel.generateAnyClass(prompt, level, dur, aud, tone)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Standalone Web Site Export Dialog
        if (uiState.showExportDialog && uiState.exportedHtml != null && activeCourse != null) {
            WebExportDialog(
                htmlContent = uiState.exportedHtml!!,
                courseTitle = activeCourse.title,
                onDismiss = { viewModel.closeExportDialog() }
            )
        }
    }
}
