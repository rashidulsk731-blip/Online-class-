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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.NotificationEntity
import com.example.model.AppLanguage
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    viewModel: AppViewModel,
    onBackClick: () -> Unit
) {
    val notifications by viewModel.notifications.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val isAs = currentLanguage == AppLanguage.ASSAMESE

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isAs) "জাননীসমূহ" else "Notifications") },
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
                .padding(horizontal = 16.dp)
                .testTag("notifications_screen")
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            if (notifications.isEmpty()) {
                EmptyStateBox(if (isAs) "কোনো নতুন জাননী নাই" else "No new notifications.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    items(notifications) { notif ->
                        NotificationCard(
                            notif = notif,
                            isAs = isAs,
                            onClick = { viewModel.markNotificationRead(notif.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    notif: NotificationEntity,
    isAs: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("notif_card_${notif.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notif.isRead) Color.White else Blue50
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        when (notif.type) {
                            "LIVE" -> PendingOrangeLight
                            "EXAM" -> Yellow100
                            "PDF" -> ErrorRedLight
                            else -> Blue100
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (notif.type) {
                        "LIVE" -> Icons.Default.LiveTv
                        "EXAM" -> Icons.Default.Quiz
                        "PDF" -> Icons.Default.PictureAsPdf
                        else -> Icons.Default.Notifications
                    },
                    contentDescription = null,
                    tint = when (notif.type) {
                        "LIVE" -> PendingOrange
                        "EXAM" -> Yellow600
                        "PDF" -> ErrorRed
                        else -> Blue800
                    },
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isAs) notif.titleAs else notif.title,
                    fontWeight = FontWeight.Bold,
                    color = Slate800,
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isAs) notif.messageAs else notif.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate600,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: AppViewModel,
    onBackClick: () -> Unit,
    onSelectSubject: (String) -> Unit,
    onSelectVideo: (String) -> Unit,
    onSelectMaterial: (String) -> Unit
) {
    val subjects by viewModel.subjects.collectAsState()
    val videos by viewModel.videos.collectAsState()
    val materials by viewModel.studyMaterials.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val isAs = currentLanguage == AppLanguage.ASSAMESE

    var searchQuery by remember { mutableStateOf("") }

    val matchedSubjects = if (searchQuery.isBlank()) emptyList() else subjects.filter {
        it.nameEn.contains(searchQuery, ignoreCase = true) || it.nameAs.contains(searchQuery, ignoreCase = true)
    }

    val matchedVideos = if (searchQuery.isBlank()) emptyList() else videos.filter {
        it.title.contains(searchQuery, ignoreCase = true) || it.chapter.contains(searchQuery, ignoreCase = true)
    }

    val matchedMaterials = if (searchQuery.isBlank()) emptyList() else materials.filter {
        it.title.contains(searchQuery, ignoreCase = true) || it.chapter.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text(if (isAs) "বিষয়, পাঠ, ভিডিঅ' বা নোটচ্ সন্ধান..." else "Search subjects, videos, notes...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("global_search_input"),
                        shape = RoundedCornerShape(20.dp),
                        singleLine = true
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (searchQuery.isBlank()) {
                item {
                    Text(
                        text = if (isAs) "জনপ্ৰিয় সন্ধান:" else "Popular searches: Assamese, Political Science, Healthcare, Model Questions",
                        color = Slate500,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            } else {
                if (matchedSubjects.isNotEmpty()) {
                    item {
                        Text(
                            text = if (isAs) "বিষয়সমূহ (${matchedSubjects.size})" else "Subjects (${matchedSubjects.size})",
                            fontWeight = FontWeight.Bold,
                            color = Slate800
                        )
                    }
                    items(matchedSubjects) { sub ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectSubject(sub.id) },
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Text(
                                text = "${sub.nameEn} (${sub.nameAs})",
                                modifier = Modifier.padding(12.dp),
                                fontWeight = FontWeight.Bold,
                                color = Blue800
                            )
                        }
                    }
                }

                if (matchedVideos.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isAs) "ভিডিঅ' ক্লাছ (${matchedVideos.size})" else "Video Classes (${matchedVideos.size})",
                            fontWeight = FontWeight.Bold,
                            color = Slate800
                        )
                    }
                    items(matchedVideos) { vid ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectVideo(vid.id) },
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Text(
                                text = "🎬 ${vid.title} (${vid.chapter})",
                                modifier = Modifier.padding(12.dp),
                                color = Slate800,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                if (matchedMaterials.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isAs) "নোটচ্ আৰু PDF (${matchedMaterials.size})" else "Study Materials (${matchedMaterials.size})",
                            fontWeight = FontWeight.Bold,
                            color = Slate800
                        )
                    }
                    items(matchedMaterials) { mat ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectMaterial(mat.id) },
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Text(
                                text = "📄 ${mat.title} [${mat.category}]",
                                modifier = Modifier.padding(12.dp),
                                color = Slate800,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
