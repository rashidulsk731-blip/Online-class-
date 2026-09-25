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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CourseEntity
import com.example.model.AppLanguage
import com.example.model.UserRole
import com.example.ui.theme.*
import com.example.util.UpiPaymentHandler
import com.example.viewmodel.AppViewModel

@Composable
fun CoursesScreen(
    viewModel: AppViewModel,
    onCourseClick: (CourseEntity) -> Unit
) {
    val courses by viewModel.courses.collectAsState()
    val enrollments by viewModel.myEnrollments.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val isAs = currentLanguage == AppLanguage.ASSAMESE

    var selectedCourseForPayment by remember { mutableStateOf<CourseEntity?>(null) }
    var paymentSuccessMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp)
            .testTag("courses_screen")
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isAs) "বিশেষ পাঠ্যক্ৰম (₹১০০)" else "Premium Courses (₹100)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Slate800
                )
                Text(
                    text = if (isAs) "UPI পেমেণ্ট ব্যৱস্থাৰে তৎক্ষণাত নামভৰ্তি কৰক" else "Instant enrollment via UPI payment gateway (₹100 only)",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate500
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (courses.isEmpty()) {
            EmptyStateBox(if (isAs) "কোনো পাঠ্যক্ৰম উপলব্ধ নাই" else "No courses available currently.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                items(courses) { course ->
                    val isEnrolled = enrollments.any { it.courseId == course.id }
                    CourseCard(
                        course = course,
                        isEnrolled = isEnrolled,
                        isAs = isAs,
                        onEnrollClick = { selectedCourseForPayment = course },
                        onOpenCourse = { onCourseClick(course) }
                    )
                }
            }
        }
    }

    // UPI Checkout Dialog
    if (selectedCourseForPayment != null) {
        UpiPaymentDialog(
            course = selectedCourseForPayment!!,
            isAs = isAs,
            onDismiss = { selectedCourseForPayment = null },
            onPaymentSuccess = { message ->
                selectedCourseForPayment = null
                paymentSuccessMessage = message
            },
            viewModel = viewModel
        )
    }

    // Payment Success Alert
    if (paymentSuccessMessage != null) {
        AlertDialog(
            onDismissRequest = { paymentSuccessMessage = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = SuccessGreen,
                    modifier = Modifier.size(44.dp)
                )
            },
            title = {
                Text(
                    text = if (isAs) "UPI পেমেণ্ট সফল হ'ল!" else "UPI Payment Successful!",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = paymentSuccessMessage!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate700
                )
            },
            confirmButton = {
                Button(
                    onClick = { paymentSuccessMessage = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Blue800)
                ) {
                    Text(if (isAs) "পাঠ্যক্ৰম আৰম্ভ কৰক" else "Access Course Now")
                }
            }
        )
    }
}

@Composable
fun CourseCard(
    course: CourseEntity,
    isEnrolled: Boolean,
    isAs: Boolean,
    onEnrollClick: () -> Unit,
    onOpenCourse: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (isEnrolled) onOpenCourse() else onEnrollClick() }
            .testTag("course_card_${course.id}"),
        shape = RoundedCornerShape(18.dp),
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
                    color = if (isEnrolled) SuccessGreenLight else Yellow100,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isEnrolled) (if (isAs) "ভৰ্তি হৈছে" else "ENROLLED") else "₹${course.price.toInt()}",
                        color = if (isEnrolled) SuccessGreen else Yellow600,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    color = Blue100,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = course.classGrade,
                        color = Blue800,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = course.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Slate800
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = course.description,
                style = MaterialTheme.typography.bodySmall,
                color = Slate600,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Instructor: ${course.teacher}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate500,
                    fontSize = 11.sp
                )
                Text(
                    text = "Duration: ${course.durationHours} Hours",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate500,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (isEnrolled) {
                OutlinedButton(
                    onClick = onOpenCourse,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayLesson,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isAs) "ক্লাছসমূহ চাওক" else "Continue Course", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onEnrollClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("enroll_course_btn_${course.id}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue800)
                ) {
                    Icon(
                        imageVector = Icons.Default.CurrencyRupee,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isAs) "নামভৰ্তি কৰক (₹১০০)" else "Enroll with UPI (₹${course.price.toInt()})",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun UpiPaymentDialog(
    course: CourseEntity,
    isAs: Boolean,
    onDismiss: () -> Unit,
    onPaymentSuccess: (String) -> Unit,
    viewModel: AppViewModel
) {
    var studentUpiId by remember { mutableStateOf("student@okaxis") }
    var isProcessing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = { if (!isProcessing) onDismiss() },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = "UPI",
                    tint = Blue800
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isAs) "UPI পেমেণ্ট গেটৱে" else "Secure UPI Payment",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = course.title,
                    fontWeight = FontWeight.Bold,
                    color = Slate800
                )
                Text(
                    text = "Beneficiary: ${UpiPaymentHandler.MERCHANT_NAME}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate500
                )
                Text(
                    text = "Payee VPA: ${UpiPaymentHandler.DEFAULT_UPI_VPA}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Blue700,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(14.dp))

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Yellow50)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAs) "পৰিশোধ কৰিবলগীয়া ধন:" else "Total Amount Payable:",
                            fontWeight = FontWeight.SemiBold,
                            color = Slate800
                        )
                        Text(
                            text = "₹${course.price.toInt()}.00",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Yellow600
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = studentUpiId,
                    onValueChange = {
                        studentUpiId = it
                        errorMessage = null
                    },
                    label = { Text(if (isAs) "আপোনাৰ UPI ID (যেনে user@okaxis)" else "Your UPI ID (VPA)") },
                    placeholder = { Text("username@okhdfcbank") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("upi_id_input_field"),
                    singleLine = true
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = errorMessage!!,
                        color = ErrorRed,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (isAs)
                        "• পেমেণ্ট নিশ্চিত হোৱাৰ পিছত পাঠ্যক্ৰম তৎক্ষণাত আনলক হ'ব।"
                    else
                        "• Instant course activation upon UPI confirmation. 100% money back guarantee.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate500,
                    fontSize = 11.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isProcessing = true
                    viewModel.enrollCourseWithUpi(course, studentUpiId) { success, msg ->
                        isProcessing = false
                        if (success) {
                            onPaymentSuccess(msg)
                        } else {
                            errorMessage = msg
                        }
                    }
                },
                enabled = !isProcessing && studentUpiId.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                modifier = Modifier.testTag("confirm_upi_pay_btn")
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(if (isAs) "₹১০০ পৰিশোধ কৰক" else "Pay ₹${course.price.toInt()} via UPI")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isProcessing
            ) {
                Text(if (isAs) "বাতিল" else "Cancel")
            }
        }
    )
}
