package com.diet.android.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = MD3DarkPrimary,
    onPrimary = MD3DarkOnPrimary,
    primaryContainer = MD3DarkPrimaryContainer,
    onPrimaryContainer = MD3DarkOnPrimaryContainer,
    secondary = MD3DarkSecondary,
    onSecondary = MD3DarkOnSecondary,
    background = MD3DarkBackground,
    onBackground = MD3DarkOnBackground,
    surface = MD3DarkSurface,
    onSurface = MD3DarkOnSurface,
    surfaceVariant = MD3DarkSurfaceVariant,
    onSurfaceVariant = MD3DarkOnSurfaceVariant,
    outline = MD3DarkOutline,
    error = MD3DarkError,
    onError = MD3DarkOnError
)

private val LightColorScheme = lightColorScheme(
    primary = MD3LightPrimary,
    onPrimary = MD3LightOnPrimary,
    primaryContainer = MD3LightPrimaryContainer,
    onPrimaryContainer = MD3LightOnPrimaryContainer,
    secondary = MD3LightSecondary,
    onSecondary = MD3LightOnSecondary,
    background = MD3LightBackground,
    onBackground = MD3LightOnBackground,
    surface = MD3LightSurface,
    onSurface = MD3LightOnSurface,
    surfaceVariant = MD3LightSurfaceVariant,
    onSurfaceVariant = MD3LightOnSurfaceVariant,
    outline = MD3LightOutline,
    error = MD3LightError,
    onError = MD3LightOnError
)

@Composable
fun DietAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? Activity
            if (activity != null) {
                val window = activity.window
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
