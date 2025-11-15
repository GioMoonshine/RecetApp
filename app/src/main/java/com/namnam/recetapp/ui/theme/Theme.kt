package com.namnam.recetapp.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = NegroTexto,
    onPrimary = BlancoFondo,
    secondary = GrisSecundario,
    onSecondary = BlancoFondo,
    background = BlancoFondo,
    onBackground = NegroTexto,
    surface = GrisSuave,
    onSurface = NegroTexto,
    surfaceVariant = GrisSuave,
    onSurfaceVariant = GrisSecundario,
    outline = GrisBorde,
    error = Color(0xFFDC3545)
)

private val DarkColors = darkColorScheme(
    primary = BlancoTexto,
    onPrimary = NegroFondo,
    secondary = GrisClaroSecundario,
    onSecondary = NegroFondo,
    background = NegroFondo,
    onBackground = BlancoTexto,
    surface = GrisOscuroSuperficie,
    onSurface = BlancoTexto,
    surfaceVariant = GrisOscuroSuperficie,
    onSurfaceVariant = GrisClaroSecundario,
    outline = GrisOscuroBorde,
    error = Color(0xFFFF5252)
)

@Composable
fun RecetAppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}