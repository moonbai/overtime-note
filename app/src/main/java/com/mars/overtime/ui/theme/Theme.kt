package com.mars.overtime.ui.theme

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.mars.overtime.OvertimeApplication
import kotlinx.coroutines.launch

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

enum class AccentColor {
    BLUE, PURPLE, TEAL, ORANGE, PINK, RED, GREEN, YELLOW
}

enum class FontScale {
    SMALL, NORMAL, LARGE, EXTRA_LARGE
}

enum class RadiusLevel {
    SMALL, MEDIUM, LARGE, EXTRA_LARGE
}

enum class BottomBarStyle {
    ICON_AND_TEXT, ICON_ONLY, TEXT_ONLY
}

private val BlueColorScheme = lightColorScheme(
    primary = Color(0xFF1976D2),
    secondary = Color(0xFF03A9F4),
    tertiary = Color(0xFF00BCD4)
)

private val BlueDarkColorScheme = darkColorScheme(
    primary = Color(0xFF64B5F6),
    secondary = Color(0xFF4FC3F7),
    tertiary = Color(0xFF26C6DA)
)

private val PurpleColorScheme = lightColorScheme(
    primary = Color(0xFF7B1FA2),
    secondary = Color(0xFF9C27B0),
    tertiary = Color(0xFFE91E63)
)

private val PurpleDarkColorScheme = darkColorScheme(
    primary = Color(0xFFCE93D8),
    secondary = Color(0xFFBA68C8),
    tertiary = Color(0xFFF48FB1)
)

private val TealColorScheme = lightColorScheme(
    primary = Color(0xFF00897B),
    secondary = Color(0xFF26A69A),
    tertiary = Color(0xFF009688)
)

private val TealDarkColorScheme = darkColorScheme(
    primary = Color(0xFF80CBC4),
    secondary = Color(0xFF4DB6AC),
    tertiary = Color(0xFF26A69A)
)

private val OrangeColorScheme = lightColorScheme(
    primary = Color(0xFFF57C00),
    secondary = Color(0xFFFF9800),
    tertiary = Color(0xFFFF5722)
)

private val OrangeDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFCC80),
    secondary = Color(0xFFFFB74D),
    tertiary = Color(0xFFFF8A65)
)

private val PinkColorScheme = lightColorScheme(
    primary = Color(0xFFC2185B),
    secondary = Color(0xFFE91E63),
    tertiary = Color(0xFFFF4081)
)

private val PinkDarkColorScheme = darkColorScheme(
    primary = Color(0xFFF48FB1),
    secondary = Color(0xFFF06292),
    tertiary = Color(0xFFFF80AB)
)

private val RedColorScheme = lightColorScheme(
    primary = Color(0xFFD32F2F),
    secondary = Color(0xFFE53935),
    tertiary = Color(0xFFFF5252)
)

private val RedDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFCDD2),
    secondary = Color(0xFFEF9A9A),
    tertiary = Color(0xFFFF8A80)
)

private val GreenColorScheme = lightColorScheme(
    primary = Color(0xFF388E3C),
    secondary = Color(0xFF43A047),
    tertiary = Color(0xFF66BB6A)
)

private val GreenDarkColorScheme = darkColorScheme(
    primary = Color(0xFFA5D6A7),
    secondary = Color(0xFF81C784),
    tertiary = Color(0xFFAED581)
)

private val YellowColorScheme = lightColorScheme(
    primary = Color(0xFFF9A825),
    secondary = Color(0xFFFFC107),
    tertiary = Color(0xFFFFEB3B)
)

private val YellowDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFF176),
    secondary = Color(0xFFFFD54F),
    tertiary = Color(0xFFFFEA00)
)

