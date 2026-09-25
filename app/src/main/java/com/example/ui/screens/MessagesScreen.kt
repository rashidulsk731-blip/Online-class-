package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.model.AppLanguage
import com.example.model.UserRole
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel

data class DirectChatItem(
    val id: String,
    val senderName: String,
    val role: String,
    val message: String,
    val timestamp: String,
    val isFromMe: Boolean
)

@Composable
fun MessagesScreen(
    viewModel: AppViewModel
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val isAs = currentLanguage == AppLanguage.ASSAMESE

    val chatMessages = remember {
        mutableStateListOf(
            DirectChatItem(
                id = "msg_1",
                senderName = "Pranjal Sharma (Teacher)",
                role = "Assamese Faculty",
                message = "Manash, please review the essay question on Sankardeva before Monday's class test.",
                timestamp = "10:30 AM",
                isFromMe = false
            ),
            DirectChatItem(
                id = "msg_2",
                senderName = "Me",
                role = "Student",
                message = "Sure sir, I have read the reference points from the PDF notes. Thank you!",
                timestamp = "10:45 AM",
                isFromMe = true
            ),
            DirectChatItem(
                id = "msg_3",
                senderName = "Office Administration",
                role = "PM Shri Berbhngi HS School",
                message = "Reminder: Admit card verification for Class 12 council exams starts tomorrow.",
                timestamp = "02:15 PM",
                isFromMe = false
            )
        )
    }

    var messageInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .testTag("messages_screen")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isAs) "শিক্ষক-শিক্ষাৰ্থী বাৰ্তালাপ" else "Teacher & Staff Messaging",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Slate800
            )
            Text(
                text = if (isAs) "বিদ্যালয়ৰ শিক্ষকৰ সৈতে ইন-এপ বাৰ্তা প্ৰেৰণ" else "Direct communication channel with teachers and administration",
                style = MaterialTheme.typography.bodySmall,
                color = Slate500
            )
        }

        // Chat list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(chatMessages) { chat ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (chat.isFromMe) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (chat.isFromMe) 16.dp else 2.dp,
                            bottomEnd = if (chat.isFromMe) 2.dp else 16.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (chat.isFromMe) Blue800 else Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            if (!chat.isFromMe) {
                                Text(
                                    text = chat.senderName,
                                    fontWeight = FontWeight.Bold,
                                    color = Blue700,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                            }
                            Text(
                                text = chat.message,
                                color = if (chat.isFromMe) Color.White else Slate800,
                                fontSize = 14.sp,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = chat.timestamp,
                                color = if (chat.isFromMe) Yellow300 else Slate400,
                                fontSize = 10.sp,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }

        // Message input bar
        Surface(
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier.padding(bottom = 80.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageInput,
                    onValueChange = { messageInput = it },
                    placeholder = { Text(if (isAs) "বাৰ্তা লিখক..." else "Type message to teacher...") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("message_input_box"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Blue800,
                        unfocusedBorderColor = Slate300
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (messageInput.isNotBlank()) {
                            chatMessages.add(
                                DirectChatItem(
                                    id = "msg_${System.currentTimeMillis()}",
                                    senderName = "Me",
                                    role = currentUser?.role?.name ?: "Student",
                                    message = messageInput.trim(),
                                    timestamp = "Just now",
                                    isFromMe = true
                                )
                            )
                            messageInput = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Blue800, CircleShape)
                        .testTag("send_message_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
