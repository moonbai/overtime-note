package com.mars.overtime.ui.theme

import android.app.Activity
import android.os.Build
import android.view.View
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.mars.overtime.OvertimeApplication
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.ThemeController

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

private fun getFontScaleValue(scale: FontScale): Float {
    return when (scale) {
        FontScale.SMALL -> 0.85f
        FontScale.NORMAL -> 1.0f
        FontScale.LARGE -> 1.15f
        FontScale.EXTRA_LARGE -> 1.30f
    }
}

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

    val miuixMode = when (themeMode) {
        ThemeMode.SYSTEM -> if (dynamicColor) ColorSchemeMode.MonetSystem else ColorSchemeMode.System
        ThemeMode.LIGHT -> if (dynamicColor) ColorSchemeMode.MonetLight else ColorSchemeMode.Light
        ThemeMode.DARK -> if (dynamicColor) ColorSchemeMode.MonetDark else ColorSchemeMode.Dark
    }
    val themeController = remember { ThemeController(miuixMode) }

    val scaleFactor = getFontScaleValue(fontScale)

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

    MiuixTheme(
        controller = themeController,
        content = content
    )
}
