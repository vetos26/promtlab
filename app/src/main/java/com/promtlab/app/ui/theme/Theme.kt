package com.promtlab.app.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.promtlab.app.data.settings.AppSettings
import com.promtlab.app.data.settings.ThemeMode

@Composable
fun PromtLabTheme(
    settings: AppSettings = AppSettings(),
    content: @Composable () -> Unit
) {
    val darkTheme = when (settings.themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val context = LocalContext.current
    val colorScheme = when {
        settings.dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)

        darkTheme -> darkScheme(settings.accent)
        else -> lightScheme(settings.accent)
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            view.context.findActivity()?.window?.let { window ->
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !darkTheme
                controller.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private fun lightScheme(accent: AccentPalette): ColorScheme {
    val primary = accent.primaryLight
    val secondary = accent.secondaryLight
    val tertiary = lerp(primary, secondary, 0.5f)
    return lightColorScheme(
        primary = primary,
        onPrimary = Color.White,
        primaryContainer = lerp(primary, Color.White, 0.86f),
        onPrimaryContainer = lerp(primary, Color.Black, 0.42f),
        secondary = secondary,
        onSecondary = Color.White,
        secondaryContainer = lerp(secondary, Color.White, 0.86f),
        onSecondaryContainer = lerp(secondary, Color.Black, 0.42f),
        tertiary = tertiary,
        onTertiary = Color.White,
        tertiaryContainer = lerp(tertiary, Color.White, 0.86f),
        onTertiaryContainer = lerp(tertiary, Color.Black, 0.42f),
        background = Color(0xFFF6F4FB),
        onBackground = Color(0xFF15131B),
        surface = Color(0xFFFDFBFF),
        onSurface = Color(0xFF15131B),
        surfaceVariant = Color(0xFFE7E2F0),
        onSurfaceVariant = Color(0xFF5E5870),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFF9F7FE),
        surfaceContainer = Color(0xFFF3F0FA),
        surfaceContainerHigh = Color(0xFFEDE9F6),
        surfaceContainerHighest = Color(0xFFE7E2F0),
        outline = Color(0xFFC9C3D6),
        outlineVariant = Color(0xFFE1DCEB),
        error = Color(0xFFBA1A1A),
        onError = Color.White,
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002),
        inverseSurface = Color(0xFF312E39),
        inverseOnSurface = Color(0xFFF4EFF9),
        inversePrimary = accent.primaryDark,
        scrim = Color.Black
    )
}

private fun darkScheme(accent: AccentPalette): ColorScheme {
    val primary = accent.primaryDark
    val secondary = accent.secondaryDark
    val tertiary = lerp(primary, secondary, 0.5f)
    return darkColorScheme(
        primary = primary,
        onPrimary = Color(0xFF1B1626),
        primaryContainer = lerp(primary, Color.Black, 0.68f),
        onPrimaryContainer = lerp(primary, Color.White, 0.82f),
        secondary = secondary,
        onSecondary = Color(0xFF101A17),
        secondaryContainer = lerp(secondary, Color.Black, 0.68f),
        onSecondaryContainer = lerp(secondary, Color.White, 0.82f),
        tertiary = tertiary,
        onTertiary = Color(0xFF17121F),
        tertiaryContainer = lerp(tertiary, Color.Black, 0.68f),
        onTertiaryContainer = lerp(tertiary, Color.White, 0.82f),
        background = Color(0xFF0C0B11),
        onBackground = Color(0xFFF3EFFA),
        surface = Color(0xFF121018),
        onSurface = Color(0xFFF3EFFA),
        surfaceVariant = Color(0xFF2A2736),
        onSurfaceVariant = Color(0xFFB7B0C6),
        surfaceContainerLowest = Color(0xFF0A0910),
        surfaceContainerLow = Color(0xFF17151F),
        surfaceContainer = Color(0xFF1C1A26),
        surfaceContainerHigh = Color(0xFF221F2D),
        surfaceContainerHighest = Color(0xFF2A2736),
        outline = Color(0xFF8D86A0),
        outlineVariant = Color(0xFF322E3F),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),
        inverseSurface = Color(0xFFF3EFFA),
        inverseOnSurface = Color(0xFF312E39),
        inversePrimary = accent.primaryLight,
        scrim = Color.Black
    )
}
