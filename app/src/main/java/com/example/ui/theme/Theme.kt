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

private val DarkColorScheme = darkColorScheme(
    primary = PersianGoldBright,
    onPrimary = PersianVioletDeep,
    primaryContainer = PersianVioletDark,
    onPrimaryContainer = PersianGoldLight,
    secondary = PersianTurquoise,
    onSecondary = PersianVioletDeep,
    secondaryContainer = PersianVioletMedium,
    onSecondaryContainer = PersianTurquoiseLight,
    tertiary = PersianGold,
    background = BackgroundDark,
    onBackground = Color(0xFFF8F6FD),
    surface = SurfaceDark,
    onSurface = Color(0xFFF8F6FD),
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFFD6CEEE)
)

private val LightColorScheme = lightColorScheme(
    primary = PersianVioletPrimary,
    onPrimary = Color.White,
    primaryContainer = PersianVioletSoft,
    onPrimaryContainer = PersianVioletPrimary,
    secondary = PersianGoldDark,
    onSecondary = Color.White,
    secondaryContainer = PersianGoldLight,
    onSecondaryContainer = PersianVioletDark,
    tertiary = PersianTurquoise,
    background = BackgroundLight,
    onBackground = PersianVioletDeep,
    surface = SurfaceLight,
    onSurface = PersianVioletDeep,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF4A3E6D)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep our authentic Persian Violet & Gold brand palette
    content: @Composable () -> Unit,
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
