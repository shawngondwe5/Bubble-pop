package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GameColorScheme = lightColorScheme(
    primary = GameBlue,
    onPrimary = Color.White,
    primaryContainer = GameBlueLight,
    onPrimaryContainer = GameBlueDark,
    secondary = GameYellow,
    onSecondary = GameWoodDark,
    secondaryContainer = GameGold,
    onSecondaryContainer = GameWoodDark,
    tertiary = GamePurpleRibbon,
    onTertiary = Color.White,
    background = SurfaceCream,
    onBackground = TextDark,
    surface = Color.White,
    onSurface = TextDark
)

@Composable
fun BubblePopTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GameColorScheme,
        typography = GameTypography,
        content = content
    )
}
