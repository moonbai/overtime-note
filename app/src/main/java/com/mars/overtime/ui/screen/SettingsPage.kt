package com.mars.overtime.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(
    onNavigateToAppearanceSettings: () -> Unit,
    onNavigateToPushSettings: () -> Unit,
    onNavigateToSalarySettings: () -> Unit,
    onNavigateToCalendarSettings: () -> Unit,
    onNavigateToBackupSettings: () -> Unit,
    onNavigateToHolidaySettings: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsItem("外观设置", "主题、强调色、字体大小", onNavigateToAppearanceSettings)
            SettingsItem("推送设置", "钉钉、飞书、WxPusher、自定义推送", onNavigateToPushSettings)
            SettingsItem("薪资设置", "基础薪资、加班倍率", onNavigateToSalarySettings)
            SettingsItem("日历同步", "安卓日历同步设置", onNavigateToCalendarSettings)
            SettingsItem("备份恢复", "本地备份、WebDAV云端备份", onNavigateToBackupSettings)
            SettingsItem("节假日管理", "更新节假日规则", onNavigateToHolidaySettings)
            SettingsItem("关于", "版本信息、作者", onNavigateToAbout)
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        trailingContent = { Icon(Icons.Default.ArrowForward, contentDescription = null) },
        modifier = Modifier.clickable(onClick = onClick)
    )
    HorizontalDivider()
}
