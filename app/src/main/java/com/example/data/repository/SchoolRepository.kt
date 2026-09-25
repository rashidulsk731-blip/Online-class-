package com.example.data.repository

import com.example.data.local.*
import kotlinx.coroutines.flow.Flow

class SchoolRepository(private val db: AppDatabase) {

    // Auth & Users
    suspend fun login(email: String, pass: String): UserEntity? = db.userDao().login(email, pass)

    suspend fun getUserById(userId: String): UserEntity? = db.userDao().getUserById(userId)

    suspend fun registerStudent(
        name: String,
        email: String,
        phone: String,
        password: String,
        classGrade: String,
        rollNumber: String,
        parentName: String,
        schoolName: String,
        photoUri: String
    ): UserEntity {
        val userId = "student_${System.currentTimeMillis()}"
        val user = UserEntity(
            id = userId,
            email = email,
            phone = phone,
            passwordHash = password,
            role = "STUDENT",
            name = name,
            photoUri = photoUri
        )
        db.userDao().insertUser(user)

        val student = StudentEntity(
            id = userId,
            userId = userId,
            rollNumber = rollNumber,
            classGrade = classGrade,
            schoolName = schoolName,
            parentName = parentName,
            phone = phone,
            email = email,
            photoUri = photoUri
        )
        db.userDao().insertStudent(student)
        return user
    }

    fun getAllStudents(): Flow<List<StudentEntity>> = db.userDao().getAllStudents()
    fun getAllTeachers(): Flow<List<TeacherEntity>> = db.userDao().getAllTeachers()
    suspend fun getStudentByUserId(userId: String): StudentEntity? = db.userDao().getStudentByUserId(userId)

    // School Info
    fun getSchool(): Flow<SchoolEntity?> = db.schoolDao().getSchool()
    suspend fun updateSchool(school: SchoolEntity) = db.schoolDao().insertOrUpdateSchool(school)

    // Subjects
    fun getAllSubjects(): Flow<List<SubjectEntity>> = db.subjectDao().getAllSubjects()
    fun getSubjectsByClass(classGrade: String): Flow<List<SubjectEntity>> = db.subjectDao().getSubjectsByClass(classGrade)
    suspend fun getSubjectById(id: String): SubjectEntity? = db.subjectDao().getSubjectById(id)
    suspend fun insertSubject(sub: SubjectEntity) = db.subjectDao().insertSubject(sub)
    suspend fun deleteSubject(sub: SubjectEntity) = db.subjectDao().deleteSubject(sub)

    // Courses & Enrollment
    fun getActiveCourses(): Flow<List<CourseEntity>> = db.courseDao().getAllActiveCourses()
    fun getAllCourses(): Flow<List<CourseEntity>> = db.courseDao().getAllCourses()
    suspend fun getCourseById(id: String): CourseEntity? = db.courseDao().getCourseById(id)
    suspend fun insertCourse(c: CourseEntity) = db.courseDao().insertCourse(c)
    suspend fun deleteCourse(c: CourseEntity) = db.courseDao().deleteCourse(c)
    fun getEnrollmentsForStudent(studentId: String): Flow<List<EnrollmentEntity>> = db.courseDao().getEnrollmentsForStudent(studentId)
    suspend fun enrollInCourse(studentId: String, courseId: String) {
        val enrollment = EnrollmentEntity(
            id = "enr_${studentId}_${courseId}_${System.currentTimeMillis()}",
            studentId = studentId,
            courseId = courseId,
            progressPercent = 0,
            status = "ACTIVE"
        )
        db.courseDao().insertEnrollment(enrollment)
    }
    suspend fun isStudentEnrolled(studentId: String, courseId: String): Boolean =
        db.courseDao().isStudentEnrolled(studentId, courseId) > 0

    // Videos
    fun getVideosByClass(classGrade: String): Flow<List<VideoEntity>> = db.learningDao().getVideosByClass(classGrade)
    fun getVideosBySubject(subjectId: String): Flow<List<VideoEntity>> = db.learningDao().getVideosBySubject(subjectId)
    fun getAllVideos(): Flow<List<VideoEntity>> = db.learningDao().getAllVideos()
    suspend fun insertVideo(video: VideoEntity) = db.learningDao().insertVideo(video)
    suspend fun deleteVideo(video: VideoEntity) = db.learningDao().deleteVideo(video)

    // Live Classes
    fun getLiveClassesByClass(classGrade: String): Flow<List<LiveClassEntity>> = db.learningDao().getLiveClassesByClass(classGrade)
    fun getAllLiveClasses(): Flow<List<LiveClassEntity>> = db.learningDao().getAllLiveClasses()
    suspend fun insertLiveClass(liveClass: LiveClassEntity) = db.learningDao().insertLiveClass(liveClass)
    suspend fun deleteLiveClass(liveClass: LiveClassEntity) = db.learningDao().deleteLiveClass(liveClass)

