package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.domain.model.ThemeMode

private val NoorDarkColorScheme = darkColorScheme(
    primary = EmeraldSoft,
    onPrimary = EmeraldDeep,
    primaryContainer = EmeraldDeep,
    onPrimaryContainer = EmeraldMist,
    secondary = GoldPrimary,
    onSecondary = NightBlack,
    secondaryContainer = GoldContainerDark,
    onSecondaryContainer = GoldBright,
    tertiary = EmeraldBright,
    onTertiary = Color.White,
    background = NightBlack,
    onBackground = DarkInk,
    surface = NightSurface,
    onSurface = DarkInk,
    surfaceVariant = NightSurfaceElevated,
    onSurfaceVariant = DarkInkMuted,
    outline = Color(0xFF284239),
    outlineVariant = Color(0xFF192E27),
    error = SemanticError,
    onError = Color.White
)

private val NoorLightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = EmeraldMist,
    onPrimaryContainer = EmeraldDeep,
    secondary = GoldMuted,
    onSecondary = Color.White,
    secondaryContainer = GoldContainerLight,
    onSecondaryContainer = Color(0xFF4A3611),
    tertiary = EmeraldBright,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightInk,
    surface = LightSurface,
    onSurface = LightInk,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightInkMuted,
    outline = Color(0xFFC1D3CB),
    outlineVariant = Color(0xFFDAE6E0),
    error = SemanticError,
    onError = Color.White
)

val NoorShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(34.dp)
)

data class NoorExtendedColors(
    val isDark: Boolean,
    val glassSurface: Color,
    val glassSurfaceStrong: Color,
    val glassBorderTop: Color,
    val glassBorderBottom: Color,
    val goldAccent: Color,
    val goldSoft: Color,
    val emeraldGlow: Color,
    val patternTint: Color
)

val LocalNoorColors = staticCompositionLocalOf {
    NoorExtendedColors(
        isDark = true,
        glassSurface = NightSurfaceGlass,
        glassSurfaceStrong = NightSurfaceElevated,
        glassBorderTop = GlassHighlightDark,
        glassBorderBottom = GlassBorderDark,
        goldAccent = GoldPrimary,
        goldSoft = GoldBright,
        emeraldGlow = EmeraldPrimary,
        patternTint = Color(0x18D4AF6A)
    )
}

@Composable
fun NoorTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (isDark) NoorDarkColorScheme else NoorLightColorScheme
    val extendedColors = if (isDark) {
        NoorExtendedColors(
            isDark = true,
            glassSurface = Color(0xB80E1E19),
            glassSurfaceStrong = Color(0xDD132922),
            glassBorderTop = Color(0x36FFFFFF),
            glassBorderBottom = Color(0x30D4AF6A),
            goldAccent = GoldPrimary,
            goldSoft = GoldBright,
            emeraldGlow = EmeraldPrimary,
            patternTint = Color(0x18D4AF6A)
        )
    } else {
        NoorExtendedColors(
            isDark = false,
            glassSurface = Color(0xCCFFFFFF),
            glassSurfaceStrong = Color(0xF0FFFFFF),
            glassBorderTop = Color(0xE6FFFFFF),
            glassBorderBottom = Color(0x380E6B4D),
            goldAccent = GoldMuted,
            goldSoft = GoldPrimary,
            emeraldGlow = EmeraldMist,
            patternTint = Color(0x160E6B4D)
        )
    }

    CompositionLocalProvider(LocalNoorColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = NoorTypography,
            shapes = NoorShapes,
            content = content
        )
    }
}
