package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppLanguage
import com.example.model.UserRole
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel

@Composable
fun LoginScreen(
    viewModel: AppViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val isAs = currentLanguage == AppLanguage.ASSAMESE

    var emailOrPhone by remember { mutableStateOf("student@school.edu") }
    var password by remember { mutableStateOf("pass123") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(24.dp)
            .testTag("login_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App / School Logo
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(4.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_app_logo),
                contentDescription = "School Logo",
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (isAs) "ষ্টুডেণ্ট এডুকেশ্বন" else "Student Education",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Slate800
        )
        Text(
            text = "PM Shri Berbhngi HS School",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = Blue700
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = if (isAs) "ছাত্ৰ-ছাত্ৰী প্ৰৱেশ (Login)" else "Student & Staff Login",
                    fontWeight = FontWeight.Bold,
                    color = Slate800,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = emailOrPhone,
                    onValueChange = {
                        emailOrPhone = it
                        errorMessage = null
                    },
                    label = { Text(if (isAs) "ইমেইল বা ফোন নম্বৰ" else "Email or Phone Number") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_email_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    label = { Text(if (isAs) "পাছৱৰ্ড" else "Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_password_input"),
                    singleLine = true
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorMessage!!, color = ErrorRed, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        isLoading = true
                        viewModel.login(emailOrPhone, password) { success, msg ->
                            isLoading = false
                            if (success) {
                                onLoginSuccess()
                            } else {
                                errorMessage = msg
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("login_submit_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue800),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text(if (isAs) "লগইন কৰক" else "Sign In", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isAs) "নতুন একাউণ্ট খুলিব নে? " else "Don't have an account? ",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )
                    Text(
                        text = if (isAs) "পঞ্জীয়ন কৰক" else "Register",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Blue800,
                        modifier = Modifier
                            .clickable { onNavigateToRegister() }
                            .testTag("nav_to_register_btn")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Demo Logins
        Text(
            text = "Quick Demo Access (Instant Login):",
            style = MaterialTheme.typography.bodySmall,
            color = Slate500,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledTonalButton(
                onClick = {
                    viewModel.switchRoleDemo(UserRole.STUDENT)
                    onLoginSuccess()
                },
                modifier = Modifier.weight(1f).testTag("quick_student_login")
            ) {
                Text("Student", fontSize = 11.sp)
            }
            FilledTonalButton(
                onClick = {
                    viewModel.switchRoleDemo(UserRole.TEACHER)
                    onLoginSuccess()
                },
                modifier = Modifier.weight(1f).testTag("quick_teacher_login")
            ) {
                Text("Teacher", fontSize = 11.sp)
            }
            FilledTonalButton(
                onClick = {
                    viewModel.switchRoleDemo(UserRole.ADMIN)
                    onLoginSuccess()
                },
                modifier = Modifier.weight(1f).testTag("quick_admin_login")
            ) {
                Text("Admin", fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun RegisterScreen(
    viewModel: AppViewModel,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val isAs = currentLanguage == AppLanguage.ASSAMESE

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var classGrade by remember { mutableStateOf("Class 12") }
    var rollNumber by remember { mutableStateOf("") }
    var parentName by remember { mutableStateOf("") }
    var schoolName by remember { mutableStateOf("PM Shri Berbhngi HS School") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(20.dp)
            .testTag("register_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = if (isAs) "নতুন শিক্ষাৰ্থী পঞ্জীয়ন" else "Student Registration",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Slate800
            )
            Text(
                text = "PM Shri Berbhngi HS School • Class 11-12",
                style = MaterialTheme.typography.bodySmall,
                color = Slate500
            )
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text(if (isAs) "সম্পূৰ্ণ নাম" else "Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text(if (isAs) "ফোন নম্বৰ" else "Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(if (isAs) "পাছৱৰ্ড" else "Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Class selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Class 11", "Class 12").forEach { grade ->
                            FilterChip(
                                selected = classGrade == grade,
                                onClick = { classGrade = grade },
                                label = { Text(grade) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = rollNumber,
                        onValueChange = { rollNumber = it },
                        label = { Text(if (isAs) "ৰোল নম্বৰ" else "Class Roll Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = parentName,
                        onValueChange = { parentName = it },
                        label = { Text(if (isAs) "অভিভাৱকৰ নাম" else "Parent / Guardian Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = schoolName,
                        onValueChange = { schoolName = it },
                        label = { Text(if (isAs) "বিদ্যালয়ৰ নাম" else "School Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = errorMessage!!, color = ErrorRed, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (fullName.isBlank() || phone.isBlank() || password.isBlank()) {
                                errorMessage = "Please fill in all required fields."
                            } else {
                                isSubmitting = true
                                viewModel.registerStudent(
                                    name = fullName,
                                    email = email.ifBlank { "${phone}@school.edu" },
                                    phone = phone,
                                    password = password,
                                    classGrade = classGrade,
                                    rollNumber = rollNumber.ifBlank { "101" },
                                    parentName = parentName.ifBlank { "Guardian" },
                                    schoolName = schoolName,
                                    photoUri = ""
                                ) { success, msg ->
                                    isSubmitting = false
                                    if (success) {
                                        onRegisterSuccess()
                                    } else {
                                        errorMessage = msg
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("register_submit_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Blue800)
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Text(if (isAs) "পঞ্জীয়ন সম্পূৰ্ণ কৰক" else "Complete Registration")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Already have an account? ", style = MaterialTheme.typography.bodySmall)
                        Text(
                            text = "Login here",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Blue800,
                            modifier = Modifier.clickable { onNavigateToLogin() }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
