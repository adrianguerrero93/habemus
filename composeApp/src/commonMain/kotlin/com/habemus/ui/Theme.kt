package com.habemus.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Darker blue with violet tint color palette
private val PrimaryBlue = Color(0xFF003D99)        // Dark blue with violet tint
private val SecondaryBlue = Color(0xFF0052CC)      // Medium dark blue
private val TertiaryBlue = Color(0xFF4D7FCC)       // Lighter blue with tint
private val ErrorRed = Color(0xFFD32F2F)           // Red for errors
private val BackgroundLight = Color(0xFFF1F3F8)    // Very light blue with violet tint
private val SurfaceLight = Color(0xFFFFFFFF)       // Pure white

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = SecondaryBlue,
    onPrimaryContainer = Color.White,
    secondary = TertiaryBlue,
    onSecondary = Color.White,
    secondaryContainer = SecondaryBlue,
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFF2D5C99),
    onTertiary = Color.White,
    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFCDD2),
    onErrorContainer = ErrorRed,
    background = BackgroundLight,
    onBackground = Color(0xFF1A1A1A),
    surface = SurfaceLight,
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFE8EEF7),
    onSurfaceVariant = Color(0xFF333333),
    outline = Color(0xFF6B8FCF),
    outlineVariant = Color(0xFFC5D3E8)
)

@Composable
fun HabemosBarTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}

