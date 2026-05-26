package com.mars.overtime.ui.theme

import android.app.Activity
import android.os.Build
import android.view.View
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.mars.overtime.OvertimeApplication

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
    onPrimary = Color.White,
    secondary = Color(0xFF03A9F4),
    onSecondary = Color.White,
    tertiary = Color(0xFF00BCD4),
    onTertiary = Color.White,
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    primaryContainer = Color(0xFFD1E4FF),
    onPrimaryContainer = Color(0xFF001D36),
    secondaryContainer = Color(0xFFE1F5FE),
    onSecondaryContainer = Color(0xFF001F29)
)

private val BlueDarkColorScheme = darkColorScheme(
    primary = Color(0xFF64B5F6),
    onPrimary = Color(0xFF003258),
    secondary = Color(0xFF4FC3F7),
    onSecondary = Color(0xFF003547),
    tertiary = Color(0xFF26C6DA),
    onTertiary = Color(0xFF00363D),
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    primaryContainer = Color(0xFF00497D),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondaryContainer = Color(0xFF004D64),
    onSecondaryContainer = Color(0xFFB8EAFF)
)

private val PurpleColorScheme = lightColorScheme(
    primary = Color(0xFF7B1FA2),
    onPrimary = Color.White,
    secondary = Color(0xFF9C27B0),
    onSecondary = Color.White,
    tertiary = Color(0xFFE91E63),
    onTertiary = Color.White,
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    primaryContainer = Color(0xFFF3E5F5),
    onPrimaryContainer = Color(0xFF21005E),
    secondaryContainer = Color(0xFFFCE4EC),
    onSecondaryContainer = Color(0xFF3E001E)
)

private val PurpleDarkColorScheme = darkColorScheme(
    primary = Color(0xFFCE93D8),
    onPrimary = Color(0xFF4A0072),
    secondary = Color(0xFFBA68C8),
    onSecondary = Color(0xFF4A0072),
    tertiary = Color(0xFFF48FB1),
    onTertiary = Color(0xFF5E1133),
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    primaryContainer = Color(0xFF6A0085),
    onPrimaryContainer = Color(0xFFF3E5F5),
    secondaryContainer = Color(0xFF7E2054),
    onSecondaryContainer = Color(0xFFFFD9E3)
)

private val TealColorScheme = lightColorScheme(
    primary = Color(0xFF00897B),
    onPrimary = Color.White,
    secondary = Color(0xFF26A69A),
    onSecondary = Color.White,
    tertiary = Color(0xFF009688),
    onTertiary = Color.White,
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    primaryContainer = Color(0xFFB2DFDB),
    onPrimaryContainer = Color(0xFF00201D),
    secondaryContainer = Color(0xFFE0F2F1),
    onSecondaryContainer = Color(0xFF00251A)
)

private val TealDarkColorScheme = darkColorScheme(
    primary = Color(0xFF80CBC4),
    onPrimary = Color(0xFF003733),
    secondary = Color(0xFF4DB6AC),
    onSecondary = Color(0xFF003731),
    tertiary = Color(0xFF26A69A),
    onTertiary = Color(0xFF003733),
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    primaryContainer = Color(0xFF00504A),
    onPrimaryContainer = Color(0xFFB2DFDB),
    secondaryContainer = Color(0xFF004D47),
    onSecondaryContainer = Color(0xFFA7F3EC)
)

private val OrangeColorScheme = lightColorScheme(
    primary = Color(0xFFF57C00),
    onPrimary = Color.White,
    secondary = Color(0xFFFF9800),
    onSecondary = Color.White,
    tertiary = Color(0xFFFF5722),
    onTertiary = Color.White,
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    primaryContainer = Color(0xFFFFE0B2),
    onPrimaryContainer = Color(0xFF331B00),
    secondaryContainer = Color(0xFFFFF3E0),
    onSecondaryContainer = Color(0xFF331A00)
)

