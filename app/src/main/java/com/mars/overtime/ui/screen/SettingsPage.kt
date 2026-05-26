package com.mars.overtime.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SettingsSection(title = "通用设置")
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Palette,
                    title = "外观设置",
                    subtitle = "主题、颜色、导航栏样式",
                    onClick = onNavigateToAppearanceSettings
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.AttachMoney,
                    title = "薪资设置",
                    subtitle = "基础薪资、加班倍率",
                    onClick = onNavigateToSalarySettings
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Notifications,
                    title = "推送设置",
                    subtitle = "配置消息推送渠道",
                    onClick = onNavigateToPushSettings
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.CalendarMonth,
                    title = "日历设置",
                    subtitle = "同步到系统日历",
                    onClick = onNavigateToCalendarSettings
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Event,
                    title = "节假日设置",
                    subtitle = "自定义节假日数据源",
                    onClick = onNavigateToHolidaySettings
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSection(title = "数据管理")
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Backup,
                    title = "备份与恢复",
                    subtitle = "本地备份、云同步",
                    onClick = onNavigateToBackupSettings
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSection(title = "关于")
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "关于应用",
                    subtitle = "版本信息、开发者",
                    onClick = onNavigateToAbout
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun SettingsSection(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
    )
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
