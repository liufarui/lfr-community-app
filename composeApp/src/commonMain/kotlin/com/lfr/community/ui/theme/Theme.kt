package com.lfr.community.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4A9EFF),
    onPrimary = Color.White,
    surface = Color(0xFF1E1E2E),
    onSurface = Color(0xFFE0E0E0),
    background = Color(0xFF0F0F1A),
    onBackground = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2A2A3A),
    onSurfaceVariant = Color(0xFF888888),
    outline = Color(0xFF333344),
    secondary = Color(0xFF6C6CFF),
)

@Composable
fun CommunityTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography(),
        content = content
    )
}
