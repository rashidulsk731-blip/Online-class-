package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppLanguage
import com.example.model.UserRole
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel

@Composable
fun ProfileScreen(
    viewModel: AppViewModel,
    onNavigateToResults: () -> Unit,
    onNavigateToAttendance: () -> Unit,
    onNavigateToCourses: () -> Unit,
    onNavigateToAdminPanel: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val school by viewModel.school.collectAsState()
    val enrollments by viewModel.myEnrollments.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val isAs = currentLanguage == AppLanguage.ASSAMESE
    val context = LocalContext.current

    var showAboutSchoolDialog by remember { mutableStateOf(false) }
    var showAboutDeveloperDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp)
            .testTag("profile_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            // Profile Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Blue100),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (currentUser?.role) {
                                UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                                UserRole.TEACHER -> Icons.Default.School
                                else -> Icons.Default.Person
                            },
                            contentDescription = "User Avatar",
                            tint = Blue800,
                            modifier = Modifier.size(46.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = currentUser?.name ?: "Student Profile",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Slate800
                    )

                    Surface(
                        color = when (currentUser?.role) {
                            UserRole.ADMIN -> Yellow100
                            UserRole.TEACHER -> Blue100
                            else -> SuccessGreenLight
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = "${currentUser?.role?.name ?: "STUDENT"} • ${currentUser?.classGrade ?: "Class 12"}",
                            color = when (currentUser?.role) {
                                UserRole.ADMIN -> Yellow600
                                UserRole.TEACHER -> Blue800
                                else -> SuccessGreen
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = Slate100)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Detail list
                    ProfileDetailRow(label = if (isAs) "ৰোল নম্বৰ:" else "Roll Number:", value = currentUser?.rollNumber ?: "101")
                    ProfileDetailRow(label = if (isAs) "বিদ্যালয়:" else "School:", value = currentUser?.schoolName ?: "PM Shri Berbhngi HS School")
                    ProfileDetailRow(label = if (isAs) "ফোন নম্বৰ:" else "Phone:", value = currentUser?.phone ?: "9876543210")
                    ProfileDetailRow(label = if (isAs) "অভিভাৱক:" else "Guardian:", value = currentUser?.parentName ?: "Biren Nath")
                    ProfileDetailRow(label = if (isAs) "ভৰ্তি পাঠ্যক্ৰম:" else "Enrolled Courses:", value = "${enrollments.size} Active")
                }
            }
        }

        // Quick Admin / Teacher Desk shortcut
        if (currentUser?.role == UserRole.ADMIN || currentUser?.role == UserRole.TEACHER) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToAdminPanel() }
                        .testTag("admin_panel_shortcut"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Yellow500)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DashboardCustomize, contentDescription = null, tint = Slate800)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isAs) "শিক্ষক / এডমিন ডেশ্ববৰ্ড" else "Teacher & Admin Portal",
                                    fontWeight = FontWeight.Bold,
                                    color = Slate800
                                )
                                Text(
                                    text = if (isAs) "পাঠ্যক্ৰম, পৰীক্ষা আৰু নম্বৰ নিয়ন্ত্ৰণ" else "Manage courses, live classes, exams & attendance",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate700
                                )
                            }
                        }
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Slate800)
                    }
                }
            }
        }

        // Navigation Items
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isAs) "অধ্যয়ন আৰু তথ্য" else "Academic Records & Portals",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Slate800
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    ProfileMenuRow(
                        title = if (isAs) "মাৰ্কশ্বীট আৰু ফলাফল" else "My Examination Results",
                        icon = Icons.Default.EmojiEvents,
                        onClick = onNavigateToResults
                    )
                    Divider(color = Slate100)
                    ProfileMenuRow(
                        title = if (isAs) "উপস্থিতিৰ খতিয়ান" else "Attendance Log",
                        icon = Icons.Default.EventAvailable,
                        onClick = onNavigateToAttendance
                    )
                    Divider(color = Slate100)
                    ProfileMenuRow(
                        title = if (isAs) "ভৰ্তি হোৱা পাঠ্যক্ৰম" else "My Enrolled Courses",
                        icon = Icons.Default.School,
                        onClick = onNavigateToCourses
                    )
                }
            }
        }

        // App & Institution Info (About School & About Developer)
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isAs) "বিদ্যালয় আৰু বিকাশকৰ তথ্য" else "School & Developer Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Slate800
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    ProfileMenuRow(
                        title = if (isAs) "বিদ্যালয়ৰ বিষয়ে (PM Shri Berbhngi HS School)" else "About School (PM Shri Berbhngi)",
                        icon = Icons.Default.AccountBalance,
                        onClick = { showAboutSchoolDialog = true }
                    )
                    Divider(color = Slate100)
                    ProfileMenuRow(
                        title = if (isAs) "এপ বিকাশকৰ সবিশেষ (Rashidul Sk)" else "About Developer (Rashidul Sk)",
                        icon = Icons.Default.Code,
                        onClick = { showAboutDeveloperDialog = true }
                    )
                    Divider(color = Slate100)
                    ProfileMenuRow(
                        title = if (isAs) "ভাষা সলনি কৰক (English / অসমীয়া)" else "Language (English / অসমীয়া)",
                        icon = Icons.Default.Translate,
                        onClick = { viewModel.toggleLanguage() }
                    )
                }
            }
        }

        // Quick Role Switcher for Evaluator/Reviewer Testing
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Instant Role Switcher (Demo / Testing)",
                style = MaterialTheme.typography.bodySmall,
                color = Slate500,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.switchRoleDemo(UserRole.STUDENT) },
                    colors = ButtonDefaults.buttonColors(containerColor = if (currentUser?.role == UserRole.STUDENT) Blue800 else Slate400),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Student", fontSize = 11.sp)
                }
                Button(
                    onClick = { viewModel.switchRoleDemo(UserRole.TEACHER) },
                    colors = ButtonDefaults.buttonColors(containerColor = if (currentUser?.role == UserRole.TEACHER) Blue800 else Slate400),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Teacher", fontSize = 11.sp)
                }
                Button(
                    onClick = { viewModel.switchRoleDemo(UserRole.ADMIN) },
                    colors = ButtonDefaults.buttonColors(containerColor = if (currentUser?.role == UserRole.ADMIN) Blue800 else Slate400),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Admin", fontSize = 11.sp)
                }
            }
        }

        // Logout
        item {
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedButton(
                onClick = onLogoutClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isAs) "লগআউট কৰক" else "Sign Out")
            }
        }
    }

    // About School Dialog
    if (showAboutSchoolDialog) {
        val s = school
        AlertDialog(
            onDismissRequest = { showAboutSchoolDialog = false },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_app_logo),
                    contentDescription = "School Logo",
                    modifier = Modifier.size(54.dp)
                )
            },
            title = {
                Text(
                    text = s?.name ?: "PM Shri Berbhngi HS School",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Address: ${s?.address ?: "Berbhngi, Barpeta / Bongaigaon, Assam - 781318"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate700
                    )
                    Text(
                        text = "Head / Principal: ${s?.adminName ?: "Senior Academic Council"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate700
                    )
                    Text(
                        text = "Contact Phone: ${s?.contactNumber ?: "9387628351"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate700
                    )
                    Text(
                        text = "Email: ${s?.email ?: "rashidul728@gmail.com"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate700
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "PM Shri Berbhngi Higher Secondary School is a premier government higher secondary education institution committed to excellence in academic curriculum, smart learning, and comprehensive student mentoring for Class 11 and 12.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${s?.contactNumber ?: "9387628351"}"))
                        try { context.startActivity(intent) } catch (e: Exception) {}
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Blue800)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isAs) "বিদ্যালয়লৈ কল কৰক" else "Call School")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAboutSchoolDialog = false }) {
                    Text(if (isAs) "বন্ধ কৰক" else "Close")
                }
            }
        )
    }

    // About Developer Dialog (Requested by User)
    if (showAboutDeveloperDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDeveloperDialog = false },
            icon = {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Blue800),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = "Developer",
                        tint = Yellow400,
                        modifier = Modifier.size(32.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "App Developer Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "App Developer Name: Rashidul Sk",
                        fontWeight = FontWeight.Bold,
                        color = Blue900
                    )
                    Text(
                        text = "App Developer Contact: 9387628351",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate800
                    )
                    Text(
                        text = "App Developer Email: rashidul728@gmail.com",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate800
                    )
                    Text(
                        text = "App Developer Experience: 1 Year",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate800
                    )
                    Text(
                        text = "App Developer Portfolio: Not provided",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate500
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "About Developer: Dedicated Android and full-stack software engineer building reliable, modern, accessible educational systems for schools and students across Assam.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:9387628351"))
                        try { context.startActivity(intent) } catch (e: Exception) {}
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Blue800)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Call Developer")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:rashidul728@gmail.com")
                            putExtra(Intent.EXTRA_SUBJECT, "Student Education App Query")
                        }
                        try { context.startActivity(intent) } catch (e: Exception) {}
                    }
                ) {
                    Text("Send Email")
                }
            }
        )
    }
}

@Composable
fun ProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Slate500, style = MaterialTheme.typography.bodySmall)
        Text(text = value, fontWeight = FontWeight.Bold, color = Slate800, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun ProfileMenuRow(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = Blue800, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Text(text = title, fontWeight = FontWeight.SemiBold, color = Slate800, fontSize = 14.sp)
        }
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Slate400)
    }
}
