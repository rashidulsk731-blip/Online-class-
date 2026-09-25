package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppLanguage
import com.example.model.CurrentUser
import com.example.model.UserRole
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolTopAppBar(
    currentUser: CurrentUser?,
    currentLanguage: AppLanguage,
    onToggleLanguage: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSearchClick: () -> Unit,
    onRoleSwitchClick: () -> Unit
) {
    Surface(
        color = Blue800,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // School & App Brand
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // School/App Logo
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .padding(2.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_app_logo),
                            contentDescription = "School Logo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (currentLanguage == AppLanguage.ASSAMESE) "ষ্টুডেণ্ট এডুকেশ্বন" else "Student Education",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "PM Shri Berbhngi HS School",
                            style = MaterialTheme.typography.bodySmall,
                            color = Yellow400,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Actions: Search, Language Switcher, Notifications, Role
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onSearchClick,
                        modifier = Modifier.size(38.dp).testTag("top_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Language Switcher Badge
                    Surface(
                        onClick = onToggleLanguage,
                        shape = RoundedCornerShape(16.dp),
                        color = Yellow500,
                        modifier = Modifier.testTag("language_switch_button")
                    ) {
                        Text(
                            text = if (currentLanguage == AppLanguage.ENGLISH) "অসমীয়া" else "ENG",
                            color = Slate800,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    IconButton(
                        onClick = onNotificationsClick,
                        modifier = Modifier.size(38.dp).testTag("top_notif_button")
                    ) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = Yellow500,
                                    contentColor = Slate800
                                ) {
                                    Text("3", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    // Role Chip / Switcher
                    Surface(
                        onClick = onRoleSwitchClick,
                        shape = CircleShape,
                        color = when (currentUser?.role) {
                            UserRole.ADMIN -> Yellow400
                            UserRole.TEACHER -> Blue100
                            else -> Color.White.copy(alpha = 0.2f)
                        },
                        modifier = Modifier.testTag("role_switch_button")
                    ) {
                        Box(
                            modifier = Modifier.size(34.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (currentUser?.role) {
                                    UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                                    UserRole.TEACHER -> Icons.Default.SupervisedUserCircle
                                    else -> Icons.Default.Person
                                },
                                contentDescription = "User Role",
                                tint = if (currentUser?.role == UserRole.ADMIN) Slate800 else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SchoolBottomNavigation(
    currentTab: String,
    currentLanguage: AppLanguage,
    onSelectTab: (String) -> Unit
) {
    val items = listOf(
        Triple("home", Icons.Filled.Home, Icons.Outlined.Home),
        Triple("courses", Icons.Filled.School, Icons.Outlined.School),
        Triple("classes", Icons.Filled.PlayLesson, Icons.Outlined.PlayLesson),
        Triple("exam", Icons.Filled.Assignment, Icons.Outlined.Assignment),
        Triple("profile", Icons.Filled.Person, Icons.Outlined.Person)
    )

    NavigationBar(
        containerColor = Color.White,
        contentColor = Blue800,
        tonalElevation = 8.dp,
        modifier = Modifier.navigationBarsPadding()
    ) {
        items.forEach { (tab, filledIcon, outlinedIcon) ->
            val isSelected = currentTab == tab
            val label = when (tab) {
                "home" -> if (currentLanguage == AppLanguage.ASSAMESE) "গৃহ" else "Home"
                "courses" -> if (currentLanguage == AppLanguage.ASSAMESE) "পাঠ্যক্ৰম" else "Courses"
                "classes" -> if (currentLanguage == AppLanguage.ASSAMESE) "ক্লাছসমূহ" else "Classes"
                "exam" -> if (currentLanguage == AppLanguage.ASSAMESE) "পৰীক্ষা" else "Exam"
                else -> if (currentLanguage == AppLanguage.ASSAMESE) "প্ৰফাইল" else "Profile"
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = { onSelectTab(tab) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) filledIcon else outlinedIcon,
                        contentDescription = label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Blue800,
                    selectedTextColor = Blue800,
                    indicatorColor = Blue100,
                    unselectedIconColor = Slate500,
                    unselectedTextColor = Slate500
                ),
                modifier = Modifier.testTag("nav_tab_$tab")
            )
        }
    }
}

@Composable
fun StatusBadge(
    text: String,
    containerColor: Color,
    contentColor: Color
) {
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            color = contentColor,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
