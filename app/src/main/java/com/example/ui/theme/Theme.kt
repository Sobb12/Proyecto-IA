package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = OnEmerald,
    primaryContainer = MintContainer,
    onPrimaryContainer = OnMintContainer,
    secondary = TerracottaSecondary,
    onSecondary = OnTerracotta,
    secondaryContainer = PeachContainer,
    onSecondaryContainer = OnPeachContainer,
    tertiary = DewTertiary,
    onTertiary = OnDew,
    tertiaryContainer = SkyContainer,
    onTertiaryContainer = OnSkyContainer,
    background = SunlitBackground,
    onBackground = OnSunlitBackground,
    surface = SunlitSurface,
    onSurface = OnSunlitBackground,
    surfaceVariant = SunlitSurfaceVariant,
    onSurfaceVariant = OnSunlitSurfaceVariant,
    outline = SageOutline,
    outlineVariant = SageOutlineVariant
)

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldDarkPrimary,
    onPrimary = OnEmeraldDark,
    primaryContainer = MintDarkContainer,
    onPrimaryContainer = OnMintDarkContainer,
    secondary = TerracottaDarkSecondary,
    onSecondary = OnTerracottaDark,
    secondaryContainer = PeachDarkContainer,
    onSecondaryContainer = OnPeachDarkContainer,
    tertiary = DewDarkTertiary,
    onTertiary = OnDewDark,
    tertiaryContainer = SkyDarkContainer,
    onTertiaryContainer = OnSkyDarkContainer,
    background = NightBackground,
    onBackground = OnNightBackground,
    surface = NightSurface,
    onSurface = OnNightBackground,
    surfaceVariant = NightSurfaceVariant,
    onSurfaceVariant = OnNightSurfaceVariant,
    outline = NightOutline,
    outlineVariant = NightOutlineVariant
)

val SproutShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun SproutPalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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
        typography = SproutTypography,
        shapes = SproutShapes,
        content = content
    )
}
