package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.VideoEntity
import com.example.model.AppLanguage
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel

@Composable
fun VideoClassesScreen(
    viewModel: AppViewModel,
    onVideoClick: (VideoEntity) -> Unit
) {
    val videos by viewModel.videos.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val isAs = currentLanguage == AppLanguage.ASSAMESE

    var selectedSubjectId by remember { mutableStateOf<String?>(null) }

    val filteredVideos = if (selectedSubjectId == null) videos else videos.filter { it.subjectId == selectedSubjectId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .testTag("video_classes_screen")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isAs) "ভিডিঅ' ক্লাছসমূহ" else "Video Class Library",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Slate800
            )
            Text(
                text = if (isAs) "অধ্যায়ভিত্তিক নিৰ্বাচিত অনলাইন ভিডিঅ' পাঠ" else "High-quality chapter-wise video lectures for Class 11 & 12",
                style = MaterialTheme.typography.bodySmall,
                color = Slate500
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filter by subject chips
            ScrollableTabRow(
                selectedTabIndex = if (selectedSubjectId == null) 0 else subjects.indexOfFirst { it.id == selectedSubjectId } + 1,
                edgePadding = 0.dp,
                containerColor = Color.Transparent,
                divider = {}
            ) {
                Tab(
                    selected = selectedSubjectId == null,
                    onClick = { selectedSubjectId = null },
                    text = { Text(if (isAs) "সকলো" else "All Subjects") }
                )
                subjects.forEach { sub ->
                    Tab(
                        selected = selectedSubjectId == sub.id,
                        onClick = { selectedSubjectId = sub.id },
                        text = { Text(if (isAs) sub.nameAs else sub.nameEn) }
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            items(filteredVideos) { video ->
                VideoItemCard(
                    video = video,
                    isAs = isAs,
                    onClick = { onVideoClick(video) }
                )
            }
        }
    }
}

@Composable
fun VideoItemCard(
    video: VideoEntity,
    isAs: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("video_card_${video.id}"),
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
                    .size(width = 100.dp, height = 70.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Blue900),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = "Play",
                    tint = Yellow400,
                    modifier = Modifier.size(36.dp)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = video.duration,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Slate800,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${video.chapter} • ${video.teacher}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate500,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (video.isCompleted) SuccessGreenLight else Blue100,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (video.isCompleted) (if (isAs) "সম্পূৰ্ণ" else "Completed") else (if (isAs) "চাওক" else "Watch"),
                            color = if (video.isCompleted) SuccessGreen else Blue800,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    video: VideoEntity,
    viewModel: AppViewModel,
    onBackClick: () -> Unit
) {
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val isAs = currentLanguage == AppLanguage.ASSAMESE

    var isPlaying by remember { mutableStateOf(true) }
    var currentProgress by remember { mutableFloatStateOf(0.35f) }
    var playbackSpeed by remember { mutableStateOf("1.0x") }
    var isCompleted by remember { mutableStateOf(video.isCompleted) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = video.title,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
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
            // Interactive Video Screen Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = { isPlaying = !isPlaying },
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color.White.copy(alpha = 0.25f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = Color.White,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isPlaying) "Playing: 08:30 / ${video.duration}" else "Paused: 08:30 / ${video.duration}",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }

                // Video Scrubber at bottom of player
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Slider(
                        value = currentProgress,
                        onValueChange = { currentProgress = it },
                        colors = SliderDefaults.colors(
                            thumbColor = Yellow400,
                            activeTrackColor = Yellow400,
                            inactiveTrackColor = Color.White.copy(alpha = 0.4f)
                        )
                    )
                }
            }

            // Controls Bar (Speed, Completed toggle, Quality)
            Surface(
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Playback Speed
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Speed: ",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500
                        )
                        listOf("1.0x", "1.25x", "1.5x").forEach { speed ->
                            FilterChip(
                                selected = playbackSpeed == speed,
                                onClick = { playbackSpeed = speed },
                                label = { Text(speed, fontSize = 11.sp) },
                                modifier = Modifier.padding(horizontal = 2.dp)
                            )
                        }
                    }

                    // Mark as Completed Button
                    FilledTonalButton(
                        onClick = { isCompleted = !isCompleted },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (isCompleted) SuccessGreenLight else Blue100
                        )
                    ) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Status",
                            tint = if (isCompleted) SuccessGreen else Blue800,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isCompleted) (if (isAs) "সম্পূৰ্ণ হ'ল" else "Completed") else (if (isAs) "সম্পূৰ্ণ কৰক" else "Mark Done"),
                            color = if (isCompleted) SuccessGreen else Blue800,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Lesson Details
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate800
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${video.chapter} • Teacher: ${video.teacher}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate600
                )
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (isAs) "পাঠ্যটোৰ গুৰুত্বপূৰ্ণ পইণ্টসমূহ:" else "Key Lesson Notes:",
                            fontWeight = FontWeight.Bold,
                            color = Slate800,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isAs)
                                "• পৰীক্ষাৰ বাবে অতি গুৰুত্বপূৰ্ণ ৫ নম্বৰৰ ব্যাকৰণ আৰু চমু প্ৰশ্নোত্তৰ আলোচনা কৰা হৈছে।\n• পাঠটো শেষ কৰি অনলাইন টেষ্টত অংশ লওক।"
                            else
                                "• Key points and formulas discussed in this video will be asked in the upcoming evaluation.\n• After watching, download the PDF summary from the Study Materials tab.",
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
