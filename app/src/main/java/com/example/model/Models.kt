package com.example.model

enum class UserRole {
    STUDENT,
    TEACHER,
    ADMIN
}

enum class AppLanguage(val code: String, val displayNameEn: String, val displayNameAs: String) {
    ENGLISH("en", "English", "ইংৰাজী"),
    ASSAMESE("as", "অসমীয়া (Assamese)", "অসমীয়া")
}

enum class PaymentStatus {
    PENDING,
    SUCCESSFUL,
    FAILED,
    REFUNDED
}

enum class DoubtStatus {
    PENDING,
    ANSWERED,
    SOLVED
}

enum class NotificationType {
    VIDEO,
    PDF,
    COURSE,
    LIVE_CLASS,
    EXAM,
    RESULT,
    TEACHER_REPLY,
    NOTICE
}

data class CurrentUser(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: UserRole,
    val classGrade: String = "Class 12",
    val rollNumber: String = "101",
    val schoolName: String = "PM Shri Berbhngi HS School",
    val parentName: String = "M. Rahman",
    val photoUri: String = ""
)

data class NavItem(
    val route: String,
    val titleEn: String,
    val titleAs: String,
    val icon: String
)
