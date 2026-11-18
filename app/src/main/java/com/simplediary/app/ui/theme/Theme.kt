package com.simplediary.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = SoftBlue,
    onPrimary = PureWhite,
    primaryContainer = SoftBlueContainer,
    onPrimaryContainer = SoftBlueDark,
    secondary = SoftBlueLight,
    onSecondary = TextPrimary,
    background = SoftBackground,
    onBackground = TextPrimary,
    surface = PureWhite,
    onSurface = TextPrimary,
    surfaceVariant = SoftBlueContainer,
    onSurfaceVariant = TextSecondary,
    outline = DividerSoft
)

@Composable
fun SimpleDiaryTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content
    )
}
