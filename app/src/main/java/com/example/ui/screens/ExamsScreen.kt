package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ExamEntity
import com.example.model.AppLanguage
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel

@Composable
fun ExamsScreen(
    viewModel: AppViewModel,
    onStartExamClick: (ExamEntity) -> Unit
) {
    val exams by viewModel.exams.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val isAs = currentLanguage == AppLanguage.ASSAMESE

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp)
            .testTag("exams_screen")
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (isAs) "অনলাইন পৰীক্ষা আৰু কুইজ" else "Online Exams & Quizzes",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Slate800
        )
        Text(
            text = if (isAs) "সময় নিৰ্ধাৰিত বহু-বিকল্পভিত্তিক পৰীক্ষা (MCQ)" else "Timed multiple choice assessments with instant evaluation",
            style = MaterialTheme.typography.bodySmall,
            color = Slate500
        )
        Spacer(modifier = Modifier.height(14.dp))

        if (exams.isEmpty()) {
            EmptyStateBox(if (isAs) "কোনো পৰীক্ষা উপলব্ধ নাই" else "No exams scheduled currently.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                items(exams) { exam ->
                    ExamCard(
                        exam = exam,
                        isAs = isAs,
                        onStart = { onStartExamClick(exam) }
                    )
                }
            }
        }
    }
}

