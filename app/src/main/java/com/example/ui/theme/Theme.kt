package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Blue800,
    onPrimary = Color.White,
    primaryContainer = Blue100,
    onPrimaryContainer = Blue900,
    secondary = Blue600,
    onSecondary = Color.White,
    secondaryContainer = Blue50,
    onSecondaryContainer = Blue800,
    tertiary = Yellow500,
    onTertiary = Slate800,
    tertiaryContainer = Yellow100,
    onTertiaryContainer = Yellow600,
    background = BackgroundLight,
    onBackground = Slate800,
    surface = SurfaceLight,
    onSurface = Slate800,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate600,
    outline = Slate300
)

private val DarkColorScheme = darkColorScheme(
    primary = Blue500,
    onPrimary = Color.White,
    primaryContainer = Blue900,
    onPrimaryContainer = Blue100,
    secondary = Blue600,
    onSecondary = Color.White,
    secondaryContainer = Blue800,
    onSecondaryContainer = Blue50,
    tertiary = Yellow400,
    onTertiary = Slate800,
    tertiaryContainer = Yellow600,
    onTertiaryContainer = Yellow100,
    background = Slate800,
    onBackground = Color.White,
    surface = Slate700,
    onSurface = Color.White,
    surfaceVariant = Slate600,
    onSurfaceVariant = Slate200,
    outline = Slate500
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our crisp educational blue & yellow theme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
