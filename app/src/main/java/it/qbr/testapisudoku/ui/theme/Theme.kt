package it.qbr.testapisudoku.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


val AppColorScheme = lightColorScheme(
    primary = blue_p,
    onPrimary = Color.White,
    secondary = blue_secondary,
    onSecondary = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = background_rows,
    onSurface = gray,
    error = quit_background,
    onError = Color.Red,
)


@Composable
fun QBRSudokuTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = AppTypography,
        content = content
    )
}
