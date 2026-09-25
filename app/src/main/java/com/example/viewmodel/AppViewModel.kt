package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.*
import com.example.data.repository.SchoolRepository
import com.example.model.*
import com.example.util.LanguageManager
import com.example.util.UpiPaymentHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = SchoolRepository(db)

    // Language state
    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    // Current User session
    private val _currentUser = MutableStateFlow<CurrentUser?>(
        CurrentUser(
            id = "student_1",
            name = "Manash Jyoti Nath",
            email = "student@school.edu",
            phone = "9876543210",
            role = UserRole.STUDENT,
            classGrade = "Class 12",
            rollNumber = "101",
            schoolName = "PM Shri Berbhngi HS School",
            parentName = "Biren Nath"
        )
    )
    val currentUser: StateFlow<CurrentUser?> = _currentUser.asStateFlow()

    // Selected Class Grade filter ("Class 12" / "Class 11")
    private val _selectedClass = MutableStateFlow("Class 12")
    val selectedClass: StateFlow<String> = _selectedClass.asStateFlow()

    // Bottom Navigation State ("home", "courses", "classes", "exam", "profile")
    private val _currentTab = MutableStateFlow("home")
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    // UI Message Banner / Toast
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    // Flow bindings
    val school = repository.getSchool().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val subjects = repository.getAllSubjects().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val courses = repository.getActiveCourses().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allCourses = repository.getAllCourses().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val videos = repository.getAllVideos().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val liveClasses = repository.getAllLiveClasses().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val studyMaterials = repository.getAllStudyMaterials().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val exams = repository.getAllExams().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allStudents = repository.getAllStudents().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allTeachers = repository.getAllTeachers().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Student specific
    val myEnrollments = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getEnrollmentsForStudent(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myResults = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getResultsForStudent(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myAttendance = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getAttendanceForStudent(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myDoubts = _currentUser.flatMapLatest { user ->
        if (user != null && user.role == UserRole.STUDENT) repository.getDoubtsForStudent(user.id) else repository.getAllDoubts()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getNotificationsForStudent(user.id) else repository.getAllNotifications()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAttendance = repository.getAllAttendance().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allResults = repository.getAllResults().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allPayments = repository.getAllPayments().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Media / Content Viewer states
    val activeVideo = MutableStateFlow<VideoEntity?>(null)
    val activePdf = MutableStateFlow<StudyMaterialEntity?>(null)
    val activeSubject = MutableStateFlow<SubjectEntity?>(null)
    val activeCourse = MutableStateFlow<CourseEntity?>(null)

    // Exam In-Progress State
    val activeExam = MutableStateFlow<ExamEntity?>(null)
    val activeExamQuestions = MutableStateFlow<List<QuestionEntity>>(emptyList())
    val currentQuestionIndex = MutableStateFlow(0)
    val studentAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap()) // qIndex -> selectedOption
    val examRemainingSeconds = MutableStateFlow(0)
    val examFinishedResult = MutableStateFlow<ExamAttemptEntity?>(null)
    private var examTimerJob: Job? = null

    init {
        viewModelScope.launch {
            // Seed database
            DatabaseInitializer.populateInitialData(db)
        }
    }

    // Language toggle
    fun toggleLanguage() {
        _currentLanguage.value = if (_currentLanguage.value == AppLanguage.ENGLISH) {
            AppLanguage.ASSAMESE
        } else {
            AppLanguage.ENGLISH
        }
    }

    fun setLanguage(lang: AppLanguage) {
        _currentLanguage.value = lang
    }

    fun getString(key: String): String = LanguageManager.getString(key, _currentLanguage.value)

    fun setSelectedClass(grade: String) {
        _selectedClass.value = grade
    }

    fun setBottomTab(tab: String) {
        _currentTab.value = tab
    }

    // Authentication
    fun login(email: String, pass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val user = repository.login(email.trim(), pass.trim())
            if (user != null) {
                val role = when (user.role) {
                    "ADMIN" -> UserRole.ADMIN
                    "TEACHER" -> UserRole.TEACHER
                    else -> UserRole.STUDENT
                }
                val student = if (role == UserRole.STUDENT) repository.getStudentByUserId(user.id) else null

                _currentUser.value = CurrentUser(
                    id = user.id,
                    name = user.name,
                    email = user.email,
                    phone = user.phone,
                    role = role,
                    classGrade = student?.classGrade ?: "Class 12",
                    rollNumber = student?.rollNumber ?: "101",
                    schoolName = student?.schoolName ?: "PM Shri Berbhngi HS School",
                    parentName = student?.parentName ?: "Guardian",
                    photoUri = user.photoUri
                )
                onResult(true, "Login Successful!")
            } else {
                onResult(false, "Invalid email or password. Please try again.")
            }
        }
    }

    fun registerStudent(
        name: String,
        email: String,
        phone: String,
        password: String,
        classGrade: String,
        rollNumber: String,
        parentName: String,
        schoolName: String,
        photoUri: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val user = repository.registerStudent(
                    name = name,
                    email = email,
                    phone = phone,
                    password = password,
                    classGrade = classGrade,
                    rollNumber = rollNumber,
                    parentName = parentName,
                    schoolName = schoolName,
                    photoUri = photoUri
                )
                _currentUser.value = CurrentUser(
                    id = user.id,
                    name = name,
                    email = email,
                    phone = phone,
                    role = UserRole.STUDENT,
                    classGrade = classGrade,
                    rollNumber = rollNumber,
                    schoolName = schoolName,
                    parentName = parentName,
                    photoUri = photoUri
                )
                onResult(true, "Registration successful! Welcome to Student Education.")
            } catch (e: Exception) {
                onResult(false, e.localizedMessage ?: "Registration error occurred.")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _currentTab.value = "home"
    }

    fun switchRoleDemo(role: UserRole) {
        when (role) {
            UserRole.STUDENT -> {
                _currentUser.value = CurrentUser(
                    id = "student_1",
                    name = "Manash Jyoti Nath",
                    email = "student@school.edu",
                    phone = "9876543210",
                    role = UserRole.STUDENT,
                    classGrade = "Class 12",
                    rollNumber = "101",
                    schoolName = "PM Shri Berbhngi HS School",
                    parentName = "Biren Nath"
                )
            }
            UserRole.TEACHER -> {
                _currentUser.value = CurrentUser(
                    id = "teacher_1",
                    name = "Pranjal Sharma",
                    email = "teacher@school.edu",
                    phone = "9854012345",
                    role = UserRole.TEACHER,
                    classGrade = "Class 12",
                    rollNumber = "Staff-04",
                    schoolName = "PM Shri Berbhngi HS School",
                    parentName = "Senior Faculty"
                )
            }
            UserRole.ADMIN -> {
                _currentUser.value = CurrentUser(
                    id = "admin_1",
                    name = "Principal / Admin Desk",
                    email = "admin@school.edu",
                    phone = "9387628351",
                    role = UserRole.ADMIN,
                    classGrade = "All Classes",
                    rollNumber = "Admin-01",
                    schoolName = "PM Shri Berbhngi HS School",
                    parentName = "Administration"
                )
            }
        }
    }

    // UPI Payment & Enrollment
    fun enrollCourseWithUpi(
        course: CourseEntity,
        enteredUpiId: String,
        onComplete: (Boolean, String) -> Unit
    ) {
        val user = _currentUser.value ?: run {
            onComplete(false, "Please log in as student to enroll.")
            return
        }

        val request = UpiPaymentHandler.PaymentRequest(
            studentId = user.id,
            courseId = course.id,
            courseTitle = course.title,
            amount = course.price
        )

        val result = UpiPaymentHandler.processPayment(request, enteredUpiId)
        if (result.status == PaymentStatus.SUCCESSFUL) {
            viewModelScope.launch {
                repository.recordPayment(
                    PaymentEntity(
                        id = "pay_${System.currentTimeMillis()}",
                        studentId = user.id,
                        courseId = course.id,
                        courseTitle = course.title,
                        amount = course.price,
                        upiId = enteredUpiId,
                        transactionRef = result.transactionRef,
                        status = "SUCCESSFUL"
                    )
                )
                repository.enrollInCourse(user.id, course.id)
                onComplete(true, result.message)
            }
        } else {
            onComplete(false, result.message)
        }
    }

    // Exam Flow
    fun startExam(exam: ExamEntity) {
        activeExam.value = exam
        currentQuestionIndex.value = 0
        studentAnswers.value = emptyMap()
        examFinishedResult.value = null
        examRemainingSeconds.value = exam.durationMinutes * 60

        viewModelScope.launch {
            repository.getQuestionsByExam(exam.id).collectLatest { qList ->
                activeExamQuestions.value = qList
            }
        }

        examTimerJob?.cancel()
        examTimerJob = viewModelScope.launch {
            while (examRemainingSeconds.value > 0) {
                delay(1000)
                examRemainingSeconds.value -= 1
            }
            // Auto submit if time runs out
            submitExam()
        }
    }

    fun selectExamAnswer(questionIndex: Int, option: Int) {
        studentAnswers.value = studentAnswers.value + (questionIndex to option)
    }

    fun submitExam() {
        examTimerJob?.cancel()
        val exam = activeExam.value ?: return
        val questions = activeExamQuestions.value
        val answers = studentAnswers.value
        val user = _currentUser.value

        var totalScore = 0
        var maxMarks = 0

        questions.forEachIndexed { index, q ->
            maxMarks += q.marks
            val studentChoice = answers[index]
            if (studentChoice != null && studentChoice == q.correctOption) {
                totalScore += q.marks
            }
        }

        val percentage = if (maxMarks > 0) (totalScore.toDouble() / maxMarks) * 100.0 else 0.0
        val grade = when {
            percentage >= 90 -> "A+"
            percentage >= 80 -> "A"
            percentage >= 70 -> "B+"
            percentage >= 60 -> "B"
            percentage >= 50 -> "C"
            else -> "D"
        }

        val answersSummary = answers.entries.joinToString(",") { "${it.key}:${it.value}" }

        val attempt = ExamAttemptEntity(
            id = "att_${System.currentTimeMillis()}",
            examId = exam.id,
            studentId = user?.id ?: "student_1",
            score = totalScore,
            totalMarks = maxMarks,
            percentage = percentage,
            grade = grade,
            studentAnswers = answersSummary
        )

        viewModelScope.launch {
            repository.submitExamAttempt(attempt)
            // Also store in Results table for permanent record card
            repository.insertResult(
                ResultEntity(
                    id = "res_${System.currentTimeMillis()}",
                    studentId = user?.id ?: "student_1",
                    studentName = user?.name ?: "Student",
                    classGrade = user?.classGrade ?: "Class 12",
                    rollNumber = user?.rollNumber ?: "101",
                    subjectId = exam.subjectId,
                    subjectName = exam.title,
                    examName = exam.title,
                    totalMarks = maxMarks,
                    obtainedMarks = totalScore,
                    percentage = percentage,
                    grade = grade,
                    date = "2026-09-23"
                )
            )
            examFinishedResult.value = attempt
        }
    }

    // Doubts
    fun askDoubt(subjectId: String, chapter: String, questionText: String, onComplete: () -> Unit) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.askDoubt(
                DoubtEntity(
                    id = "doubt_${System.currentTimeMillis()}",
                    studentId = user.id,
                    studentName = user.name,
                    subjectId = subjectId,
                    chapter = chapter,
                    question = questionText,
                    status = "PENDING",
                    date = "2026-09-23"
                )
            )
            onComplete()
        }
    }

    fun replyDoubt(doubt: DoubtEntity, replyText: String) {
        val teacherName = _currentUser.value?.name ?: "Teacher"
        viewModelScope.launch {
            repository.replyDoubt(
                doubt.copy(
                    reply = replyText,
                    repliedBy = "$teacherName (Teacher)",
                    status = "ANSWERED"
                )
            )
        }
    }

    fun markDoubtSolved(doubt: DoubtEntity) {
        viewModelScope.launch {
            repository.replyDoubt(doubt.copy(status = "SOLVED"))
        }
    }

    // Teacher & Admin Management
    fun addVideo(
        title: String,
        subjectId: String,
        chapter: String,
        duration: String,
        url: String,
        classGrade: String
    ) {
        viewModelScope.launch {
            repository.insertVideo(
                VideoEntity(
                    id = "vid_${System.currentTimeMillis()}",
                    title = title,
                    subjectId = subjectId,
                    chapter = chapter,
                    duration = duration,
                    videoUrl = url,
                    classGrade = classGrade,
                    teacher = _currentUser.value?.name ?: "Faculty"
                )
            )
        }
    }

    fun deleteVideo(video: VideoEntity) {
        viewModelScope.launch { repository.deleteVideo(video) }
    }

    fun addLiveClass(
        title: String,
        subjectId: String,
        classGrade: String,
        date: String,
        time: String,
        joinUrl: String
    ) {
        viewModelScope.launch {
            repository.insertLiveClass(
                LiveClassEntity(
                    id = "live_${System.currentTimeMillis()}",
                    title = title,
                    subjectId = subjectId,
                    classGrade = classGrade,
                    teacher = _currentUser.value?.name ?: "Faculty",
                    scheduledDate = date,
                    scheduledTime = time,
                    joinUrl = joinUrl,
                    status = "UPCOMING"
                )
            )
        }
    }

    fun deleteLiveClass(liveClass: LiveClassEntity) {
        viewModelScope.launch { repository.deleteLiveClass(liveClass) }
    }

    fun addStudyMaterial(
        title: String,
        subjectId: String,
        chapter: String,
        category: String,
        fileUrl: String,
        contentPreview: String,
        classGrade: String
    ) {
        viewModelScope.launch {
            repository.insertStudyMaterial(
                StudyMaterialEntity(
                    id = "mat_${System.currentTimeMillis()}",
                    title = title,
                    subjectId = subjectId,
                    classGrade = classGrade,
                    chapter = chapter,
                    category = category,
                    fileUrl = fileUrl,
                    contentPreview = contentPreview,
                    date = "2026-09-23"
                )
            )
        }
    }

    fun deleteStudyMaterial(material: StudyMaterialEntity) {
        viewModelScope.launch { repository.deleteStudyMaterial(material) }
    }

    fun markAttendance(student: StudentEntity, isPresent: Boolean, date: String, remarks: String) {
        viewModelScope.launch {
            repository.markAttendance(
                AttendanceEntity(
                    id = "att_${student.id}_${System.currentTimeMillis()}",
                    studentId = student.id,
                    studentName = student.userId, // or look up name
                    classGrade = student.classGrade,
                    date = date,
                    isPresent = isPresent,
                    remarks = remarks
                )
            )
        }
    }

    fun addResult(
        studentId: String,
        studentName: String,
        rollNumber: String,
        subjectId: String,
        subjectName: String,
        examName: String,
        totalMarks: Int,
        obtainedMarks: Int
    ) {
        val percentage = (obtainedMarks.toDouble() / totalMarks) * 100.0
        val grade = when {
            percentage >= 90 -> "A+"
            percentage >= 80 -> "A"
            percentage >= 70 -> "B+"
            percentage >= 60 -> "B"
            percentage >= 50 -> "C"
            else -> "D"
        }
        viewModelScope.launch {
            repository.insertResult(
                ResultEntity(
                    id = "res_${System.currentTimeMillis()}",
                    studentId = studentId,
                    studentName = studentName,
                    classGrade = _selectedClass.value,
                    rollNumber = rollNumber,
                    subjectId = subjectId,
                    subjectName = subjectName,
                    examName = examName,
                    totalMarks = totalMarks,
                    obtainedMarks = obtainedMarks,
                    percentage = percentage,
                    grade = grade,
                    date = "2026-09-23"
                )
            )
        }
    }

    fun broadcastNotification(titleEn: String, titleAs: String, msgEn: String, msgAs: String, type: String) {
        viewModelScope.launch {
            repository.postNotification(
                NotificationEntity(
                    id = "notif_${System.currentTimeMillis()}",
                    title = titleEn,
                    titleAs = titleAs,
                    message = msgEn,
                    messageAs = msgAs,
                    type = type
                )
            )
        }
    }

    fun markNotificationRead(notifId: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(notifId)
        }
    }
}
