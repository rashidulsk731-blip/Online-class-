package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.data.local.DoubtEntity
import com.example.model.AppLanguage
import com.example.model.UserRole
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoubtScreen(
    viewModel: AppViewModel
) {
    val doubts by viewModel.myDoubts.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val isAs = currentLanguage == AppLanguage.ASSAMESE

    var showAskDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: All, 1: Pending, 2: Answered / Solved
    var replyDoubtTarget by remember { mutableStateOf<DoubtEntity?>(null) }
    var replyInputText by remember { mutableStateOf("") }

    val filteredDoubts = when (selectedTab) {
        1 -> doubts.filter { it.status == "PENDING" }
        2 -> doubts.filter { it.status == "ANSWERED" || it.status == "SOLVED" }
        else -> doubts
    }

    Scaffold(
        floatingActionButton = {
            if (currentUser?.role == UserRole.STUDENT) {
                ExtendedFloatingActionButton(
                    onClick = { showAskDialog = true },
                    containerColor = Blue800,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.AddComment, contentDescription = "Ask") },
                    text = { Text(if (isAs) "প্ৰশ্ন সোধক" else "Ask Doubt") },
                    modifier = Modifier.testTag("ask_doubt_fab")
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight)
                .padding(horizontal = 16.dp)
                .testTag("doubt_screen")
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isAs) "সংশয় আৰু প্ৰশ্ন সমাধান" else "Doubt Clearing Desk",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Slate800
            )
            Text(
                text = if (isAs) "শিক্ষকৰ সৈতে পোনপটীয়া প্ৰশ্নোত্তৰ আৰু সমাধান" else "Ask academic questions directly to subject teachers and get expert solutions",
                style = MaterialTheme.typography.bodySmall,
                color = Slate500
            )
            Spacer(modifier = Modifier.height(14.dp))

            // Filter Tabs
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Blue800
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(if (isAs) "সকলো (${doubts.size})" else "All (${doubts.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(if (isAs) "বিচাৰাধীন" else "Pending") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text(if (isAs) "উত্তৰ দিয়া" else "Answered") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredDoubts.isEmpty()) {
                EmptyStateBox(if (isAs) "কোনো প্ৰশ্ন পোৱা নগ'ল" else "No doubts found under this category.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    items(filteredDoubts) { doubt ->
                        DoubtCard(
                            doubt = doubt,
                            isAs = isAs,
                            isTeacherOrAdmin = currentUser?.role == UserRole.TEACHER || currentUser?.role == UserRole.ADMIN,
                            onReply = { replyDoubtTarget = doubt },
                            onMarkSolved = { viewModel.markDoubtSolved(doubt) }
                        )
                    }
                }
            }
        }
    }

    // Ask Doubt Dialog
    if (showAskDialog) {
        var selectedSubject by remember { mutableStateOf(subjects.firstOrNull()?.id ?: "sub_assamese") }
        var chapterText by remember { mutableStateOf("") }
        var questionText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAskDialog = false },
            title = { Text(if (isAs) "নতুন প্ৰশ্ন সোধক" else "Submit New Academic Doubt") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Select Subject:", style = MaterialTheme.typography.bodySmall, color = Slate600)
                    Spacer(modifier = Modifier.height(4.dp))

                    ScrollableTabRow(
                        selectedTabIndex = subjects.indexOfFirst { it.id == selectedSubject }.coerceAtLeast(0),
                        edgePadding = 0.dp,
                        containerColor = Color.Transparent
                    ) {
                        subjects.forEach { s ->
                            Tab(
                                selected = selectedSubject == s.id,
                                onClick = { selectedSubject = s.id },
                                text = { Text(if (isAs) s.nameAs else s.nameEn, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = chapterText,
                        onValueChange = { chapterText = it },
                        label = { Text(if (isAs) "অধ্যায়ৰ নাম" else "Chapter Name / Topic") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = questionText,
                        onValueChange = { questionText = it },
                        label = { Text(if (isAs) "আপোনাৰ প্ৰশ্ন লিখক" else "Type your doubt description...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 5
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (questionText.isNotBlank()) {
                            viewModel.askDoubt(
                                subjectId = selectedSubject,
                                chapter = chapterText.ifBlank { "General Topic" },
                                questionText = questionText
                            ) {
                                showAskDialog = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Blue800),
                    enabled = questionText.isNotBlank()
                ) {
                    Text(if (isAs) "প্ৰেৰণ কৰক" else "Submit Doubt")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAskDialog = false }) {
                    Text(if (isAs) "বাতিল" else "Cancel")
                }
            }
        )
    }

    // Teacher Reply Dialog
    if (replyDoubtTarget != null) {
        val target = replyDoubtTarget!!
        AlertDialog(
            onDismissRequest = { replyDoubtTarget = null },
            title = { Text(if (isAs) "প্ৰশ্নৰ উত্তৰ দিয়ক" else "Teacher Explanation & Reply") },
            text = {
                Column {
                    Text("Student: ${target.studentName}", fontWeight = FontWeight.Bold, color = Blue800)
                    Text("Question: ${target.question}", style = MaterialTheme.typography.bodyMedium, color = Slate700)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = replyInputText,
                        onValueChange = { replyInputText = it },
                        label = { Text(if (isAs) "উত্তৰ / ব্যাখ্যা লিখক..." else "Type detailed teacher reply...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 5
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (replyInputText.isNotBlank()) {
                            viewModel.replyDoubt(target, replyInputText)
                            replyInputText = ""
                            replyDoubtTarget = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Blue800),
                    enabled = replyInputText.isNotBlank()
                ) {
                    Text(if (isAs) "উত্তৰ প্ৰেৰণ কৰক" else "Post Reply")
                }
            },
            dismissButton = {
                TextButton(onClick = { replyDoubtTarget = null }) {
                    Text(if (isAs) "বাতিল" else "Cancel")
                }
            }
        )
    }
}

@Composable
fun DoubtCard(
    doubt: DoubtEntity,
    isAs: Boolean,
    isTeacherOrAdmin: Boolean,
    onReply: () -> Unit,
    onMarkSolved: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = when (doubt.status) {
                        "SOLVED" -> SuccessGreenLight
                        "ANSWERED" -> Blue100
                        else -> Yellow100
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = when (doubt.status) {
                            "SOLVED" -> if (isAs) "সমাধান হ'ল" else "SOLVED"
                            "ANSWERED" -> if (isAs) "উত্তৰ দিয়া হ'ল" else "ANSWERED"
                            else -> if (isAs) "বিচাৰাধীন" else "PENDING"
                        },
                        color = when (doubt.status) {
                            "SOLVED" -> SuccessGreen
                            "ANSWERED" -> Blue800
                            else -> Yellow600
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Text(
                    text = "${doubt.studentName} • ${doubt.date}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate500
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Topic: ${doubt.chapter}",
                style = MaterialTheme.typography.bodySmall,
                color = Blue800,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = doubt.question,
                style = MaterialTheme.typography.bodyMedium,
                color = Slate800,
                fontWeight = FontWeight.Medium,
                lineHeight = 20.sp
            )

            if (doubt.reply.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Blue50)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = "Teacher",
                                tint = Blue800,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = doubt.repliedBy,
                                fontWeight = FontWeight.Bold,
                                color = Blue900,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = doubt.reply,
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate800,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            if (isTeacherOrAdmin) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onMarkSolved,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(if (isAs) "সমাধান চিহ্নিত কৰক" else "Mark Solved")
                    }
                    Button(
                        onClick = onReply,
                        colors = ButtonDefaults.buttonColors(containerColor = Blue800)
                    ) {
                        Text(if (isAs) "উত্তৰ দিয়ক" else "Reply")
                    }
                }
            }
        }
    }
}
