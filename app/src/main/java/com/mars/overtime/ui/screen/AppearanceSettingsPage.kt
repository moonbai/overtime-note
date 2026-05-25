package com.mars.overtime.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mars.overtime.ui.theme.AccentColor
import com.mars.overtime.ui.theme.ThemeManager
import com.mars.overtime.ui.theme.ThemeMode
import com.mars.overtime.ui.theme.saveAccentColor
import com.mars.overtime.ui.theme.saveDynamicColor
import com.mars.overtime.ui.theme.saveThemeMode
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceSettingsPage(
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    val selectedThemeMode by ThemeManager.themeMode.collectAsState()
    val selectedAccentColor by ThemeManager.accentColor.collectAsState()
    val dynamicColor by ThemeManager.dynamicColor.collectAsState()

    LaunchedEffect(Unit) {
        try {
            val db = com.mars.overtime.OvertimeApplication.database
            val configDao = db.configDao()
            val themeModeConfig = configDao.getConfig("theme_mode")
            val accentColorConfig = configDao.getConfig("accent_color")
            val dynamicColorConfig = configDao.getConfig("dynamic_color")

            themeModeConfig?.value?.let { value ->
                ThemeManager.updateThemeMode(ThemeMode.valueOf(value))
            }
            accentColorConfig?.value?.let { value ->
                ThemeManager.updateAccentColor(AccentColor.valueOf(value))
            }
            dynamicColorConfig?.value?.let { value ->
                ThemeManager.updateDynamicColor(value.toBoolean())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("外观设置") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "主题模式",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThemeModeCard(
                    title = "跟随系统",
                    icon = Icons.Default.Palette,
                    selected = selectedThemeMode == ThemeMode.SYSTEM,
                    onClick = {
                        scope.launch { saveThemeMode(ThemeMode.SYSTEM) }
                    },
                    modifier = Modifier.weight(1f)
                )
                ThemeModeCard(
                    title = "浅色",
                    icon = Icons.Default.LightMode,
                    selected = selectedThemeMode == ThemeMode.LIGHT,
                    onClick = {
                        scope.launch { saveThemeMode(ThemeMode.LIGHT) }
                    },
                    modifier = Modifier.weight(1f)
                )
                ThemeModeCard(
                    title = "深色",
                    icon = Icons.Default.DarkMode,
                    selected = selectedThemeMode == ThemeMode.DARK,
                    onClick = {
                        scope.launch { saveThemeMode(ThemeMode.DARK) }
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "动态颜色",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "根据壁纸自动调整主题色（Android 12+）",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = dynamicColor,
                    onCheckedChange = { enabled ->
                        scope.launch { saveDynamicColor(enabled) }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "强调色",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(AccentColor.values()) { color ->
                    AccentColorItem(
                        color = color,
                        selected = selectedAccentColor == color,
                        onClick = {
                            scope.launch { saveAccentColor(color) }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ThemeModeCard(
    title: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (selected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (selected)
            androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        else
            null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (selected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AccentColorItem(
    color: AccentColor,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colorValue = when (color) {
        AccentColor.BLUE -> Color(0xFF1976D2)
        AccentColor.PURPLE -> Color(0xFF7B1FA2)
        AccentColor.TEAL -> Color(0xFF00897B)
        AccentColor.ORANGE -> Color(0xFFF57C00)
        AccentColor.PINK -> Color(0xFFC2185B)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(colorValue)
                .then(
                    if (selected) {
                        Modifier.border(3.dp, MaterialTheme.colorScheme.onBackground, CircleShape)
                    } else {
                        Modifier
                    }
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "已选择",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = when (color) {
                AccentColor.BLUE -> "蓝色"
                AccentColor.PURPLE -> "紫色"
                AccentColor.TEAL -> "青色"
                AccentColor.ORANGE -> "橙色"
                AccentColor.PINK -> "粉色"
            },
            style = MaterialTheme.typography.bodySmall,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}