private val OrangeDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFCC80),
    onPrimary = Color(0xFF462A00),
    secondary = Color(0xFFFFB74D),
    onSecondary = Color(0xFF462A00),
    tertiary = Color(0xFFFF8A65),
    onTertiary = Color(0xFF5C1900),
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    primaryContainer = Color(0xFF6B4000),
    onPrimaryContainer = Color(0xFFFFE0B2),
    secondaryContainer = Color(0xFF995900),
    onSecondaryContainer = Color(0xFFFFDCC2)
)

private val PinkColorScheme = lightColorScheme(
    primary = Color(0xFFC2185B),
    onPrimary = Color.White,
    secondary = Color(0xFFE91E63),
    onSecondary = Color.White,
    tertiary = Color(0xFFFF4081),
    onTertiary = Color.White,
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    primaryContainer = Color(0xFFFCE4EC),
    onPrimaryContainer = Color(0xFF3E001D),
    secondaryContainer = Color(0xFFFCE4EC),
    onSecondaryContainer = Color(0xFF3E001D)
)

private val PinkDarkColorScheme = darkColorScheme(
    primary = Color(0xFFF48FB1),
    onPrimary = Color(0xFF5E0029),
    secondary = Color(0xFFF06292),
    onSecondary = Color(0xFF5E0029),
    tertiary = Color(0xFFFF80AB),
    onTertiary = Color(0xFF5E0033),
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    primaryContainer = Color(0xFF8C0043),
    onPrimaryContainer = Color(0xFFFCE4EC),
    secondaryContainer = Color(0xFF7A003C),
    onSecondaryContainer = Color(0xFFFFD9E3)
)

private val RedColorScheme = lightColorScheme(
    primary = Color(0xFFD32F2F),
    onPrimary = Color.White,
    secondary = Color(0xFFE53935),
    onSecondary = Color.White,
    tertiary = Color(0xFFFF5252),
    onTertiary = Color.White,
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    primaryContainer = Color(0xFFFFDAD6),
    onPrimaryContainer = Color(0xFF410002),
    secondaryContainer = Color(0xFFFFDAD6),
    onSecondaryContainer = Color(0xFF410002)
)

private val RedDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFCDD2),
    onPrimary = Color(0xFF690005),
    secondary = Color(0xFFEF9A9A),
    onSecondary = Color(0xFF690005),
    tertiary = Color(0xFFFF8A80),
    onTertiary = Color(0xFF690005),
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    primaryContainer = Color(0xFF93000A),
    onPrimaryContainer = Color(0xFFFFDAD6),
    secondaryContainer = Color(0xFF7D0000),
    onSecondaryContainer = Color(0xFFFFDAD6)
)

private val GreenColorScheme = lightColorScheme(
    primary = Color(0xFF388E3C),
    onPrimary = Color.White,
    secondary = Color(0xFF43A047),
    onSecondary = Color.White,
    tertiary = Color(0xFF66BB6A),
    onTertiary = Color.White,
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color(0xFF002106),
    secondaryContainer = Color(0xFFE8F5E9),
    onSecondaryContainer = Color(0xFF002106)
)

private val GreenDarkColorScheme = darkColorScheme(
    primary = Color(0xFFA5D6A7),
    onPrimary = Color(0xFF003910),
    secondary = Color(0xFF81C784),
    onSecondary = Color(0xFF003910),
    tertiary = Color(0xFFAED581),
    onTertiary = Color(0xFF1B5E20),
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    primaryContainer = Color(0xFF005319),
    onPrimaryContainer = Color(0xFFC8E6C9),
    secondaryContainer = Color(0xFF004013),
    onSecondaryContainer = Color(0xFFA5F0B7)
)

private val YellowColorScheme = lightColorScheme(
    primary = Color(0xFFF9A825),
    onPrimary = Color.Black,
    secondary = Color(0xFFFFC107),
    onSecondary = Color.Black,
    tertiary = Color(0xFFFFEB3B),
    onTertiary = Color.Black,
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    primaryContainer = Color(0xFFFFF9C4),
    onPrimaryContainer = Color(0xFF2D2000),
    secondaryContainer = Color(0xFFFFFDE7),
    onSecondaryContainer = Color(0xFF2D2000)
)

