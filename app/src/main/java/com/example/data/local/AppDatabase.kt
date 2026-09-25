package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        StudentEntity::class,
        TeacherEntity::class,
        AdminEntity::class,
        SchoolEntity::class,
        SubjectEntity::class,
        CourseEntity::class,
        EnrollmentEntity::class,
        VideoEntity::class,
        LiveClassEntity::class,
        StudyMaterialEntity::class,
        ExamEntity::class,
        QuestionEntity::class,
        ExamAttemptEntity::class,
        ResultEntity::class,
        AttendanceEntity::class,
        DoubtEntity::class,
        MessageEntity::class,
        NotificationEntity::class,
        PaymentEntity::class,
        StudentProgressEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun schoolDao(): SchoolDao
    abstract fun subjectDao(): SubjectDao
    abstract fun courseDao(): CourseDao
    abstract fun learningDao(): LearningDao
    abstract fun examDao(): ExamDao
    abstract fun resultDao(): ResultDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun communicationDao(): CommunicationDao
    abstract fun paymentDao(): PaymentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "student_education_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
