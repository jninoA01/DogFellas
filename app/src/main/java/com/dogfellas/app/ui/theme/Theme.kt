package com.dogfellas.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DogFellasColorScheme = lightColorScheme(
    primary        = BrownLight,
    onPrimary      = TextDark,
    secondary      = GreenBtn,
    onSecondary    = Color.White,
    background     = Cream,
    onBackground   = TextDark,
    surface        = Tan,
    onSurface      = TextDark,
    error          = IconRed,
)

@Composable
fun DogFellasTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DogFellasColorScheme,
        content = content
    )
}