private val YellowDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFF176),
    onPrimary = Color(0xFF3D2F00),
    secondary = Color(0xFFFFD54F),
    onSecondary = Color(0xFF3D2F00),
    tertiary = Color(0xFFFFEA00),
    onTertiary = Color(0xFF3D3000),
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    primaryContainer = Color(0xFF584500),
    onPrimaryContainer = Color(0xFFFFF9C4),
    secondaryContainer = Color(0xFF4D3C00),
    onSecondaryContainer = Color(0xFFFFEFC7)
)

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

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val baseScheme = if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            val accentPrimary = when (accentColor) {
                AccentColor.BLUE -> Color(0xFF1976D2)
                AccentColor.PURPLE -> Color(0xFF7B1FA2)
                AccentColor.TEAL -> Color(0xFF00897B)
                AccentColor.ORANGE -> Color(0xFFF57C00)
                AccentColor.PINK -> Color(0xFFC2185B)
                AccentColor.RED -> Color(0xFFD32F2F)
                AccentColor.GREEN -> Color(0xFF388E3C)
                AccentColor.YELLOW -> Color(0xFFF9A825)
            }
            baseScheme.copy(
                primary = if (darkTheme) accentPrimary.copy(alpha = 0.9f) else accentPrimary,
                onPrimary = if (darkTheme) Color.White else Color.White,
                primaryContainer = accentPrimary.copy(alpha = 0.12f),
                onPrimaryContainer = accentPrimary
            )
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

    val scaleFactor = getFontScaleValue(fontScale)

    val typography = Typography(
        displayLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (57 * scaleFactor).sp,
            lineHeight = (64 * scaleFactor).sp,
            letterSpacing = (-0.25).sp
        ),
        displayMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (45 * scaleFactor).sp,
            lineHeight = (52 * scaleFactor).sp,
            letterSpacing = 0.sp
        ),
        displaySmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (36 * scaleFactor).sp,
            lineHeight = (44 * scaleFactor).sp,
            letterSpacing = 0.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (32 * scaleFactor).sp,
            lineHeight = (40 * scaleFactor).sp,
            letterSpacing = 0.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (28 * scaleFactor).sp,
            lineHeight = (36 * scaleFactor).sp,
            letterSpacing = 0.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (24 * scaleFactor).sp,
            lineHeight = (32 * scaleFactor).sp,
            letterSpacing = 0.sp
        ),
        titleLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (22 * scaleFactor).sp,
            lineHeight = (28 * scaleFactor).sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = (16 * scaleFactor).sp,
            lineHeight = (24 * scaleFactor).sp,
            letterSpacing = 0.15.sp
        ),
        titleSmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = (14 * scaleFactor).sp,
            lineHeight = (20 * scaleFactor).sp,
            letterSpacing = 0.1.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (16 * scaleFactor).sp,
            lineHeight = (24 * scaleFactor).sp,
            letterSpacing = 0.5.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (14 * scaleFactor).sp,
            lineHeight = (20 * scaleFactor).sp,
            letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (12 * scaleFactor).sp,
            lineHeight = (16 * scaleFactor).sp,
            letterSpacing = 0.4.sp
        ),
        labelLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = (14 * scaleFactor).sp,
            lineHeight = (20 * scaleFactor).sp,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = (12 * scaleFactor).sp,
            lineHeight = (16 * scaleFactor).sp,
            letterSpacing = 0.5.sp
        ),
        labelSmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = (11 * scaleFactor).sp,
            lineHeight = (16 * scaleFactor).sp,
            letterSpacing = 0.5.sp
        )
    )

    val shapes = Shapes(
        extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
        small = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        medium = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        large = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(28.dp)
    )

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
        typography = typography,
        shapes = shapes,
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
