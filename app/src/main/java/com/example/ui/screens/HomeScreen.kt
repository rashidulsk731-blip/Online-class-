package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppLanguage
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel

@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    onNavigateToSubjects: () -> Unit,
    onNavigateToVideos: () -> Unit,
    onNavigateToLiveClasses: () -> Unit,
    onNavigateToPdfs: () -> Unit,
    onNavigateToExams: () -> Unit,
    onNavigateToResults: () -> Unit,
    onNavigateToAttendance: () -> Unit,
    onNavigateToDoubts: () -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToCourses: () -> Unit,
    onOpenVideo: (String) -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val selectedClass by viewModel.selectedClass.collectAsState()
    val courses by viewModel.courses.collectAsState()
    val videos by viewModel.videos.collectAsState()
    val liveClasses by viewModel.liveClasses.collectAsState()
    val studyMaterials by viewModel.studyMaterials.collectAsState()
    val exams by viewModel.exams.collectAsState()
    val results by viewModel.myResults.collectAsState()
    val attendanceList by viewModel.myAttendance.collectAsState()

    val totalAttendance = attendanceList.size
    val presentCount = attendanceList.count { it.isPresent }
    val attendancePct = if (totalAttendance > 0) (presentCount * 100) / totalAttendance else 85

    val isAs = currentLanguage == AppLanguage.ASSAMESE

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .testTag("home_screen_scroll"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // 1. Welcome & Class Grade Selector Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Blue800),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Blue900, Blue700)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isAs) "নমস্কাৰ," else "Welcome,",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Yellow300
                                )
                                Text(
                                    text = currentUser?.name ?: "Student",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Roll: ${currentUser?.rollNumber ?: "101"} • ${currentUser?.schoolName ?: "PM Shri Berbhngi HS School"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Blue100,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Class 11 / Class 12 Switcher Chip
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Yellow400,
                                modifier = Modifier.testTag("class_switcher_chip")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Class,
                                        contentDescription = "Class",
                                        tint = Slate800,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = selectedClass,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate800,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Toggle Buttons for Class 11 & Class 12
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Class 11", "Class 12").forEach { grade ->
                                val isCurrent = selectedClass == grade
                                FilterChip(
                                    selected = isCurrent,
                                    onClick = { viewModel.setSelectedClass(grade) },
                                    label = {
                                        Text(
                                            text = if (isAs) (if (grade == "Class 11") "একাদশ শ্ৰেণী" else "দ্বাদশ শ্ৰেণী") else grade,
                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Yellow400,
                                        selectedLabelColor = Slate800,
                                        containerColor = Color.White.copy(alpha = 0.15f),
                                        labelColor = Color.White
                                    ),
                                    border = null
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Continue Learning Banner
        item {
            SectionHeader(
                title = if (isAs) "অধ্যয়ন অব্যাহত ৰাখক" else "Continue Learning",
                actionText = if (isAs) "সকলো" else "View All",
                onActionClick = onNavigateToVideos
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onNavigateToVideos() }
                    .testTag("continue_learning_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Blue100),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircleFilled,
                            contentDescription = "Play Video",
                            tint = Blue700,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isAs) "অসমীয়া: ব্যাকৰণ আৰু সন্ধি" else "Assamese: Sandhi & Pratyay Rules",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate800,
                            maxLines = 1
                        )
                        Text(
                            text = "Teacher: Pranjal Sharma • 18m remaining",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { 0.65f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Yellow500,
                            trackColor = Slate200
                        )
                    }
                }
            }
        }

        // 3. Quick Action Hub (10 Features Grid)
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(
                title = if (isAs) "শ্ৰেণীৰ সেৱাসমূহ" else "Learning Hub",
                actionText = null,
                onActionClick = {}
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Row 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionTile(
                        title = if (isAs) "১০ টা বিষয়" else "10 Subjects",
                        subtitle = if (isAs) "সকলো পাঠ্যসূচী" else "Full Syllabus",
                        icon = Icons.Default.MenuBook,
                        badgeColor = Blue600,
                        modifier = Modifier.weight(1f).testTag("action_subjects"),
                        onClick = onNavigateToSubjects
                    )
                    QuickActionTile(
                        title = if (isAs) "ভিডিঅ' ক্লাছ" else "Video Class",
                        subtitle = if (isAs) "অধ্যায়ভিত্তিক" else "Chapter Lessons",
                        icon = Icons.Default.VideoLibrary,
                        badgeColor = Blue700,
                        modifier = Modifier.weight(1f).testTag("action_videos"),
                        onClick = onNavigateToVideos
                    )
                }

                // Row 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionTile(
                        title = if (isAs) "লাইভ ক্লাছ" else "Live Class",
                        subtitle = if (isAs) "প্ৰত্যক্ষ শ্ৰেণী" else "Join Interactive",
                        icon = Icons.Default.LiveTv,
                        badgeColor = PendingOrange,
                        modifier = Modifier.weight(1f).testTag("action_live"),
                        onClick = onNavigateToLiveClasses
                    )
                    QuickActionTile(
                        title = if (isAs) "নোটচ্ আৰু PDF" else "PDF & Notes",
                        subtitle = if (isAs) "ডিজিটেল লাইব্ৰেৰী" else "Digital Library",
                        icon = Icons.Default.PictureAsPdf,
                        badgeColor = ErrorRed,
                        modifier = Modifier.weight(1f).testTag("action_pdfs"),
                        onClick = onNavigateToPdfs
                    )
                }

                // Row 3
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionTile(
                        title = if (isAs) "অনলাইন পৰীক্ষা" else "Online Exam",
                        subtitle = if (isAs) "কুইজ আৰু নম্বৰ" else "Timed MCQ Tests",
                        icon = Icons.Default.Quiz,
                        badgeColor = Yellow600,
                        modifier = Modifier.weight(1f).testTag("action_exams"),
                        onClick = onNavigateToExams
                    )
                    QuickActionTile(
                        title = if (isAs) "ফলাফল আৰু মাৰ্কচ্" else "Result & Marks",
                        subtitle = if (isAs) "মাৰ্কশ্বীট" else "Report Cards",
                        icon = Icons.Default.EmojiEvents,
                        badgeColor = SuccessGreen,
                        modifier = Modifier.weight(1f).testTag("action_results"),
                        onClick = onNavigateToResults
                    )
                }

                // Row 4
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionTile(
                        title = if (isAs) "উপস্থিতি" else "Attendance",
                        subtitle = "$attendancePct% ${if (isAs) "উপস্থিত" else "Present"}",
                        icon = Icons.Default.EventAvailable,
                        badgeColor = Blue800,
                        modifier = Modifier.weight(1f).testTag("action_attendance"),
                        onClick = onNavigateToAttendance
                    )
                    QuickActionTile(
                        title = if (isAs) "প্ৰশ্ন সোধক" else "Ask Doubt",
                        subtitle = if (isAs) "শিক্ষকৰ সহাৰি" else "Teacher Help",
                        icon = Icons.Default.HelpOutline,
                        badgeColor = Yellow500,
                        modifier = Modifier.weight(1f).testTag("action_doubts"),
                        onClick = onNavigateToDoubts
                    )
                }

                // Row 5
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionTile(
                        title = if (isAs) "শিক্ষক বাৰ্তা" else "Messages",
                        subtitle = if (isAs) "ইন-এপ চ্যাট" else "Direct Chat",
                        icon = Icons.Default.Chat,
                        badgeColor = Blue500,
                        modifier = Modifier.weight(1f).testTag("action_messages"),
                        onClick = onNavigateToMessages
                    )
                    QuickActionTile(
                        title = if (isAs) "পাঠ্যক্ৰম (₹১০০)" else "Courses (₹100)",
                        subtitle = if (isAs) "UPI ভৰ্তি" else "Paid Enrolment",
                        icon = Icons.Default.CurrencyRupee,
                        badgeColor = SuccessGreen,
                        modifier = Modifier.weight(1f).testTag("action_courses"),
                        onClick = onNavigateToCourses
                    )
                }
            }
        }

        // 4. Upcoming Live Classes
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(
                title = if (isAs) "অনাগত লাইভ ক্লাছ" else "Upcoming Live Classes",
                actionText = if (isAs) "সকলো" else "See All",
                onActionClick = onNavigateToLiveClasses
            )

            if (liveClasses.isEmpty()) {
                Text(
                    text = if (isAs) "কোনো লাইভ ক্লাছ নাই" else "No live sessions scheduled",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Slate500
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(liveClasses.take(3)) { lc ->
                        LiveClassCard(
                            liveClass = lc,
                            isAs = isAs,
                            onJoinClick = onNavigateToLiveClasses
                        )
                    }
                }
            }
        }

        // 5. Attendance Summary Meter
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(
                title = if (isAs) "উপস্থিতি পৰিসংখ্যা" else "Attendance Tracker",
                actionText = if (isAs) "হিচাপ" else "Details",
                onActionClick = onNavigateToAttendance
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onNavigateToAttendance() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isAs) "মুঠ শ্ৰেণী: $totalAttendance দিন" else "Total Tracked: $totalAttendance Classes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate800
                        )
                        Text(
                            text = "${if (isAs) "উপস্থিত" else "Present"}: $presentCount | ${if (isAs) "অনুপস্থিত" else "Absent"}: ${totalAttendance - presentCount}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { attendancePct / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (attendancePct >= 75) SuccessGreen else ErrorRed,
                            trackColor = Slate200
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Surface(
                        shape = CircleShape,
                        color = if (attendancePct >= 75) SuccessGreenLight else ErrorRedLight
                    ) {
                        Box(
                            modifier = Modifier.size(56.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$attendancePct%",
                                fontWeight = FontWeight.Bold,
                                color = if (attendancePct >= 75) SuccessGreen else ErrorRed,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // 6. PM Shri School Notice Banner
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Yellow50),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = "Notice",
                        tint = Yellow600,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isAs) "বিদ্যালয়ৰ গুৰুত্বপূৰ্ণ জাননী" else "PM Shri Berbhngi School Notice",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Slate800
                        )
                        Text(
                            text = if (isAs)
                                "সকলো ছাত্ৰ-ছাত্ৰীক জনোৱা যায় যে অৰ্ধবাৰ্ষিক পৰীক্ষাৰ প্ৰস্তুতিৰ বাবে ডিজিটেল লাইব্ৰেৰীৰ পৰা আৰ্হি প্ৰশ্নকাকতসমূহ ডাউনলোড কৰি অনুশীলন কৰক।"
                            else
                                "All students of Class 11 and 12 are advised to download the model question papers from the PDF library for mid-term exam preparation.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate600,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    actionText: String?,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Slate800
        )
        if (actionText != null) {
            Text(
                text = actionText,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = Blue700,
                modifier = Modifier
                    .clickable { onActionClick() }
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun QuickActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badgeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(badgeColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = badgeColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate800,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate500,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun LiveClassCard(
    liveClass: com.example.data.local.LiveClassEntity,
    isAs: Boolean,
    onJoinClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .clickable { onJoinClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = PendingOrangeLight,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isAs) "লাইভ ক্লাছ" else "LIVE",
                        color = PendingOrange,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = liveClass.scheduledDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate500,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = liveClass.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Slate800,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Teacher: ${liveClass.teacher}",
                style = MaterialTheme.typography.bodySmall,
                color = Slate500
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onJoinClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Blue700)
            ) {
                Text(
                    text = if (isAs) "অংশগ্ৰহণ কৰক" else "Join Class",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
