package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SubjectEntity
import com.example.model.AppLanguage
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel

@Composable
fun SubjectsScreen(
    viewModel: AppViewModel,
    onSubjectClick: (SubjectEntity) -> Unit
) {
    val subjects by viewModel.subjects.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val selectedClass by viewModel.selectedClass.collectAsState()
    val isAs = currentLanguage == AppLanguage.ASSAMESE

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp)
            .testTag("subjects_screen")
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isAs) "নিৰ্ধাৰিত বিষয়সমূহ (১০ টা)" else "Prescribed Subjects (10)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Slate800
                )
                Text(
                    text = "$selectedClass • PM Shri Berbhngi HS School",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate500
                )
            }
        }
        Spacer(modifier = Modifier.height(14.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 90.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(subjects) { subject ->
                SubjectCard(
                    subject = subject,
                    isAs = isAs,
                    onClick = { onSubjectClick(subject) }
                )
            }
        }
    }
}

@Composable
fun SubjectCard(
    subject: SubjectEntity,
    isAs: Boolean,
    onClick: () -> Unit
) {
    val icon = getSubjectIcon(subject.id)
    val color = getSubjectColor(subject.id)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("subject_card_${subject.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = subject.nameEn,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (isAs) subject.nameAs else subject.nameEn,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Slate800,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = if (isAs) subject.nameEn else subject.nameAs,
                style = MaterialTheme.typography.bodySmall,
                color = Slate500,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Teacher: ${subject.teacherName}",
                style = MaterialTheme.typography.bodySmall,
                color = Blue700,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDetailScreen(
    subject: SubjectEntity,
    viewModel: AppViewModel,
    onBackClick: () -> Unit,
    onPlayVideo: (String) -> Unit,
    onReadPdf: (String) -> Unit,
    onTakeTest: (String) -> Unit
) {
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val isAs = currentLanguage == AppLanguage.ASSAMESE
    val videos by viewModel.videos.collectAsState()
    val studyMaterials by viewModel.studyMaterials.collectAsState()
    val exams by viewModel.exams.collectAsState()

    val subjectVideos = videos.filter { it.subjectId == subject.id }
    val subjectMaterials = studyMaterials.filter { it.subjectId == subject.id }
    val subjectExams = exams.filter { it.subjectId == subject.id }

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf(
        if (isAs) "ভিডিঅ' পাঠ" else "Videos",
        if (isAs) "নোটচ্ আৰু PDF" else "Notes & PDF",
        if (isAs) "অনলাইন পৰীক্ষা" else "Tests & Quiz",
        if (isAs) "শিক্ষক আৰু অগ্ৰগতি" else "Teacher & Info"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isAs) subject.nameAs else subject.nameEn,
                            fontWeight = FontWeight.Bold,
                            color = Slate800
                        )
                        Text(
                            text = subject.code,
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Slate800
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight)
        ) {
            // Tabs
            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = Blue800
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> {
                    // Video Lessons
                    if (subjectVideos.isEmpty()) {
                        EmptyStateBox(if (isAs) "কোনো ভিডিঅ' এতিয়াও যোগ কৰা হোৱা নাই" else "No video lessons uploaded yet for this subject.")
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(subjectVideos) { video ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onPlayVideo(video.id) },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(46.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Blue100),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = "Play",
                                                tint = Blue800
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = video.title,
                                                fontWeight = FontWeight.Bold,
                                                color = Slate800,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = "${video.chapter} • ${video.duration}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Slate500
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.Default.ChevronRight,
                                            contentDescription = "View",
                                            tint = Slate400
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // PDF Notes & Study Materials
                    if (subjectMaterials.isEmpty()) {
                        EmptyStateBox(if (isAs) "কোনো অধ্যয়ন সামগ্ৰী পোৱা নগ'ল" else "No study materials available yet.")
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(subjectMaterials) { mat ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onReadPdf(mat.id) },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(46.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(ErrorRedLight),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PictureAsPdf,
                                                contentDescription = "PDF",
                                                tint = ErrorRed
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = mat.title,
                                                fontWeight = FontWeight.Bold,
                                                color = Slate800,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = "${mat.category} • ${mat.fileSize} • ${mat.pageCount} Pages",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Slate500
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.Default.Download,
                                            contentDescription = "Read",
                                            tint = Blue800
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Online Tests & Quizzes
                    if (subjectExams.isEmpty()) {
                        EmptyStateBox(if (isAs) "কোনো সক্ৰিয় পৰীক্ষা নাই" else "No online exams scheduled for this subject currently.")
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(subjectExams) { exam ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onTakeTest(exam.id) },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = exam.title,
                                                fontWeight = FontWeight.Bold,
                                                color = Slate800,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            Surface(
                                                color = Yellow100,
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    text = "${exam.totalMarks} Marks",
                                                    color = Yellow600,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Time Limit: ${exam.durationMinutes} minutes • Date: ${exam.examDate}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Slate500
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Button(
                                            onClick = { onTakeTest(exam.id) },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = ButtonDefaults.buttonColors(containerColor = Blue800)
                                        ) {
                                            Text(if (isAs) "পৰীক্ষা আৰম্ভ কৰক" else "Start Exam Now")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else -> {
                    // Teacher Info & Progress
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = if (isAs) "বিষয় শিক্ষক" else "Subject Faculty",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate800
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Blue100
                                    ) {
                                        Box(
                                            modifier = Modifier.size(48.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = "Teacher",
                                                tint = Blue800
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = subject.teacherName,
                                            fontWeight = FontWeight.Bold,
                                            color = Slate800
                                        )
                                        Text(
                                            text = "Faculty of ${subject.nameEn} • PM Shri Berbhngi HS School",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Slate500
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = if (isAs) "পাঠ্যক্ৰমৰ বিৱৰণ" else "Subject Overview",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate800
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = subject.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Slate600,
                                    lineHeight = 20.sp
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = if (isAs) "সামগ্ৰিক অগ্ৰগতি (৬০%)" else "Overall Progress (60%)",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Slate700
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { 0.6f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = Yellow500,
                                    trackColor = Slate200
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateBox(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Empty",
                tint = Slate400,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                color = Slate500,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

fun getSubjectIcon(id: String): ImageVector {
    return when (id) {
        "sub_assamese" -> Icons.Default.MenuBook
        "sub_english" -> Icons.Default.Language
        "sub_education" -> Icons.Default.School
        "sub_iti" -> Icons.Default.Build
        "sub_pol_science" -> Icons.Default.Gavel
        "sub_healthcare" -> Icons.Default.LocalHospital
        "sub_sociology" -> Icons.Default.Groups
        "sub_bihu" -> Icons.Default.MusicNote
        "sub_history" -> Icons.Default.AccountBalance
        "sub_dance" -> Icons.Default.Celebration
        else -> Icons.Default.Book
    }
}

fun getSubjectColor(id: String): Color {
    return when (id) {
        "sub_assamese" -> Blue800
        "sub_english" -> Blue600
        "sub_education" -> Yellow600
        "sub_iti" -> PendingOrange
        "sub_pol_science" -> Blue900
        "sub_healthcare" -> ErrorRed
        "sub_sociology" -> SuccessGreen
        "sub_bihu" -> Yellow500
        "sub_history" -> Slate700
        "sub_dance" -> Color(0xFF9333EA)
        else -> Blue800
    }
}
