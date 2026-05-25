package com.mars.overtime.ui.theme

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ThemeManager {
    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _accentColor = MutableStateFlow(AccentColor.BLUE)
    val accentColor: StateFlow<AccentColor> = _accentColor.asStateFlow()

    private val _dynamicColor = MutableStateFlow(false)
    val dynamicColor: StateFlow<Boolean> = _dynamicColor.asStateFlow()

    fun updateThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }

    fun updateAccentColor(color: AccentColor) {
        _accentColor.value = color
    }

    fun updateDynamicColor(enabled: Boolean) {
        _dynamicColor.value = enabled
    }
}