@Composable
fun ExamCard(
    exam: ExamEntity,
    isAs: Boolean,
    onStart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("exam_card_${exam.id}"),
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
                    color = Blue100,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (exam.isActive) (if (isAs) "সক্ৰিয় পৰীক্ষা" else "ACTIVE TEST") else "CLOSED",
                        color = Blue800,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Text(
                    text = "${exam.durationMinutes} mins",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate500,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = exam.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Slate800
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Total Marks: ${exam.totalMarks} • Date: ${exam.examDate}",
                style = MaterialTheme.typography.bodySmall,
                color = Slate500
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = exam.instructions,
                style = MaterialTheme.typography.bodySmall,
                color = Slate600
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("start_exam_btn_${exam.id}"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Blue800)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isAs) "পৰীক্ষাত অংশ লওক" else "Start Examination",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamTakingScreen(
    viewModel: AppViewModel,
    onBackClick: () -> Unit,
    onViewResult: () -> Unit
) {
    val exam by viewModel.activeExam.collectAsState()
    val questions by viewModel.activeExamQuestions.collectAsState()
    val currentIndex by viewModel.currentQuestionIndex.collectAsState()
    val answers by viewModel.studentAnswers.collectAsState()
    val remainingSeconds by viewModel.examRemainingSeconds.collectAsState()
    val examResult by viewModel.examFinishedResult.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val isAs = currentLanguage == AppLanguage.ASSAMESE

    var showSubmitConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(examResult) {
        if (examResult != null) {
            onViewResult()
        }
    }

    if (exam == null || questions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Blue800)
        }
        return
    }

    val currentQ = questions.getOrNull(currentIndex)
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = exam?.title ?: "Exam",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Question ${currentIndex + 1} of ${questions.size}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Timer Badge
                    Surface(
                        color = if (remainingSeconds < 180) ErrorRedLight else Yellow100,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Timer",
                                tint = if (remainingSeconds < 180) ErrorRed else Yellow600,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = timeFormatted,
                                fontWeight = FontWeight.Bold,
                                color = if (remainingSeconds < 180) ErrorRed else Yellow600,
                                fontSize = 13.sp
                            )
                        }
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
                .padding(16.dp)
        ) {
            // Progress meter
            LinearProgressIndicator(
                progress = { (currentIndex + 1).toFloat() / questions.size.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Blue800,
                trackColor = Slate200
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (currentQ != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Q${currentIndex + 1}. ${currentQ.questionText}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate800,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        val options = listOf(currentQ.optionA, currentQ.optionB, currentQ.optionC, currentQ.optionD)
                        val selectedOpt = answers[currentIndex]

                        options.forEachIndexed { optIndex, optText ->
                            val isSelected = selectedOpt == optIndex
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clickable {
                                        viewModel.selectExamAnswer(currentIndex, optIndex)
                                    }
                                    .testTag("option_${optIndex}"),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Blue100 else Slate100,
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Blue800) else null
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { viewModel.selectExamAnswer(currentIndex, optIndex) },
                                        colors = RadioButtonDefaults.colors(selectedColor = Blue800)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = optText,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = Slate800
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Controls: Prev, Next, Submit
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = {
                        if (currentIndex > 0) viewModel.currentQuestionIndex.value = currentIndex - 1
                    },
                    enabled = currentIndex > 0
                ) {
                    Text(if (isAs) "পূৰ্বৱৰ্তী" else "Previous")
                }

                if (currentIndex < questions.size - 1) {
                    Button(
                        onClick = { viewModel.currentQuestionIndex.value = currentIndex + 1 },
                        colors = ButtonDefaults.buttonColors(containerColor = Blue800)
                    ) {
                        Text(if (isAs) "পৰৱৰ্তী" else "Next")
                    }
                } else {
                    Button(
                        onClick = { showSubmitConfirmation = true },
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        modifier = Modifier.testTag("submit_exam_final_btn")
                    ) {
                        Text(if (isAs) "দাখিল কৰক" else "Submit Exam")
                    }
                }
            }
        }
    }

    if (showSubmitConfirmation) {
        AlertDialog(
            onDismissRequest = { showSubmitConfirmation = false },
            title = { Text(if (isAs) "পৰীক্ষা দাখিল কৰিব নে?" else "Submit Examination?") },
            text = {
                Text(
                    if (isAs)
                        "আপুনি ${answers.size}/${questions.size} টা প্ৰশ্নৰ উত্তৰ দিছে। দাখিল কৰাৰ পিছত উত্তৰ সলনি কৰিব নোৱাৰিব।"
                    else
                        "You have answered ${answers.size} of ${questions.size} questions. Are you sure you want to finalize and submit?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitConfirmation = false
                        viewModel.submitExam()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                ) {
                    Text(if (isAs) "হয়, দাখিল কৰক" else "Yes, Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitConfirmation = false }) {
                    Text(if (isAs) "বাতিল" else "Review Answers")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamResultScreen(
    viewModel: AppViewModel,
    onCloseClick: () -> Unit
) {
    val result by viewModel.examFinishedResult.collectAsState()
    val exam by viewModel.activeExam.collectAsState()
    val questions by viewModel.activeExamQuestions.collectAsState()
    val answers by viewModel.studentAnswers.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val isAs = currentLanguage == AppLanguage.ASSAMESE

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isAs) "পৰীক্ষাৰ ফলাফল" else "Exam Results") },
                navigationIcon = {
                    IconButton(onClick = onCloseClick) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Score Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Yellow100,
                            modifier = Modifier.size(80.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = "Trophy",
                                    tint = Yellow600,
                                    modifier = Modifier.size(44.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isAs) "অভিনন্দন!" else "Test Completed!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Slate800
                        )
                        Text(
                            text = exam?.title ?: "Examination",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${result?.score ?: 0}/${result?.totalMarks ?: 0}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Blue800
                                )
                                Text(
                                    text = if (isAs) "প্ৰাপ্ত নম্বৰ" else "Score",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate500
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${String.format("%.1f", result?.percentage ?: 0.0)}%",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen
                                )
                                Text(
                                    text = if (isAs) "শতাংশ" else "Percentage",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate500
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = result?.grade ?: "A",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Yellow600
                                )
                                Text(
                                    text = if (isAs) "গ্ৰেড" else "Grade",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate500
                                )
                            }
                        }
                    }
                }
            }

            // Question Review Heading
            item {
                Text(
                    text = if (isAs) "প্ৰশ্নোত্তৰ আৰু ব্যাখ্যা পৰ্যালোচনা" else "Detailed Answer Review & Solutions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate800
                )
            }

            // List of questions with correct/incorrect review
            itemsIndexed(questions) { index, q ->
                val studentChoice = answers[index]
                val isCorrect = studentChoice != null && studentChoice == q.correctOption

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Q${index + 1}",
                                fontWeight = FontWeight.Bold,
                                color = Slate800
                            )
                            Surface(
                                color = if (isCorrect) SuccessGreenLight else ErrorRedLight,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (isCorrect) "+${q.marks} Correct" else "0 Marks",
                                    color = if (isCorrect) SuccessGreen else ErrorRed,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = q.questionText,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate800
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        val opts = listOf(q.optionA, q.optionB, q.optionC, q.optionD)
                        opts.forEachIndexed { optIdx, optText ->
                            val isStudentPick = studentChoice == optIdx
                            val isCorrectOpt = q.correctOption == optIdx

                            val bg = when {
                                isCorrectOpt -> SuccessGreenLight
                                isStudentPick && !isCorrect -> ErrorRedLight
                                else -> Slate100
                            }

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = bg
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = when {
                                            isCorrectOpt -> Icons.Default.Check
                                            isStudentPick -> Icons.Default.Close
                                            else -> Icons.Default.Circle
                                        },
                                        contentDescription = null,
                                        tint = when {
                                            isCorrectOpt -> SuccessGreen
                                            isStudentPick -> ErrorRed
                                            else -> Slate400
                                        },
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = optText,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Slate800,
                                        fontWeight = if (isCorrectOpt || isStudentPick) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }

                        if (q.explanation.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Solution: ${q.explanation}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate600
                            )
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = onCloseClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue800)
                ) {
                    Text(if (isAs) "গৃহলৈ ঘূৰি যাওক" else "Back to Dashboard")
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
