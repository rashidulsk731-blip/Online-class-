package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTeacherPanelScreen(
    viewModel: AppViewModel,
    onBackClick: () -> Unit
) {
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val isAs = currentLanguage == AppLanguage.ASSAMESE
    val students by viewModel.allStudents.collectAsState()
    val videos by viewModel.videos.collectAsState()
    val materials by viewModel.studyMaterials.collectAsState()
    val liveClasses by viewModel.liveClasses.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val allPayments by viewModel.allPayments.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        if (isAs) "ছাত্ৰ-ছাত্ৰী" else "Students",
        if (isAs) "পাঠদান যোগ" else "Add Content",
        if (isAs) "উপস্থিতি চিহ্নিতকৰণ" else "Attendance",
        if (isAs) "ফলাফল প্ৰকাশ" else "Add Result",
        if (isAs) "জাননী প্ৰচাৰ" else "Broadcast"
    )

    // Modals
    var showAddVideoDialog by remember { mutableStateOf(false) }
    var showAddMaterialDialog by remember { mutableStateOf(false) }
    var showAddLiveClassDialog by remember { mutableStateOf(false) }
    var showAddResultDialog by remember { mutableStateOf(false) }
    var showBroadcastDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(if (isAs) "শিক্ষক আৰু এডমিন নিয়ন্ত্ৰণ কক্ষ" else "Teacher & Admin Control Desk")
                        Text(
                            text = "PM Shri Berbhngi HS School Management",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Blue800,
                edgePadding = 12.dp
            ) {
                tabs.forEachIndexed { idx, title ->
                    Tab(
                        selected = selectedTab == idx,
                        onClick = { selectedTab = idx },
                        text = { Text(title, fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    // Students Directory & Payments Overview
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Yellow50)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Payments, contentDescription = null, tint = Yellow600)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Total UPI Course Collections: ₹${allPayments.size * 100}.00",
                                            fontWeight = FontWeight.Bold,
                                            color = Slate800
                                        )
                                        Text(
                                            text = "${allPayments.size} Paid Course Enrolments Recorded",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Slate600
                                        )
                                    }
                                }
                            }
                        }

                        items(students) { st ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "Roll ${st.rollNumber} - ${st.userId.replace("_", " ").replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}",
                                            fontWeight = FontWeight.Bold,
                                            color = Slate800
                                        )
                                        Text(
                                            text = "${st.classGrade} • Guardian: ${st.parentName}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Slate500
                                        )
                                        Text(
                                            text = "Contact: ${st.phone}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Blue700
                                        )
                                    }
                                    Surface(
                                        color = SuccessGreenLight,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "ACTIVE",
                                            color = SuccessGreen,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Content Management (Upload Video, PDF, Live Class)
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Button(
                                onClick = { showAddVideoDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Blue800)
                            ) {
                                Icon(Icons.Default.VideoCall, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isAs) "নতুন ভিডিঅ' ক্লাছ যোগ কৰক" else "Add New Video Lecture")
                            }
                        }

                        item {
                            Button(
                                onClick = { showAddMaterialDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Blue700)
                            ) {
                                Icon(Icons.Default.UploadFile, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isAs) "পিডিএফ / অধ্যয়ন সামগ্ৰী আপলোড" else "Upload PDF / Study Notes")
                            }
                        }

                        item {
                            Button(
                                onClick = { showAddLiveClassDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = PendingOrange)
                            ) {
                                Icon(Icons.Default.LiveTv, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isAs) "লাইভ ক্লাছ সময়সূচী নিৰ্ধাৰণ" else "Schedule New Live Class")
                            }
                        }

                        item {
                            Text(
                                text = "Current Active Videos (${videos.size}):",
                                fontWeight = FontWeight.Bold,
                                color = Slate800,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        }

                        items(videos) { vid ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(vid.title, fontWeight = FontWeight.Bold, color = Slate800)
                                        Text("${vid.chapter} • ${vid.duration}", style = MaterialTheme.typography.bodySmall, color = Slate500)
                                    }
                                    IconButton(onClick = { viewModel.deleteVideo(vid) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ErrorRed)
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Mark Attendance for Class
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text(
                                text = "Mark Attendance for Today (Class 12):",
                                fontWeight = FontWeight.Bold,
                                color = Slate800
                            )
                        }

                        items(students) { st ->
                            var isPresent by remember { mutableStateOf(true) }
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "Roll ${st.rollNumber} • ${st.userId}",
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = st.schoolName,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Slate500
                                        )
                                    }

                                    Row {
                                        FilterChip(
                                            selected = isPresent,
                                            onClick = {
                                                isPresent = true
                                                viewModel.markAttendance(st, true, "2026-09-23", "Present in classroom")
                                            },
                                            label = { Text("Present") },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = SuccessGreenLight,
                                                selectedLabelColor = SuccessGreen
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        FilterChip(
                                            selected = !isPresent,
                                            onClick = {
                                                isPresent = false
                                                viewModel.markAttendance(st, false, "2026-09-23", "Absent with leave")
                                            },
                                            label = { Text("Absent") },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = ErrorRedLight,
                                                selectedLabelColor = ErrorRed
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                3 -> {
                    // Add / Publish Student Result
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp)
                    ) {
                        Button(
                            onClick = { showAddResultDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Blue800)
                        ) {
                            Icon(Icons.Default.AddChart, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isAs) "নতুন নম্বৰপত্ৰিকা প্ৰকাশ কৰক" else "Publish Exam Marksheet")
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Published Marks & Scorecards:",
                            fontWeight = FontWeight.Bold,
                            color = Slate800
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val allRes by viewModel.allResults.collectAsState()
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(allRes) { res ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = "${res.studentName} (Roll: ${res.rollNumber})",
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "${res.subjectName} - ${res.examName}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Slate500
                                            )
                                        }
                                        Text(
                                            text = "${res.obtainedMarks}/${res.totalMarks} (${res.grade})",
                                            fontWeight = FontWeight.Bold,
                                            color = Blue800
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                else -> {
                    // School Broadcast Notice
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Button(
                            onClick = { showBroadcastDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Blue800)
                        ) {
                            Icon(Icons.Default.Campaign, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isAs) "নতুন বিদ্যালয় জাননী প্ৰেৰণ কৰক" else "Broadcast Notice to All Students")
                        }
                    }
                }
            }
        }
    }

    // Add Video Dialog
    if (showAddVideoDialog) {
        var title by remember { mutableStateOf("") }
        var chapter by remember { mutableStateOf("") }
        var duration by remember { mutableStateOf("25 mins") }
        var selectedSubject by remember { mutableStateOf(subjects.firstOrNull()?.id ?: "sub_assamese") }

        AlertDialog(
            onDismissRequest = { showAddVideoDialog = false },
            title = { Text("Add Video Lesson") },
            text = {
                Column {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Video Title") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = chapter, onValueChange = { chapter = it }, label = { Text("Chapter") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Duration (e.g. 20 mins)") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.addVideo(
                                title = title,
                                subjectId = selectedSubject,
                                chapter = chapter.ifBlank { "Unit 1" },
                                duration = duration,
                                url = "https://school.edu/video.mp4",
                                classGrade = "Class 12"
                            )
                            showAddVideoDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Blue800)
                ) { Text("Save Video") }
            },
            dismissButton = { TextButton(onClick = { showAddVideoDialog = false }) { Text("Cancel") } }
        )
    }

    // Upload PDF Dialog
    if (showAddMaterialDialog) {
        var title by remember { mutableStateOf("") }
        var chapter by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Notes") }
        var content by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddMaterialDialog = false },
            title = { Text("Upload PDF / Study Material") },
            text = {
                Column {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Document Title") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = chapter, onValueChange = { chapter = it }, label = { Text("Chapter") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Study Text Content") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.addStudyMaterial(
                                title = title,
                                subjectId = "sub_assamese",
                                chapter = chapter.ifBlank { "Chapter 1" },
                                category = category,
                                fileUrl = "https://school.edu/notes.pdf",
                                contentPreview = content.ifBlank { "Essential revision points for the upcoming term exam." },
                                classGrade = "Class 12"
                            )
                            showAddMaterialDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Blue800)
                ) { Text("Upload") }
            },
            dismissButton = { TextButton(onClick = { showAddMaterialDialog = false }) { Text("Cancel") } }
        )
    }

    // Schedule Live Class Dialog
    if (showAddLiveClassDialog) {
        var title by remember { mutableStateOf("") }
        var date by remember { mutableStateOf("2026-09-25") }
        var time by remember { mutableStateOf("11:00 AM") }

        AlertDialog(
            onDismissRequest = { showAddLiveClassDialog = false },
            title = { Text("Schedule Live Class") },
            text = {
                Column {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Topic Title") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Time (e.g. 10:30 AM)") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.addLiveClass(
                                title = title,
                                subjectId = "sub_education",
                                classGrade = "Class 12",
                                date = date,
                                time = time,
                                joinUrl = "https://meet.google.com/live-pmshri"
                            )
                            showAddLiveClassDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Blue800)
                ) { Text("Schedule") }
            },
            dismissButton = { TextButton(onClick = { showAddLiveClassDialog = false }) { Text("Cancel") } }
        )
    }

    // Add Result Dialog
    if (showAddResultDialog) {
        var rollNo by remember { mutableStateOf("101") }
        var studentName by remember { mutableStateOf("Manash Jyoti Nath") }
        var subjectName by remember { mutableStateOf("Assamese") }
        var examName by remember { mutableStateOf("Unit Test 2") }
        var totalMarks by remember { mutableStateOf("50") }
        var obtainedMarks by remember { mutableStateOf("45") }

        AlertDialog(
            onDismissRequest = { showAddResultDialog = false },
            title = { Text("Publish Result Marks") },
            text = {
                Column {
                    OutlinedTextField(value = studentName, onValueChange = { studentName = it }, label = { Text("Student Name") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = rollNo, onValueChange = { rollNo = it }, label = { Text("Roll Number") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = subjectName, onValueChange = { subjectName = it }, label = { Text("Subject") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    Row {
                        OutlinedTextField(value = totalMarks, onValueChange = { totalMarks = it }, label = { Text("Total") }, modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(6.dp))
                        OutlinedTextField(value = obtainedMarks, onValueChange = { obtainedMarks = it }, label = { Text("Obtained") }, modifier = Modifier.weight(1f))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addResult(
                            studentId = "student_1",
                            studentName = studentName,
                            rollNumber = rollNo,
                            subjectId = "sub_assamese",
                            subjectName = subjectName,
                            examName = examName,
                            totalMarks = totalMarks.toIntOrNull() ?: 50,
                            obtainedMarks = obtainedMarks.toIntOrNull() ?: 45
                        )
                        showAddResultDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Blue800)
                ) { Text("Publish") }
            },
            dismissButton = { TextButton(onClick = { showAddResultDialog = false }) { Text("Cancel") } }
        )
    }

    // Broadcast Notice Dialog
    if (showBroadcastDialog) {
        var noticeTitle by remember { mutableStateOf("") }
        var noticeMsg by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showBroadcastDialog = false },
            title = { Text("Broadcast School Notice") },
            text = {
                Column {
                    OutlinedTextField(value = noticeTitle, onValueChange = { noticeTitle = it }, label = { Text("Notice Title") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = noticeMsg, onValueChange = { noticeMsg = it }, label = { Text("Announcement Message") }, modifier = Modifier.fillMaxWidth(), maxLines = 4)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (noticeTitle.isNotBlank()) {
                            viewModel.broadcastNotification(
                                titleEn = noticeTitle,
                                titleAs = noticeTitle,
                                msgEn = noticeMsg,
                                msgAs = noticeMsg,
                                type = "NOTICE"
                            )
                            showBroadcastDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Blue800)
                ) { Text("Broadcast") }
            },
            dismissButton = { TextButton(onClick = { showBroadcastDialog = false }) { Text("Cancel") } }
        )
    }
}
