package de.barbot.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import de.barbot.app.R

// Farben aus "BarBot App.dc.html".
val Lime = Color(0xFFC5FF5C)
val Ink = Color(0xFF0D0D0D)
val CardWhite = Color(0xFFFFFFFF)
val Field = Color(0xFFF2F2F2)
val FieldSoft = Color(0xFFF7F7F7)
val FieldDim = Color(0xFFE9E9E9)
val BadgeGrey = Color(0xFFE4E4E4)
val DividerGrey = Color(0xFFECECEC)
val TrackGrey = Color(0xFFE2E2E2)

// Die Graustufen im Design sind Schwarz mit Alpha - hier als Konstanten.
val Ink62 = Ink.copy(alpha = 0.62f)
val Ink55 = Ink.copy(alpha = 0.55f)
val Ink50 = Ink.copy(alpha = 0.50f)
val Ink45 = Ink.copy(alpha = 0.45f)
val Ink42 = Ink.copy(alpha = 0.42f)
val Ink40 = Ink.copy(alpha = 0.40f)
val Ink35 = Ink.copy(alpha = 0.35f)

val Montserrat = FontFamily(
    Font(R.font.montserrat_regular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.montserrat_semibold, FontWeight.SemiBold, FontStyle.Normal),
    Font(R.font.montserrat_bold, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.montserrat_medium_italic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.montserrat_semibold_italic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.montserrat_bold_italic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.montserrat_extrabold_italic, FontWeight.ExtraBold, FontStyle.Italic),
)

/**
 * Textstile aus dem Design. Die Groessen sind die Canvas-Werte geteilt durch 3
 * (das Design ist 1080 px breit, das entspricht 360 dp auf dem Geraet).
 */
object BarBotType {
    /** 76 px - "Mit BarBot verbinden", "Getraenk waehlen" */
    val Display = TextStyle(
        fontFamily = Montserrat, fontWeight = FontWeight.ExtraBold, fontStyle = FontStyle.Italic,
        fontSize = 25.sp, lineHeight = 26.5.sp, letterSpacing = (-0.5).sp, color = Ink,
    )

    /** 82 px - Drinkname auf der Infoseite */
    val Hero = TextStyle(
        fontFamily = Montserrat, fontWeight = FontWeight.ExtraBold, fontStyle = FontStyle.Italic,
        fontSize = 27.5.sp, letterSpacing = (-0.7).sp, color = Ink,
    )

    /** 46 px - Drinkname in der Liste */
    val DrinkName = TextStyle(
        fontFamily = Montserrat, fontWeight = FontWeight.ExtraBold, fontStyle = FontStyle.Italic,
        fontSize = 15.sp, letterSpacing = (-0.2).sp, color = Ink,
    )

    /** 36/30 px - Abschnittslabel wie "Gefundene Geraete", "Zutaten" */
    val SectionLabel = TextStyle(
        fontFamily = Montserrat, fontWeight = FontWeight.ExtraBold, fontStyle = FontStyle.Italic,
        fontSize = 11.sp, letterSpacing = 0.2.sp, color = Ink45,
    )

    /** 42 px - Geraetename, Text im Sperrbalken */
    val Strong = TextStyle(
        fontFamily = Montserrat, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic,
        fontSize = 14.sp, color = Ink,
    )

    /** 32 px - Zeile unter dem Drinknamen */
    val Meta = TextStyle(
        fontFamily = Montserrat, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic,
        fontSize = 10.5.sp, color = Ink50,
    )

    /** 25 px - Mengenangabe in der Liste */
    val MetaSmall = TextStyle(
        fontFamily = Montserrat, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic,
        fontSize = 8.5.sp, color = Ink42,
    )

    /** 30 px - Zutatentext */
    val Body = TextStyle(
        fontFamily = Montserrat, fontWeight = FontWeight.Medium, fontStyle = FontStyle.Italic,
        fontSize = 10.sp, lineHeight = 13.5.sp, color = Ink,
    )

    /** 26/28 px - Zutaten in der Liste, Statuszeilen */
    val BodySmall = TextStyle(
        fontFamily = Montserrat, fontWeight = FontWeight.Medium, fontStyle = FontStyle.Italic,
        fontSize = 9.sp, lineHeight = 11.5.sp, color = Ink62,
    )

    /** 30 px - "Bluetooth aktiv - Suche laeuft" */
    val Status = TextStyle(
        fontFamily = Montserrat, fontWeight = FontWeight.SemiBold, fontStyle = FontStyle.Italic,
        fontSize = 10.sp, color = Ink55,
    )

    /** 46 px - Beschriftung der Aktionsleiste unten */
    val Action = TextStyle(
        fontFamily = Montserrat, fontWeight = FontWeight.SemiBold, fontStyle = FontStyle.Normal,
        fontSize = 15.5.sp, color = Ink,
    )

    /** 32 px - "Verbinden"-Pille in der Geraeteliste */
    val Pill = TextStyle(
        fontFamily = Montserrat, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Normal,
        fontSize = 10.5.sp, color = Ink,
    )

    /** 44 px - "BT"-Kachel */
    val Badge = TextStyle(
        fontFamily = Montserrat, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Normal,
        fontSize = 14.5.sp, color = Ink,
    )
}

private val BarBotColorScheme = lightColorScheme(
    primary = Lime,
    onPrimary = Ink,
    secondary = Lime,
    onSecondary = Ink,
    background = CardWhite,
    onBackground = Ink,
    surface = CardWhite,
    onSurface = Ink,
    surfaceVariant = Field,
    onSurfaceVariant = Ink55,
    outline = DividerGrey,
)

@Composable
fun BarBotTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BarBotColorScheme,
        typography = Typography(
            bodyLarge = BarBotType.Body,
            bodyMedium = BarBotType.BodySmall,
            titleMedium = BarBotType.Strong,
        ),
        content = content,
    )
}
