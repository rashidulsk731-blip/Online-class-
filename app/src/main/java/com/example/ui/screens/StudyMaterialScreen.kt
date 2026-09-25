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
import com.example.data.local.StudyMaterialEntity
import com.example.model.AppLanguage
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel

@Composable
fun StudyMaterialScreen(
    viewModel: AppViewModel,
    onMaterialClick: (StudyMaterialEntity) -> Unit
) {
    val materials by viewModel.studyMaterials.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val isAs = currentLanguage == AppLanguage.ASSAMESE

    val categories = listOf(
        "All",
        "Notes",
        "PDF",
        "Study Material",
        "Question Paper",
        "Practice Material"
    )

    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedSubjectId by remember { mutableStateOf<String?>(null) }

    val filtered = materials.filter { mat ->
        val matchesCat = selectedCategory == "All" || mat.category.equals(selectedCategory, ignoreCase = true)
        val matchesSub = selectedSubjectId == null || mat.subjectId == selectedSubjectId
        val matchesQuery = searchQuery.isBlank() ||
                mat.title.contains(searchQuery, ignoreCase = true) ||
                mat.chapter.contains(searchQuery, ignoreCase = true)
        matchesCat && matchesSub && matchesQuery
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .testTag("study_material_screen")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isAs) "ডিজিটেল লাইব্ৰেৰী আৰু অধ্যয়ন সামগ্ৰী" else "Digital Library & Study Notes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Slate800
            )
            Text(
                text = if (isAs) "পিডিএফ নোটচ্, আৰ্হি প্ৰশ্নকাকত আৰু চিল্যাবাছ" else "Curated PDFs, Question Papers & Practice Guides",
                style = MaterialTheme.typography.bodySmall,
                color = Slate500
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(if (isAs) "নোটচ্ বা পাঠ সন্ধান কৰক..." else "Search notes, question papers...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Slate500)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("study_material_search_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Blue800,
                    unfocusedBorderColor = Slate300
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Chips
            ScrollableTabRow(
                selectedTabIndex = categories.indexOf(selectedCategory),
                edgePadding = 0.dp,
                containerColor = Color.Transparent,
                divider = {}
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Tab(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        text = {
                            Text(
                                text = cat,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
        }

        if (filtered.isEmpty()) {
            EmptyStateBox(if (isAs) "কোনো সামগ্ৰী পোৱা নগ'ল" else "No matching documents found in library.")
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                items(filtered) { mat ->
                    MaterialCard(
                        material = mat,
                        isAs = isAs,
                        onClick = { onMaterialClick(mat) }
                    )
                }
            }
        }
    }
}

@Composable
fun MaterialCard(
    material: StudyMaterialEntity,
    isAs: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("material_card_${material.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when (material.category) {
                            "Question Paper" -> Yellow100
                            "Notes" -> Blue100
                            else -> ErrorRedLight
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (material.category) {
                        "Question Paper" -> Icons.Default.Quiz
                        "Notes" -> Icons.Default.Description
                        else -> Icons.Default.PictureAsPdf
                    },
                    contentDescription = material.category,
                    tint = when (material.category) {
                        "Question Paper" -> Yellow600
                        "Notes" -> Blue800
                        else -> ErrorRed
                    }
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = material.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Slate800,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "${material.chapter} • ${material.fileSize} • ${material.pageCount} Pages",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate500,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = Slate100,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = material.category,
                        color = Slate700,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = "Read",
                    tint = Blue800
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(
    material: StudyMaterialEntity,
    viewModel: AppViewModel,
    onBackClick: () -> Unit
) {
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val isAs = currentLanguage == AppLanguage.ASSAMESE

    var currentPage by remember { mutableIntStateOf(1) }
    var isBookmarked by remember { mutableStateOf(false) }
    var showDownloadedSnackbar by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = material.title,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Page $currentPage of ${material.pageCount}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isBookmarked = !isBookmarked }) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) Yellow500 else Slate700
                        )
                    }
                    IconButton(onClick = { showDownloadedSnackbar = true }) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download",
                            tint = Blue800
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
            if (showDownloadedSnackbar) {
                Surface(
                    color = SuccessGreenLight,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAs) "পিডিএফ সফলতাৰে ডাউনলোড হ'ল!" else "PDF saved to offline storage successfully!",
                            color = SuccessGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // PDF Reader Simulation Canvas
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    // Document Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "PM Shri Berbhngi HS School - Digital Library",
                            style = MaterialTheme.typography.bodySmall,
                            color = Blue800,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Page $currentPage/${material.pageCount}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500
                        )
                    }
                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = Slate200)

                    Text(
                        text = material.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate800
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Subject Category: ${material.category} • Date: ${material.date}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate500
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = material.contentPreview.ifBlank {
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate700,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Blue50)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isAs) "মনত ৰাখিবলগীয়া তথ্য:" else "Study Notes & Exam Tips:",
                                fontWeight = FontWeight.Bold,
                                color = Blue900,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isAs)
                                    "১. প্ৰতিটো অধ্যায়ৰ মূল প্ৰশ্নোত্তৰ ভালদৰে চৰ্চা কৰক।\n২. চিত্ৰ আৰু সংজ্ঞাসমূহ স্পষ্টকৈ উল্লেখ কৰিব লাগিব।"
                                else
                                    "1. Focus on chapter-end exercises and model questions.\n2. Revise key definitions, diagrams, and historical timelines.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate700,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // Pagination Controls at bottom
            Surface(
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { if (currentPage > 1) currentPage-- },
                        enabled = currentPage > 1
                    ) {
                        Text(if (isAs) "পূৰ্বৱৰ্তী পৃষ্ঠা" else "Previous")
                    }

                    Text(
                        text = "$currentPage / ${material.pageCount}",
                        fontWeight = FontWeight.Bold,
                        color = Slate800
                    )

                    Button(
                        onClick = { if (currentPage < material.pageCount) currentPage++ },
                        enabled = currentPage < material.pageCount,
                        colors = ButtonDefaults.buttonColors(containerColor = Blue800)
                    ) {
                        Text(if (isAs) "পৰৱৰ্তী পৃষ্ঠা" else "Next")
                    }
                }
            }
        }
    }
}
