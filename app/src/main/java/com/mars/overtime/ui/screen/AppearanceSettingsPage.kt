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

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mars.overtime.ui.theme.*
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.*
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun AppearanceSettingsPage(
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    val selectedThemeMode by ThemeManager.themeMode.collectAsState()
    val selectedAccentColor by ThemeManager.accentColor.collectAsState()
    val dynamicColor by ThemeManager.dynamicColor.collectAsState()
    val fontScale by ThemeManager.fontScale.collectAsState()
    val bottomBarStyle by ThemeManager.bottomBarStyle.collectAsState()
    val quickReportMode by ThemeManager.quickReportMode.collectAsState()

    LaunchedEffect(Unit) {
        try {
            val db = com.mars.overtime.OvertimeApplication.database
            val configDao = db.configDao()
            val themeModeConfig = configDao.getConfig("theme_mode")
            val accentColorConfig = configDao.getConfig("accent_color")
            val dynamicColorConfig = configDao.getConfig("dynamic_color")
            val fontScaleConfig = configDao.getConfig("font_scale")
            val bottomBarStyleConfig = configDao.getConfig("bottom_bar_style")
            val quickReportModeConfig = configDao.getConfig("quick_report_mode")

            themeModeConfig?.value?.let { value ->
                ThemeManager.updateThemeMode(ThemeMode.valueOf(value))
            }
            accentColorConfig?.value?.let { value ->
                ThemeManager.updateAccentColor(AccentColor.valueOf(value))
            }
            dynamicColorConfig?.value?.let { value ->
                ThemeManager.updateDynamicColor(value.toBoolean())
            }
            fontScaleConfig?.value?.let { value ->
                ThemeManager.updateFontScale(FontScale.valueOf(value))
            }
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("外观设置") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(MiuixIcons.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "主题模式",
                style = MiuixTheme.textStyles.titleMedium,
                color = MiuixTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThemeModeCard(
                    title = "跟随系统",
                    icon = MiuixIcons.Palette,
                    selected = selectedThemeMode == ThemeMode.SYSTEM,
                    onClick = {
                        scope.launch { saveThemeMode(ThemeMode.SYSTEM) }
                    },
                    modifier = Modifier.weight(1f)
                )
                ThemeModeCard(
                    title = "浅色",
                    icon = MiuixIcons.LightMode,
                    selected = selectedThemeMode == ThemeMode.LIGHT,
                    onClick = {
                        scope.launch { saveThemeMode(ThemeMode.LIGHT) }
                    },
                    modifier = Modifier.weight(1f)
                )
                ThemeModeCard(
                    title = "深色",
                    icon = MiuixIcons.DarkMode,
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
                        style = MiuixTheme.textStyles.titleMedium
                    )
                    Text(
                        text = "根据壁纸自动调整主题颜色（Android 12+）",
                        style = MiuixTheme.textStyles.bodySmall,
                        color = MiuixTheme.colorScheme.onSurfaceVariant
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
                style = MiuixTheme.textStyles.titleMedium,
                color = MiuixTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (dynamicColor) "开启动态颜色时，强调色将混合到主题中" else "选择应用的主题强调色",
                style = MiuixTheme.textStyles.bodySmall,
                color = MiuixTheme.colorScheme.onSurfaceVariant
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

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "字体大小",
                style = MiuixTheme.textStyles.titleMedium,
                color = MiuixTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FontScale.values().forEach { scale ->
                    FontScaleChip(
                        title = ThemeManager.getFontScaleName(scale),
                        selected = fontScale == scale,
                        onClick = {
                            scope.launch { saveFontScale(scale) }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "底栏样式",
                style = MiuixTheme.textStyles.titleMedium,
                color = MiuixTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BottomBarStyle.values().forEach { style ->
                    BottomBarStyleCard(
                        title = ThemeManager.getBottomBarStyleName(style),
                        icon = when (style) {
                            BottomBarStyle.ICON_AND_TEXT -> MiuixIcons.ViewDay
                            BottomBarStyle.ICON_ONLY -> MiuixIcons.Image
                            BottomBarStyle.TEXT_ONLY -> MiuixIcons.TextFields
                        },
                        selected = bottomBarStyle == style,
                        onClick = {
                            scope.launch { saveBottomBarStyle(style) }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "快速提报模式",
                        style = MiuixTheme.textStyles.titleMedium
                    )
                    Text(
                        text = "开启后底栏中间按钮变成提交按钮，点击直接进入提报界面",
                        style = MiuixTheme.textStyles.bodySmall,
                        color = MiuixTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = quickReportMode,
                    onCheckedChange = { enabled ->
                        scope.launch {
                            try {
                                saveQuickReportMode(enabled)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                )
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
                MiuixTheme.colorScheme.primaryContainer
            else
                MiuixTheme.colorScheme.surfaceVariant
        ),
        border = if (selected)
            androidx.compose.foundation.BorderStroke(2.dp, MiuixTheme.colorScheme.primary)
        else
            null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (selected)
                    MiuixTheme.colorScheme.primary
                else
                    MiuixTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MiuixTheme.textStyles.bodySmall,
                color = if (selected)
                    MiuixTheme.colorScheme.primary
                else
                    MiuixTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun FontScaleChip(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(title, style = MiuixTheme.textStyles.bodySmall) },
        modifier = modifier,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MiuixTheme.colorScheme.primaryContainer,
            selectedLabelColor = MiuixTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Composable
fun BottomBarStyleCard(
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
                MiuixTheme.colorScheme.primaryContainer
            else
                MiuixTheme.colorScheme.surfaceVariant
        ),
        border = if (selected)
            androidx.compose.foundation.BorderStroke(2.dp, MiuixTheme.colorScheme.primary)
        else
            null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (selected)
                    MiuixTheme.colorScheme.primary
                else
                    MiuixTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MiuixTheme.textStyles.bodySmall,
                color = if (selected)
                    MiuixTheme.colorScheme.primary
                else
                    MiuixTheme.colorScheme.onSurfaceVariant
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
        AccentColor.RED -> Color(0xFFD32F2F)
        AccentColor.GREEN -> Color(0xFF388E3C)
        AccentColor.YELLOW -> Color(0xFFF9A825)
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
                        Modifier.border(3.dp, MiuixTheme.colorScheme.onBackground, CircleShape)
                    } else {
                        Modifier
                    }
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Icon(
                    imageVector = MiuixIcons.Check,
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
                AccentColor.RED -> "红色"
                AccentColor.GREEN -> "绿色"
                AccentColor.YELLOW -> "黄色"
            },
            style = MiuixTheme.textStyles.bodySmall,
            color = if (selected) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.onSurfaceVariant
        )
    }
}