    // Study Materials & PDFs
    fun getStudyMaterialsByClass(classGrade: String): Flow<List<StudyMaterialEntity>> = db.learningDao().getStudyMaterialsByClass(classGrade)
    fun getStudyMaterialsBySubject(subjectId: String): Flow<List<StudyMaterialEntity>> = db.learningDao().getStudyMaterialsBySubject(subjectId)
    fun getAllStudyMaterials(): Flow<List<StudyMaterialEntity>> = db.learningDao().getAllStudyMaterials()
    suspend fun insertStudyMaterial(mat: StudyMaterialEntity) = db.learningDao().insertStudyMaterial(mat)
    suspend fun deleteStudyMaterial(mat: StudyMaterialEntity) = db.learningDao().deleteStudyMaterial(mat)

    // Exams
    fun getExamsByClass(classGrade: String): Flow<List<ExamEntity>> = db.examDao().getExamsByClass(classGrade)
    fun getAllExams(): Flow<List<ExamEntity>> = db.examDao().getAllExams()
    suspend fun getExamById(id: String): ExamEntity? = db.examDao().getExamById(id)
    fun getQuestionsByExam(examId: String): Flow<List<QuestionEntity>> = db.examDao().getQuestionsByExam(examId)
    suspend fun insertExam(exam: ExamEntity) = db.examDao().insertExam(exam)
    suspend fun deleteExam(exam: ExamEntity) = db.examDao().deleteExam(exam)
    suspend fun insertQuestion(q: QuestionEntity) = db.examDao().insertQuestion(q)
    suspend fun insertQuestions(list: List<QuestionEntity>) = db.examDao().insertQuestions(list)
    suspend fun submitExamAttempt(attempt: ExamAttemptEntity) = db.examDao().insertAttempt(attempt)
    fun getAttemptsForStudent(studentId: String): Flow<List<ExamAttemptEntity>> = db.examDao().getAttemptsForStudent(studentId)

    // Results
    fun getResultsForStudent(studentId: String): Flow<List<ResultEntity>> = db.resultDao().getResultsForStudent(studentId)
    fun getAllResults(): Flow<List<ResultEntity>> = db.resultDao().getAllResults()
    suspend fun insertResult(result: ResultEntity) = db.resultDao().insertResult(result)
    suspend fun deleteResult(result: ResultEntity) = db.resultDao().deleteResult(result)

    // Attendance
    fun getAttendanceForStudent(studentId: String): Flow<List<AttendanceEntity>> = db.attendanceDao().getAttendanceForStudent(studentId)
    fun getAllAttendance(): Flow<List<AttendanceEntity>> = db.attendanceDao().getAllAttendance()
    suspend fun markAttendance(att: AttendanceEntity) = db.attendanceDao().insertAttendance(att)
    suspend fun markAttendanceList(list: List<AttendanceEntity>) = db.attendanceDao().insertAttendanceList(list)

    // Doubts
    fun getDoubtsForStudent(studentId: String): Flow<List<DoubtEntity>> = db.communicationDao().getDoubtsForStudent(studentId)
    fun getAllDoubts(): Flow<List<DoubtEntity>> = db.communicationDao().getAllDoubts()
    suspend fun askDoubt(doubt: DoubtEntity) = db.communicationDao().insertDoubt(doubt)
    suspend fun replyDoubt(doubt: DoubtEntity) = db.communicationDao().insertDoubt(doubt)
    suspend fun deleteDoubt(doubt: DoubtEntity) = db.communicationDao().deleteDoubt(doubt)

    // Messages
    fun getMessagesForConversation(convId: String): Flow<List<MessageEntity>> = db.communicationDao().getMessagesForConversation(convId)
    fun getRecentMessagesForUser(userId: String): Flow<List<MessageEntity>> = db.communicationDao().getRecentMessagesForUser(userId)
    suspend fun sendMessage(msg: MessageEntity) = db.communicationDao().insertMessage(msg)

    // Notifications
    fun getNotificationsForStudent(studentId: String): Flow<List<NotificationEntity>> = db.communicationDao().getNotificationsForStudent(studentId)
    fun getAllNotifications(): Flow<List<NotificationEntity>> = db.communicationDao().getAllNotifications()
    suspend fun postNotification(notif: NotificationEntity) = db.communicationDao().insertNotification(notif)
    suspend fun markNotificationAsRead(id: String) = db.communicationDao().markNotificationAsRead(id)

    // Payments
    fun getPaymentsForStudent(studentId: String): Flow<List<PaymentEntity>> = db.paymentDao().getPaymentsForStudent(studentId)
    fun getAllPayments(): Flow<List<PaymentEntity>> = db.paymentDao().getAllPayments()
    suspend fun recordPayment(payment: PaymentEntity) = db.paymentDao().insertPayment(payment)
}
