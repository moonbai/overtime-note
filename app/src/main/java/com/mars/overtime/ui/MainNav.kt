package com.mars.overtime.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.calculateTopPadding
import androidx.compose.foundation.layout.calculateBottomPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mars.overtime.ui.screen.*
import com.mars.overtime.ui.theme.ThemeManager

sealed class BottomNavScreen(val route: String) {
    object Statistics : BottomNavScreen("statistics")
    object Home : BottomNavScreen("home")
    object Settings : BottomNavScreen("settings")
}

@Composable
fun MainNav() {
    val navController = rememberNavController()
    var currentBottomNavScreen by remember { mutableStateOf<BottomNavScreen>(BottomNavScreen.Home) }
    var showBottomBar by remember { mutableStateOf(true) }
    val quickReportMode by ThemeManager.quickReportMode.collectAsState()

    val onNavigateToSettings: () -> Unit = {
        navController.navigate(BottomNavScreen.Settings.route)
    }

    val onNavigateToStatistics: () -> Unit = {
        navController.navigate(BottomNavScreen.Statistics.route)
    }

    val onNavigateToHome: () -> Unit = {
        navController.navigate(BottomNavScreen.Home.route)
    }

    val onNavigateToAddEdit: () -> Unit = {
        navController.navigate("add_edit_record")
        showBottomBar = false
    }

    val onNavigateToAppearanceSettings: () -> Unit = {
        navController.navigate("appearance_settings")
        showBottomBar = false
    }

    val onNavigateToPushSettings: () -> Unit = {
        navController.navigate("push_settings")
        showBottomBar = false
    }

    val onNavigateToSalarySettings: () -> Unit = {
        navController.navigate("salary_settings")
        showBottomBar = false
    }

    val onNavigateToCalendarSettings: () -> Unit = {
        navController.navigate("calendar_settings")
        showBottomBar = false
    }

    val onNavigateToBackupSettings: () -> Unit = {
        navController.navigate("backup_settings")
        showBottomBar = false
    }

    val onNavigateToHolidaySettings: () -> Unit = {
        navController.navigate("holiday_settings")
        showBottomBar = false
    }

    val onNavigateToAbout: () -> Unit = {
        navController.navigate("about")
        showBottomBar = false
    }

    val onNavigateBack: () -> Unit = {
        navController.popBackStack()
        showBottomBar = true
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                BottomNavigationBar(
                    onNavigateToStatistics = {
                        navController.navigate(BottomNavScreen.Statistics.route)
                        currentBottomNavScreen = BottomNavScreen.Statistics
                    },
                    onNavigateToHome = {
                        if (quickReportMode) {
                            onNavigateToAddEdit()
                        } else {
                            navController.navigate(BottomNavScreen.Home.route)
                            currentBottomNavScreen = BottomNavScreen.Home
                        }
                    },
                    onNavigateToSettings = {
                        navController.navigate(BottomNavScreen.Settings.route)
                        currentBottomNavScreen = BottomNavScreen.Settings
                    },
                    currentScreen = currentBottomNavScreen,
                    quickReportMode = quickReportMode
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavScreen.Home.route,
            modifier = Modifier
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = if (showBottomBar) paddingValues.calculateBottomPadding() else 0.dp
                )
        ) {
            composable(BottomNavScreen.Statistics.route) {
                currentBottomNavScreen = BottomNavScreen.Statistics
                showBottomBar = true
                StatisticsPage(
                    onNavigateBack = {}
                )
            }

            composable(BottomNavScreen.Home.route) {
                currentBottomNavScreen = BottomNavScreen.Home
                showBottomBar = true
                HomePage(
                    onNavigateToAddEdit = onNavigateToAddEdit,
                    onNavigateToSettings = {
                        navController.navigate(BottomNavScreen.Settings.route)
                        currentBottomNavScreen = BottomNavScreen.Settings
                    },
                    onNavigateToStatistics = {
                        navController.navigate(BottomNavScreen.Statistics.route)
                        currentBottomNavScreen = BottomNavScreen.Statistics
                    }
                )
            }

            composable(BottomNavScreen.Settings.route) {
                currentBottomNavScreen = BottomNavScreen.Settings
                showBottomBar = true
                SettingsPage(
                    onNavigateToAppearanceSettings = onNavigateToAppearanceSettings,
                    onNavigateToPushSettings = onNavigateToPushSettings,
                    onNavigateToSalarySettings = onNavigateToSalarySettings,
                    onNavigateToCalendarSettings = onNavigateToCalendarSettings,
                    onNavigateToBackupSettings = onNavigateToBackupSettings,
                    onNavigateToHolidaySettings = onNavigateToHolidaySettings,
                    onNavigateToAbout = onNavigateToAbout,
                    onNavigateBack = {}
                )
            }

            composable("add_edit_record") {
                showBottomBar = false
                AddEditRecordPage(
                    onNavigateBack = onNavigateBack
                )
            }

            composable("appearance_settings") {
                showBottomBar = false
                AppearanceSettingsPage(
                    onNavigateBack = onNavigateBack
                )
            }

            composable("push_settings") {
                showBottomBar = false
                PushSettingsPage(
                    onNavigateBack = onNavigateBack
                )
            }

            composable("salary_settings") {
                showBottomBar = false
                SalarySettingsPage(
                    onNavigateBack = onNavigateBack
                )
            }

            composable("calendar_settings") {
                showBottomBar = false
                CalendarSettingsPage(
                    onNavigateBack = onNavigateBack
                )
            }

            composable("backup_settings") {
                showBottomBar = false
                BackupSettingsPage(
                    onNavigateBack = onNavigateBack
                )
            }

            composable("holiday_settings") {
                showBottomBar = false
                HolidaySettingsPage(
                    onNavigateBack = onNavigateBack
                )
            }

            composable("about") {
                showBottomBar = false
                AboutPage(
                    onNavigateBack = onNavigateBack
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    onNavigateToStatistics: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToSettings: () -> Unit,
    currentScreen: BottomNavScreen,
    quickReportMode: Boolean = false
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentScreen == BottomNavScreen.Statistics,
            onClick = onNavigateToStatistics,
            icon = {
                Icon(
                    imageVector = Icons.Default.Assessment,
                    contentDescription = "统计"
                )
            },
            label = { Text("统计") }
        )

        NavigationBarItem(
            selected = false,
            onClick = onNavigateToHome,
            icon = {
                if (quickReportMode) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "快速提报"
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "首页"
                    )
                }
            },
            label = { Text(if (quickReportMode) "提报" else "首页") },
            colors = if (quickReportMode) {
                NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            } else {
                NavigationBarItemDefaults.colors()
            }
        )

        NavigationBarItem(
            selected = currentScreen == BottomNavScreen.Settings,
            onClick = onNavigateToSettings,
            icon = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "设置"
                )
            },
            label = { Text("设置") }
        )
    }
}
