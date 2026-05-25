package com.mars.overtime.ui

import androidx.compose.runtime.*
import androidx.navigation.compose.*
import com.mars.overtime.ui.screen.*

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddEditRecord : Screen("add_edit_record")
    object Settings : Screen("settings")
    object AppearanceSettings : Screen("appearance_settings")
    object PushSettings : Screen("push_settings")
    object SalarySettings : Screen("salary_settings")
    object CalendarSettings : Screen("calendar_settings")
    object BackupSettings : Screen("backup_settings")
    object HolidaySettings : Screen("holiday_settings")
    object About : Screen("about")
}

@Composable
fun MainNav() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomePage(
                onNavigateToAddEdit = { navController.navigate(Screen.AddEditRecord.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(Screen.AddEditRecord.route) {
            AddEditRecordPage(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Settings.route) {
            SettingsPage(
                onNavigateToAppearanceSettings = { navController.navigate(Screen.AppearanceSettings.route) },
                onNavigateToPushSettings = { navController.navigate(Screen.PushSettings.route) },
                onNavigateToSalarySettings = { navController.navigate(Screen.SalarySettings.route) },
                onNavigateToCalendarSettings = { navController.navigate(Screen.CalendarSettings.route) },
                onNavigateToBackupSettings = { navController.navigate(Screen.BackupSettings.route) },
                onNavigateToHolidaySettings = { navController.navigate(Screen.HolidaySettings.route) },
                onNavigateToAbout = { navController.navigate(Screen.About.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AppearanceSettings.route) {
            AppearanceSettingsPage(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.PushSettings.route) {
            PushSettingsPage(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.SalarySettings.route) {
            SalarySettingsPage(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.CalendarSettings.route) {
            CalendarSettingsPage(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.BackupSettings.route) {
            BackupSettingsPage(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.HolidaySettings.route) {
            HolidaySettingsPage(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.About.route) {
            AboutPage(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
