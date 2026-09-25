package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email AND passwordHash = :password LIMIT 1")
    suspend fun login(email: String, password: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE role = :role")
    fun getUsersByRole(role: String): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM students")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE userId = :userId LIMIT 1")
    suspend fun getStudentByUserId(userId: String): StudentEntity?

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    suspend fun getStudentById(id: String): StudentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity)

    @Query("SELECT * FROM teachers")
    fun getAllTeachers(): Flow<List<TeacherEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeacher(teacher: TeacherEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdmin(admin: AdminEntity)
}

@Dao
interface SchoolDao {
    @Query("SELECT * FROM schools LIMIT 1")
    fun getSchool(): Flow<SchoolEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSchool(school: SchoolEntity)
}

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE classGrade = :classGrade")
    fun getSubjectsByClass(classGrade: String): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE id = :id LIMIT 1")
    suspend fun getSubjectById(id: String): SubjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectEntity)

    @Delete
    suspend fun deleteSubject(subject: SubjectEntity)
}

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses WHERE isActive = 1")
    fun getAllActiveCourses(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses")
    fun getAllCourses(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE id = :id LIMIT 1")
    suspend fun getCourseById(id: String): CourseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseEntity)

    @Delete
    suspend fun deleteCourse(course: CourseEntity)

    @Query("SELECT * FROM enrollments WHERE studentId = :studentId")
    fun getEnrollmentsForStudent(studentId: String): Flow<List<EnrollmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnrollment(enrollment: EnrollmentEntity)

    @Query("SELECT COUNT(*) FROM enrollments WHERE studentId = :studentId AND courseId = :courseId")
    suspend fun isStudentEnrolled(studentId: String, courseId: String): Int
}

@Dao
interface LearningDao {
    // Videos
    @Query("SELECT * FROM videos WHERE classGrade = :classGrade ORDER BY orderIndex ASC")
    fun getVideosByClass(classGrade: String): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE subjectId = :subjectId ORDER BY orderIndex ASC")
    fun getVideosBySubject(subjectId: String): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos")
    fun getAllVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE id = :id LIMIT 1")
    suspend fun getVideoById(id: String): VideoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoEntity)

    @Delete
    suspend fun deleteVideo(video: VideoEntity)

    // Live Classes
    @Query("SELECT * FROM live_classes WHERE classGrade = :classGrade ORDER BY scheduledDate ASC")
    fun getLiveClassesByClass(classGrade: String): Flow<List<LiveClassEntity>>

    @Query("SELECT * FROM live_classes ORDER BY scheduledDate DESC")
    fun getAllLiveClasses(): Flow<List<LiveClassEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLiveClass(liveClass: LiveClassEntity)

    @Delete
    suspend fun deleteLiveClass(liveClass: LiveClassEntity)

    // Study Materials & PDFs
    @Query("SELECT * FROM study_materials WHERE classGrade = :classGrade")
    fun getStudyMaterialsByClass(classGrade: String): Flow<List<StudyMaterialEntity>>

    @Query("SELECT * FROM study_materials WHERE subjectId = :subjectId")
    fun getStudyMaterialsBySubject(subjectId: String): Flow<List<StudyMaterialEntity>>

    @Query("SELECT * FROM study_materials")
    fun getAllStudyMaterials(): Flow<List<StudyMaterialEntity>>

    @Query("SELECT * FROM study_materials WHERE id = :id LIMIT 1")
    suspend fun getStudyMaterialById(id: String): StudyMaterialEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyMaterial(material: StudyMaterialEntity)

    @Delete
    suspend fun deleteStudyMaterial(material: StudyMaterialEntity)

    // Progress
    @Query("SELECT * FROM student_progress WHERE studentId = :studentId")
    fun getStudentProgress(studentId: String): Flow<List<StudentProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: StudentProgressEntity)
}

@Dao
interface ExamDao {
    @Query("SELECT * FROM exams WHERE classGrade = :classGrade AND isActive = 1")
    fun getExamsByClass(classGrade: String): Flow<List<ExamEntity>>

    @Query("SELECT * FROM exams")
    fun getAllExams(): Flow<List<ExamEntity>>

    @Query("SELECT * FROM exams WHERE id = :id LIMIT 1")
    suspend fun getExamById(id: String): ExamEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: ExamEntity)

    @Delete
    suspend fun deleteExam(exam: ExamEntity)

    @Query("SELECT * FROM questions WHERE examId = :examId ORDER BY questionNumber ASC")
    fun getQuestionsByExam(examId: String): Flow<List<QuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Query("SELECT * FROM exam_attempts WHERE studentId = :studentId")
    fun getAttemptsForStudent(studentId: String): Flow<List<ExamAttemptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: ExamAttemptEntity)
}

@Dao
interface ResultDao {
    @Query("SELECT * FROM results WHERE studentId = :studentId ORDER BY date DESC")
    fun getResultsForStudent(studentId: String): Flow<List<ResultEntity>>

    @Query("SELECT * FROM results WHERE classGrade = :classGrade ORDER BY date DESC")
    fun getResultsByClass(classGrade: String): Flow<List<ResultEntity>>

    @Query("SELECT * FROM results ORDER BY date DESC")
    fun getAllResults(): Flow<List<ResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: ResultEntity)

    @Delete
    suspend fun deleteResult(result: ResultEntity)
}

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance WHERE studentId = :studentId ORDER BY date DESC")
    fun getAttendanceForStudent(studentId: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE classGrade = :classGrade ORDER BY date DESC")
    fun getAttendanceByClass(classGrade: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance ORDER BY date DESC")
    fun getAllAttendance(): Flow<List<AttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceList(list: List<AttendanceEntity>)
}

@Dao
interface CommunicationDao {
    // Doubts
    @Query("SELECT * FROM doubts WHERE studentId = :studentId ORDER BY date DESC")
    fun getDoubtsForStudent(studentId: String): Flow<List<DoubtEntity>>

    @Query("SELECT * FROM doubts ORDER BY date DESC")
    fun getAllDoubts(): Flow<List<DoubtEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoubt(doubt: DoubtEntity)

    @Delete
    suspend fun deleteDoubt(doubt: DoubtEntity)

    // Messages
    @Query("SELECT * FROM messages WHERE conversationId = :convId ORDER BY timestamp ASC")
    fun getMessagesForConversation(convId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE receiverId = :userId OR senderId = :userId ORDER BY timestamp DESC")
    fun getRecentMessagesForUser(userId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE recipientId = '' OR recipientId = :studentId ORDER BY timestamp DESC")
    fun getNotificationsForStudent(studentId: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: String)
}

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments WHERE studentId = :studentId ORDER BY paymentDate DESC")
    fun getPaymentsForStudent(studentId: String): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments ORDER BY paymentDate DESC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)
}
