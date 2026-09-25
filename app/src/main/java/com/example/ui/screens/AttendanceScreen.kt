package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AttendanceEntity
import com.example.model.AppLanguage
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel

@Composable
fun AttendanceScreen(
    viewModel: AppViewModel
) {
    val attendanceList by viewModel.myAttendance.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val isAs = currentLanguage == AppLanguage.ASSAMESE

    val total = attendanceList.size
    val present = attendanceList.count { it.isPresent }
    val absent = total - present
    val pct = if (total > 0) (present * 100) / total else 85

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp)
            .testTag("attendance_screen")
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (isAs) "উপস্থিতি পৰিদৰ্শন" else "Attendance Tracker",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Slate800
        )
        Text(
            text = if (isAs) "দৈনিক শ্ৰেণীকোঠাৰ উপস্থিতিৰ সবিশেষ" else "Daily class presence and lecture attendance log",
            style = MaterialTheme.typography.bodySmall,
            color = Slate500
        )
        Spacer(modifier = Modifier.height(14.dp))

        // Large Attendance Overview Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "$pct%",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (pct >= 75) SuccessGreen else ErrorRed
                        )
                        Text(
                            text = if (pct >= 75)
                                (if (isAs) "উপস্থিতি সন্তোষজনক (৭৫%+)" else "Eligible for Examinations (75%+)")
                            else
                                (if (isAs) "উপস্থিতি কম" else "Below Minimum Criteria"),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (pct >= 75) SuccessGreen else ErrorRed,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = if (pct >= 75) SuccessGreenLight else ErrorRedLight
                    ) {
                        Box(modifier = Modifier.size(54.dp), contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (pct >= 75) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = "Status",
                                tint = if (pct >= 75) SuccessGreen else ErrorRed,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                LinearProgressIndicator(
                    progress = { pct / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (pct >= 75) SuccessGreen else ErrorRed,
                    trackColor = Slate200
                )

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Slate100)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$total",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate800
                        )
                        Text(
                            text = if (isAs) "মুঠ ক্লাছ" else "Total Days",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$present",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                        Text(
                            text = if (isAs) "উপস্থিত" else "Present",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$absent",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ErrorRed
                        )
                        Text(
                            text = if (isAs) "অনুপস্থিত" else "Absent",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = if (isAs) "উপস্থিতিৰ ইতিহাস" else "Detailed Daily Attendance History",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Slate800
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (attendanceList.isEmpty()) {
            EmptyStateBox(if (isAs) "কোনো তথ্য পোৱা নগ'ল" else "No attendance logs recorded yet.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                items(attendanceList) { att ->
                    AttendanceRowCard(att = att, isAs = isAs)
                }
            }
        }
    }
}

@Composable
fun AttendanceRowCard(
    att: AttendanceEntity,
    isAs: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (att.isPresent) SuccessGreenLight else ErrorRedLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (att.isPresent) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = "Status",
                        tint = if (att.isPresent) SuccessGreen else ErrorRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = att.date,
                        fontWeight = FontWeight.Bold,
                        color = Slate800,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = att.remarks,
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate500,
                        fontSize = 11.sp
                    )
                }
            }

            Surface(
                color = if (att.isPresent) SuccessGreenLight else ErrorRedLight,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (att.isPresent) (if (isAs) "উপস্থিত" else "PRESENT") else (if (isAs) "অনুপস্থিত" else "ABSENT"),
                    color = if (att.isPresent) SuccessGreen else ErrorRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}
