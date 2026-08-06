package de.barbot.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Dunkles "Bar bei Nacht"-Farbschema mit warmem Kupfer-Akzent.
val BarBotBackground = Color(0xFF0B0F14)
val BarBotSurface = Color(0xFF151B23)
val BarBotSurfaceElevated = Color(0xFF1E2732)
val BarBotAmber = Color(0xFFF5A524)
val BarBotMint = Color(0xFF3DDC97)
val BarBotText = Color(0xFFECEFF4)
val BarBotTextMuted = Color(0xFF8B98A9)
val BarBotOutline = Color(0xFF2C3743)
val BarBotError = Color(0xFFFF6B6B)

private val BarBotColorScheme = darkColorScheme(
    primary = BarBotAmber,
    onPrimary = Color(0xFF231703),
    secondary = BarBotMint,
    onSecondary = Color(0xFF04231A),
    background = BarBotBackground,
    onBackground = BarBotText,
    surface = BarBotSurface,
    onSurface = BarBotText,
    surfaceVariant = BarBotSurfaceElevated,
    onSurfaceVariant = BarBotTextMuted,
    outline = BarBotOutline,
    error = BarBotError,
)

private val BarBotTypography = Typography(
    displaySmall = TextStyle(fontSize = 40.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp),
    headlineMedium = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold),
    headlineSmall = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal),
    bodyMedium = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
    labelLarge = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.4.sp),
    labelMedium = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.8.sp),
)

@Composable
fun BarBotTheme(
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // Die App ist bewusst immer dunkel - sie wird auf Partys benutzt.
    MaterialTheme(
        colorScheme = BarBotColorScheme,
        typography = BarBotTypography,
        content = content,
    )
}
