package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.LiveClassEntity
import com.example.model.AppLanguage
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel

@Composable
fun LiveClassesScreen(
    viewModel: AppViewModel
) {
    val liveClasses by viewModel.liveClasses.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val isAs = currentLanguage == AppLanguage.ASSAMESE
    val context = LocalContext.current

    var showJoinedDialog by remember { mutableStateOf<LiveClassEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp)
            .testTag("live_classes_screen")
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (isAs) "লাইভ ক্লাছ আৰু অনলাইন সত্ৰ" else "Live Classes & Interactive Sessions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Slate800
        )
        Text(
            text = if (isAs) "শিক্ষকৰ সৈতে পোনপটীয়া সংযোগ আৰু প্ৰশ্ন-উত্তৰ" else "Direct real-time interactive lectures with PM Shri Berbhngi faculty",
            style = MaterialTheme.typography.bodySmall,
            color = Slate500
        )
        Spacer(modifier = Modifier.height(14.dp))

        if (liveClasses.isEmpty()) {
            EmptyStateBox(if (isAs) "কোনো লাইভ ক্লাছ নাই" else "No live sessions scheduled currently.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                items(liveClasses) { liveClass ->
                    LiveClassDetailCard(
                        liveClass = liveClass,
                        isAs = isAs,
                        onJoin = {
                            showJoinedDialog = liveClass
                        }
                    )
                }
            }
        }
    }

    if (showJoinedDialog != null) {
        val lc = showJoinedDialog!!
        AlertDialog(
            onDismissRequest = { showJoinedDialog = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.LiveTv,
                    contentDescription = "Live",
                    tint = Blue800,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = if (isAs) "লাইভ ক্লাছত অংশগ্ৰহণ" else "Joining Live Class",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(text = lc.title, fontWeight = FontWeight.SemiBold, color = Slate800)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Instructor: ${lc.teacher}", style = MaterialTheme.typography.bodySmall)
                    Text(text = "Schedule: ${lc.scheduledDate} (${lc.scheduledTime})", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isAs)
                            "আপোনাক বিদ্যালয়ৰ সুৰক্ষিত লাইভ ক্লাছৰূমত সংযোগ কৰা হ'ব।"
                        else
                            "Connecting to PM Shri Berbhngi HS School virtual classroom meeting room via Google Meet / Secure WebRTC.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(lc.joinUrl))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Fallback
                        }
                        showJoinedDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Blue800)
                ) {
                    Text(if (isAs) "প্ৰৱেশ কৰক" else "Connect Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { showJoinedDialog = null }) {
                    Text(if (isAs) "বন্ধ কৰক" else "Close")
                }
            }
        )
    }
}

@Composable
fun LiveClassDetailCard(
    liveClass: LiveClassEntity,
    isAs: Boolean,
    onJoin: () -> Unit
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
                    color = if (liveClass.status == "COMPLETED") Slate200 else PendingOrangeLight,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (liveClass.status == "COMPLETED") (if (isAs) "সমাপ্ত" else "COMPLETED") else (if (isAs) "অনাগত" else "UPCOMING"),
                        color = if (liveClass.status == "COMPLETED") Slate700 else PendingOrange,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Time",
                        tint = Slate400,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = liveClass.scheduledTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate500
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = liveClass.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Slate800
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Faculty: ${liveClass.teacher} • Date: ${liveClass.scheduledDate}",
                style = MaterialTheme.typography.bodySmall,
                color = Slate500
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onJoin,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (liveClass.status == "COMPLETED") Slate600 else Blue800
                )
            ) {
                Icon(
                    imageVector = Icons.Default.VideoCall,
                    contentDescription = "Join",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (liveClass.status == "COMPLETED") (if (isAs) "ৰেকৰ্ডিং চাওক" else "Watch Recording") else (if (isAs) "লাইভ ক্লাছত অংশ লওক" else "Join Live Class"),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