@Composable
fun OvertimeTheme(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current

    val themeMode by ThemeManager.themeMode.collectAsState()
    val accentColor by ThemeManager.accentColor.collectAsState()
    val dynamicColor by ThemeManager.dynamicColor.collectAsState()
    val fontScale by ThemeManager.fontScale.collectAsState()
    val radiusLevel by ThemeManager.radiusLevel.collectAsState()
    val bottomBarStyle by ThemeManager.bottomBarStyle.collectAsState()
    val quickReportMode by ThemeManager.quickReportMode.collectAsState()

    LaunchedEffect(Unit) {
        try {
            val db = OvertimeApplication.database
            val configDao = db.configDao()
            val themeModeConfig = configDao.getConfig("theme_mode")
            val accentColorConfig = configDao.getConfig("accent_color")
            val dynamicColorConfig = configDao.getConfig("dynamic_color")
            val fontScaleConfig = configDao.getConfig("font_scale")
            val radiusLevelConfig = configDao.getConfig("radius_level")
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
            radiusLevelConfig?.value?.let { value ->
                ThemeManager.updateRadiusLevel(RadiusLevel.valueOf(value))
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

    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> {
            when (accentColor) {
                AccentColor.BLUE -> BlueDarkColorScheme
                AccentColor.PURPLE -> PurpleDarkColorScheme
                AccentColor.TEAL -> TealDarkColorScheme
                AccentColor.ORANGE -> OrangeDarkColorScheme
                AccentColor.PINK -> PinkDarkColorScheme
                AccentColor.RED -> RedDarkColorScheme
                AccentColor.GREEN -> GreenDarkColorScheme
                AccentColor.YELLOW -> YellowDarkColorScheme
            }
        }
        else -> {
            when (accentColor) {
                AccentColor.BLUE -> BlueColorScheme
                AccentColor.PURPLE -> PurpleColorScheme
                AccentColor.TEAL -> TealColorScheme
                AccentColor.ORANGE -> OrangeColorScheme
                AccentColor.PINK -> PinkColorScheme
                AccentColor.RED -> RedColorScheme
                AccentColor.GREEN -> GreenColorScheme
                AccentColor.YELLOW -> YellowColorScheme
            }
        }
    }

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            )
            
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
            }
            
            WindowCompat.setDecorFitsSystemWindows(window, false)
            
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}

suspend fun saveThemeMode(mode: ThemeMode) {
    try {
        ThemeManager.updateThemeMode(mode)
        val db = OvertimeApplication.database
        val configDao = db.configDao()
        configDao.saveConfig(com.mars.overtime.database.AppConfig("theme_mode", mode.name))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

suspend fun saveAccentColor(color: AccentColor) {
    try {
        ThemeManager.updateAccentColor(color)
        val db = OvertimeApplication.database
        val configDao = db.configDao()
        configDao.saveConfig(com.mars.overtime.database.AppConfig("accent_color", color.name))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

suspend fun saveDynamicColor(enabled: Boolean) {
    try {
        ThemeManager.updateDynamicColor(enabled)
        val db = OvertimeApplication.database
        val configDao = db.configDao()
        configDao.saveConfig(com.mars.overtime.database.AppConfig("dynamic_color", enabled.toString()))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

suspend fun saveFontScale(scale: FontScale) {
    try {
        ThemeManager.updateFontScale(scale)
        val db = OvertimeApplication.database
        val configDao = db.configDao()
        configDao.saveConfig(com.mars.overtime.database.AppConfig("font_scale", scale.name))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

suspend fun saveRadiusLevel(level: RadiusLevel) {
    try {
        ThemeManager.updateRadiusLevel(level)
        val db = OvertimeApplication.database
        val configDao = db.configDao()
        configDao.saveConfig(com.mars.overtime.database.AppConfig("radius_level", level.name))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

suspend fun saveBottomBarStyle(style: BottomBarStyle) {
    try {
        ThemeManager.updateBottomBarStyle(style)
        val db = OvertimeApplication.database
        val configDao = db.configDao()
        configDao.saveConfig(com.mars.overtime.database.AppConfig("bottom_bar_style", style.name))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

suspend fun saveQuickReportMode(enabled: Boolean) {
    try {
        ThemeManager.updateQuickReportMode(enabled)
        val db = OvertimeApplication.database
        val configDao = db.configDao()
        configDao.saveConfig(com.mars.overtime.database.AppConfig("quick_report_mode", enabled.toString()))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
