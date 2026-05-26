package com.mars.overtime.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mars.overtime.ui.screen.*
import com.mars.overtime.ui.theme.BottomBarStyle
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
    val bottomBarStyle by ThemeManager.bottomBarStyle.collectAsState()

    LaunchedEffect(Unit) {
        try {
            val db = com.mars.overtime.OvertimeApplication.database
            val configDao = db.configDao()
            val bottomBarStyleConfig = configDao.getConfig("bottom_bar_style")
            val quickReportModeConfig = configDao.getConfig("quick_report_mode")
            bottomBarStyleConfig?.value?.let { value ->
                ThemeManager.updateBottomBarStyle(BottomBarStyle.valueOf(value))
            }
            quickReportModeConfig?.value?.let { value ->
                ThemeManager.updateQuickReportMode(value.toBoolean())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = fadeIn(animationSpec = tween(150)),
                exit = fadeOut(animationSpec = tween(150))
            ) {
                BottomNavigationBar(
                    onNavigateToStatistics = {
                        if (currentBottomNavScreen != BottomNavScreen.Statistics) {
                            navController.navigate(BottomNavScreen.Statistics.route) {
                                popUpTo(BottomNavScreen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                            currentBottomNavScreen = BottomNavScreen.Statistics
                        }
                    },
                    onNavigateToHome = {
                        if (quickReportMode) {
                            onNavigateToAddEdit()
                        } else {
                            if (currentBottomNavScreen != BottomNavScreen.Home) {
                                navController.navigate(BottomNavScreen.Home.route) {
                                    popUpTo(BottomNavScreen.Home.route) { inclusive = true }
                                    launchSingleTop = true
                                }
                                currentBottomNavScreen = BottomNavScreen.Home
                            }
                        }
                    },
                    onNavigateToSettings = {
                        if (currentBottomNavScreen != BottomNavScreen.Settings) {
                            navController.navigate(BottomNavScreen.Settings.route) {
                                popUpTo(BottomNavScreen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                            currentBottomNavScreen = BottomNavScreen.Settings
                        }
                    },
                    currentScreen = currentBottomNavScreen,
                    quickReportMode = quickReportMode,
                    bottomBarStyle = bottomBarStyle
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavScreen.Home.route,
            modifier = Modifier.padding(paddingValues),
            enterTransition = { fadeIn(animationSpec = tween(200)) },
            exitTransition = { fadeOut(animationSpec = tween(200)) },
            popEnterTransition = { fadeIn(animationSpec = tween(200)) },
            popExitTransition = { fadeOut(animationSpec = tween(200)) }
        ) {
            composable(BottomNavScreen.Statistics.route) {
                currentBottomNavScreen = BottomNavScreen.Statistics
                showBottomBar = true
                StatisticsPage()
            }

            composable(BottomNavScreen.Home.route) {
                currentBottomNavScreen = BottomNavScreen.Home
                showBottomBar = true
                HomePage(
                    onNavigateToAddEdit = onNavigateToAddEdit,
                    onNavigateToSettings = {
                        navController.navigate(BottomNavScreen.Settings.route) {
                            popUpTo(BottomNavScreen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                        currentBottomNavScreen = BottomNavScreen.Settings
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
                    onNavigateToAbout = onNavigateToAbout
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
    quickReportMode: Boolean = false,
    bottomBarStyle: BottomBarStyle = BottomBarStyle.ICON_AND_TEXT
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentScreen == BottomNavScreen.Statistics,
            onClick = onNavigateToStatistics,
            icon = {
                if (bottomBarStyle != BottomBarStyle.TEXT_ONLY) {
                    Icon(
                        imageVector = Icons.Default.Assessment,
                        contentDescription = if (bottomBarStyle == BottomBarStyle.ICON_ONLY) "统计" else null
                    )
                }
            },
            label = if (bottomBarStyle != BottomBarStyle.ICON_ONLY) {
                { Text("统计") }
            } else null
        )

        NavigationBarItem(
            selected = false,
            onClick = onNavigateToHome,
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .then(
                            if (quickReportMode) {
                                Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            } else {
                                Modifier
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (quickReportMode) Icons.Default.Add else Icons.Default.Home,
                        contentDescription = if (bottomBarStyle == BottomBarStyle.ICON_ONLY) {
                            if (quickReportMode) "快速提报" else "首页"
                        } else null,
                        tint = if (quickReportMode) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(if (quickReportMode) 28.dp else 24.dp)
                    )
                }
            },
            label = if (bottomBarStyle != BottomBarStyle.ICON_ONLY) {
                { Text(if (quickReportMode) "提报" else "首页") }
            } else null,
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = if (quickReportMode) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    NavigationBarItemDefaults.colors().indicatorColor
                }
            )
        )

        NavigationBarItem(
            selected = currentScreen == BottomNavScreen.Settings,
            onClick = onNavigateToSettings,
            icon = {
                if (bottomBarStyle != BottomBarStyle.TEXT_ONLY) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = if (bottomBarStyle == BottomBarStyle.ICON_ONLY) "设置" else null
                    )
                }
            },
            label = if (bottomBarStyle != BottomBarStyle.ICON_ONLY) {
                { Text("设置") }
            } else null
        )
    }
}
