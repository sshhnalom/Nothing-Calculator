package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NothingColorScheme = darkColorScheme(
    primary = BrightWhite,
    onPrimary = AbsoluteBlack,
    secondary = NothingBlue,
    onSecondary = BrightWhite,
    tertiary = NothingRed,
    onTertiary = BrightWhite,
    background = AbsoluteBlack,
    onBackground = BrightWhite,
    surface = DarkCharcoal,
    onSurface = BrightWhite,
    surfaceVariant = LightCharcoal,
    onSurfaceVariant = BrightWhite
)

private val NothingLightColorScheme = lightColorScheme(
    primary = AbsoluteBlack,
    onPrimary = BrightWhite,
    secondary = NothingBlue,
    onSecondary = BrightWhite,
    tertiary = NothingRed,
    onTertiary = BrightWhite,
    background = Color(0xFFF9F9F9),
    onBackground = AbsoluteBlack,
    surface = Color(0xFFEEEEEE),
    onSurface = AbsoluteBlack,
    surfaceVariant = Color(0xFFE2E2E2),
    onSurfaceVariant = AbsoluteBlack
)

@Composable
fun MyApplicationTheme(
    themeMode: String = "System Default",
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        "Light" -> false
        "Dark" -> true
        else -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) NothingColorScheme else NothingLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
