package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.*
import com.example.model.AppLanguage
import com.example.model.UserRole
import com.example.ui.components.SchoolBottomNavigation
import com.example.ui.components.SchoolTopAppBar
import com.example.ui.screens.*
import com.example.ui.theme.Blue800
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val appViewModel: AppViewModel = viewModel()
                MainAppContainer(appViewModel)
            }
        }
    }
}

@Composable
fun MainAppContainer(viewModel: AppViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val videos by viewModel.videos.collectAsState()
    val materials by viewModel.studyMaterials.collectAsState()

    // Navigation sub-screen states
    var activeSubScreen by remember { mutableStateOf<String?>(null) }
    var selectedSubjectDetail by remember { mutableStateOf<SubjectEntity?>(null) }
    var selectedVideoForPlayback by remember { mutableStateOf<VideoEntity?>(null) }
    var selectedPdfForReading by remember { mutableStateOf<StudyMaterialEntity?>(null) }

    // Auth screen state
    var authScreenState by remember { mutableStateOf("login") } // "login", "register"

    // Role Switcher Dialog
    var showRoleSwitchDialog by remember { mutableStateOf(false) }

    // Handle back presses smoothly
    BackHandler(enabled = activeSubScreen != null) {
        activeSubScreen = null
    }

    if (currentUser == null) {
        if (authScreenState == "login") {
            LoginScreen(
                viewModel = viewModel,
                onNavigateToRegister = { authScreenState = "register" },
                onLoginSuccess = { viewModel.setBottomTab("home") }
            )
        } else {
            RegisterScreen(
                viewModel = viewModel,
                onNavigateToLogin = { authScreenState = "login" },
                onRegisterSuccess = { viewModel.setBottomTab("home") }
            )
        }
        return
    }

    // Role Switcher Dialog
    if (showRoleSwitchDialog) {
        AlertDialog(
            onDismissRequest = { showRoleSwitchDialog = false },
            title = { Text("Switch Role (Instant Test Mode)") },
            text = {
                Column {
                    Text("Select which perspective you want to experience:", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            viewModel.switchRoleDemo(UserRole.STUDENT)
                            showRoleSwitchDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Blue800)
                    ) {
                        Text("Student View (Manash - Class 12)")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            viewModel.switchRoleDemo(UserRole.TEACHER)
                            showRoleSwitchDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Blue800)
                    ) {
                        Text("Teacher View (Pranjal Sharma - Faculty)")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            viewModel.switchRoleDemo(UserRole.ADMIN)
                            showRoleSwitchDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Blue800)
                    ) {
                        Text("Admin View (Principal Desk)")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showRoleSwitchDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Main App Shell
    Scaffold(
        topBar = {
            if (activeSubScreen == null || activeSubScreen == "subjects" || activeSubScreen == "attendance" || activeSubScreen == "results" || activeSubScreen == "doubts" || activeSubScreen == "messages" || activeSubScreen == "live_classes") {
                SchoolTopAppBar(
                    currentUser = currentUser,
                    currentLanguage = currentLanguage,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onNotificationsClick = { activeSubScreen = "notifications" },
                    onSearchClick = { activeSubScreen = "search" },
                    onRoleSwitchClick = { showRoleSwitchDialog = true }
                )
            }
        },
        bottomBar = {
            if (activeSubScreen == null) {
                SchoolBottomNavigation(
                    currentTab = currentTab,
                    currentLanguage = currentLanguage,
                    onSelectTab = { tab ->
                        viewModel.setBottomTab(tab)
                        activeSubScreen = null
                    }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (activeSubScreen) {
                "subjects" -> {
                    SubjectsScreen(
                        viewModel = viewModel,
                        onSubjectClick = { sub ->
                            selectedSubjectDetail = sub
                            activeSubScreen = "subject_detail"
                        }
                    )
                }
                "subject_detail" -> {
                    selectedSubjectDetail?.let { sub ->
                        SubjectDetailScreen(
                            subject = sub,
                            viewModel = viewModel,
                            onBackClick = { activeSubScreen = "subjects" },
                            onPlayVideo = { vidId ->
                                selectedVideoForPlayback = videos.firstOrNull { it.id == vidId }
                                activeSubScreen = "video_player"
                            },
                            onReadPdf = { matId ->
                                selectedPdfForReading = materials.firstOrNull { it.id == matId }
                                activeSubScreen = "pdf_viewer"
                            },
                            onTakeTest = { examId ->
                                val targetExam = viewModel.exams.value.firstOrNull { it.id == examId }
                                if (targetExam != null) {
                                    viewModel.startExam(targetExam)
                                    activeSubScreen = "exam_taking"
                                }
                            }
                        )
                    }
                }
                "video_player" -> {
                    selectedVideoForPlayback?.let { vid ->
                        VideoPlayerScreen(
                            video = vid,
                            viewModel = viewModel,
                            onBackClick = { activeSubScreen = null }
                        )
                    }
                }
                "pdf_viewer" -> {
                    selectedPdfForReading?.let { mat ->
                        PdfViewerScreen(
                            material = mat,
                            viewModel = viewModel,
                            onBackClick = { activeSubScreen = null }
                        )
                    }
                }
                "live_classes" -> {
                    LiveClassesScreen(viewModel = viewModel)
                }
                "exam_taking" -> {
                    ExamTakingScreen(
                        viewModel = viewModel,
                        onBackClick = { activeSubScreen = null },
                        onViewResult = { activeSubScreen = "exam_result" }
                    )
                }
                "exam_result" -> {
                    ExamResultScreen(
                        viewModel = viewModel,
                        onCloseClick = {
                            viewModel.examFinishedResult.value = null
                            activeSubScreen = null
                            viewModel.setBottomTab("exam")
                        }
                    )
                }
                "results" -> {
                    ResultsScreen(viewModel = viewModel)
                }
                "attendance" -> {
                    AttendanceScreen(viewModel = viewModel)
                }
                "doubts" -> {
                    DoubtScreen(viewModel = viewModel)
                }
                "messages" -> {
                    MessagesScreen(viewModel = viewModel)
                }
                "notifications" -> {
                    NotificationsScreen(
                        viewModel = viewModel,
                        onBackClick = { activeSubScreen = null }
                    )
                }
                "search" -> {
                    SearchScreen(
                        viewModel = viewModel,
                        onBackClick = { activeSubScreen = null },
                        onSelectSubject = { subId ->
                            selectedSubjectDetail = subjects.firstOrNull { it.id == subId }
                            activeSubScreen = "subject_detail"
                        },
                        onSelectVideo = { vidId ->
                            selectedVideoForPlayback = videos.firstOrNull { it.id == vidId }
                            activeSubScreen = "video_player"
                        },
                        onSelectMaterial = { matId ->
                            selectedPdfForReading = materials.firstOrNull { it.id == matId }
                            activeSubScreen = "pdf_viewer"
                        }
                    )
                }
                "admin_panel" -> {
                    AdminTeacherPanelScreen(
                        viewModel = viewModel,
                        onBackClick = { activeSubScreen = null }
                    )
                }
                else -> {
                    // Top level tabs
                    when (currentTab) {
                        "home" -> {
                            HomeScreen(
                                viewModel = viewModel,
                                onNavigateToSubjects = { activeSubScreen = "subjects" },
                                onNavigateToVideos = { viewModel.setBottomTab("classes") },
                                onNavigateToLiveClasses = { activeSubScreen = "live_classes" },
                                onNavigateToPdfs = { activeSubScreen = "subject_detail" },
                                onNavigateToExams = { viewModel.setBottomTab("exam") },
                                onNavigateToResults = { activeSubScreen = "results" },
                                onNavigateToAttendance = { activeSubScreen = "attendance" },
                                onNavigateToDoubts = { activeSubScreen = "doubts" },
                                onNavigateToMessages = { activeSubScreen = "messages" },
                                onNavigateToCourses = { viewModel.setBottomTab("courses") },
                                onOpenVideo = { vidId ->
                                    selectedVideoForPlayback = videos.firstOrNull { it.id == vidId }
                                    activeSubScreen = "video_player"
                                }
                            )
                        }
                        "courses" -> {
                            CoursesScreen(
                                viewModel = viewModel,
                                onCourseClick = {
                                    activeSubScreen = "subjects"
                                }
                            )
                        }
                        "classes" -> {
                            VideoClassesScreen(
                                viewModel = viewModel,
                                onVideoClick = { vid ->
                                    selectedVideoForPlayback = vid
                                    activeSubScreen = "video_player"
                                }
                            )
                        }
                        "exam" -> {
                            ExamsScreen(
                                viewModel = viewModel,
                                onStartExamClick = { exam ->
                                    viewModel.startExam(exam)
                                    activeSubScreen = "exam_taking"
                                }
                            )
                        }
                        "profile" -> {
                            ProfileScreen(
                                viewModel = viewModel,
                                onNavigateToResults = { activeSubScreen = "results" },
                                onNavigateToAttendance = { activeSubScreen = "attendance" },
                                onNavigateToCourses = { viewModel.setBottomTab("courses") },
                                onNavigateToAdminPanel = { activeSubScreen = "admin_panel" },
                                onLogoutClick = { viewModel.logout() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

