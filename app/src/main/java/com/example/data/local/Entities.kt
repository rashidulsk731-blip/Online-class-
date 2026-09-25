package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val phone: String,
    val passwordHash: String,
    val role: String, // STUDENT, TEACHER, ADMIN
    val name: String,
    val photoUri: String = "",
    val schoolId: String = "school_1",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val rollNumber: String,
    val classGrade: String, // Class 11, Class 12
    val schoolName: String,
    val parentName: String,
    val phone: String,
    val email: String,
    val photoUri: String = "",
    val enrolledDate: Long = System.currentTimeMillis()
)

@Entity(tableName = "teachers")
data class TeacherEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val designation: String,
    val subjectsTaught: String,
    val phone: String,
    val email: String
)

@Entity(tableName = "admins")
data class AdminEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val email: String,
    val roleTitle: String = "Administrator"
)

@Entity(tableName = "schools")
data class SchoolEntity(
    @PrimaryKey val id: String,
    val name: String,
    val adminName: String,
    val contactNumber: String,
    val email: String,
    val address: String,
    val logoUrl: String = ""
)

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey val id: String,
    val nameEn: String,
    val nameAs: String,
    val code: String,
    val iconName: String,
    val description: String,
    val classGrade: String,
    val teacherName: String
)

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val id: String,
    val title: String,
    val titleAs: String,
    val subjectId: String,
    val classGrade: String,
    val teacher: String,
    val description: String,
    val price: Double = 100.0,
    val imageUrl: String = "",
    val durationHours: Int = 30,
    val totalLessons: Int = 24,
    val isActive: Boolean = true
)

@Entity(tableName = "enrollments")
data class EnrollmentEntity(
    @PrimaryKey val id: String,
    val studentId: String,
    val courseId: String,
    val enrolledAt: Long = System.currentTimeMillis(),
    val progressPercent: Int = 0,
    val status: String = "ACTIVE"
)

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val id: String,
    val subjectId: String,
    val classGrade: String,
    val chapter: String,
    val title: String,
    val duration: String,
    val videoUrl: String,
    val thumbnailUrl: String = "",
    val teacher: String,
    val orderIndex: Int = 1,
    val isCompleted: Boolean = false
)

@Entity(tableName = "live_classes")
data class LiveClassEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subjectId: String,
    val classGrade: String,
    val teacher: String,
    val scheduledDate: String,
    val scheduledTime: String,
    val joinUrl: String,
    val status: String = "UPCOMING" // UPCOMING, LIVE, COMPLETED
)

@Entity(tableName = "study_materials")
data class StudyMaterialEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subjectId: String,
    val classGrade: String,
    val chapter: String,
    val category: String, // Notes, PDF, Study Material, Question Paper, Practice Material
    val fileUrl: String,
    val fileSize: String = "2.4 MB",
    val pageCount: Int = 12,
    val date: String,
    val contentPreview: String = ""
)

@Entity(tableName = "exams")
data class ExamEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subjectId: String,
    val classGrade: String,
    val totalMarks: Int = 20,
    val durationMinutes: Int = 30,
    val examDate: String,
    val instructions: String,
    val isActive: Boolean = true
)

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val examId: String,
    val questionNumber: Int,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOption: Int, // 0: A, 1: B, 2: C, 3: D
    val marks: Int = 1,
    val explanation: String = ""
)

@Entity(tableName = "exam_attempts")
data class ExamAttemptEntity(
    @PrimaryKey val id: String,
    val examId: String,
    val studentId: String,
    val score: Int,
    val totalMarks: Int,
    val percentage: Double,
    val grade: String,
    val submittedAt: Long = System.currentTimeMillis(),
    val studentAnswers: String = "" // e.g. "0:1,1:2,2:0"
)

@Entity(tableName = "results")
data class ResultEntity(
    @PrimaryKey val id: String,
    val studentId: String,
    val studentName: String,
    val classGrade: String,
    val rollNumber: String,
    val subjectId: String,
    val subjectName: String,
    val examName: String,
    val totalMarks: Int,
    val obtainedMarks: Int,
    val percentage: Double,
    val grade: String,
    val date: String
)

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey val id: String,
    val studentId: String,
    val studentName: String,
    val classGrade: String,
    val date: String,
    val isPresent: Boolean,
    val remarks: String = "Regular Class"
)

@Entity(tableName = "doubts")
data class DoubtEntity(
    @PrimaryKey val id: String,
    val studentId: String,
    val studentName: String,
    val subjectId: String,
    val chapter: String,
    val question: String,
    val imageUrl: String = "",
    val status: String = "PENDING", // PENDING, ANSWERED, SOLVED
    val reply: String = "",
    val repliedBy: String = "",
    val date: String
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val senderRole: String,
    val receiverId: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val recipientId: String = "", // empty for broadcast
    val title: String,
    val titleAs: String,
    val message: String,
    val messageAs: String,
    val type: String, // VIDEO, PDF, COURSE, LIVE_CLASS, EXAM, RESULT, TEACHER_REPLY, NOTICE
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey val id: String,
    val studentId: String,
    val courseId: String,
    val courseTitle: String,
    val amount: Double = 100.0,
    val upiId: String,
    val transactionRef: String,
    val status: String = "SUCCESSFUL", // PENDING, SUCCESSFUL, FAILED, REFUNDED
    val paymentDate: Long = System.currentTimeMillis()
)

@Entity(tableName = "student_progress")
data class StudentProgressEntity(
    @PrimaryKey val id: String,
    val studentId: String,
    val lessonOrVideoId: String,
    val contentType: String, // VIDEO, PDF, EXAM
    val isCompleted: Boolean,
    val watchedDurationSeconds: Long = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)
