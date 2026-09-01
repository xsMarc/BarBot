package de.barbot.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Breite des Entwurfs: "BarBot App.dc.html" ist 1080 px breit, das sind 360 dp.
 * Alle Groessen in den Seiten sind auf diese Breite bezogen.
 */
val DesignWidth: Dp = 360.dp

private const val DESIGN_WIDTH_DP = 360f

/**
 * Hoehe, die der Kerninhalt einer Seite ohne Scrollen braucht. Bewusst kleiner
 * als die 800 dp des Entwurfs - alles darueber faengt der Scroll ab. Wuerde hier
 * die volle Entwurfshoehe stehen, bliebe der Massstab auf einem Tablet im
 * Querformat bei 1 und die Seite waere ein schmaler Streifen in der Mitte.
 */
private const val DESIGN_CORE_HEIGHT_DP = 560f

/** Unter dieser Hoehe (in Entwurfs-dp) wird es eng - vor allem im Querformat. */
val CompactHeight: Dp = 520.dp

private const val MIN_SCALE = 1f
private const val MAX_SCALE = 1.8f

/**
 * Faktor, um den der fuer ein Telefon gezeichnete Entwurf vergroessert wird.
 *
 * Auf einem Telefon kommt immer 1 heraus, dort aendert sich also nichts. Auf
 * einem Tablet waechst der Faktor mit dem kleineren der beiden Verhaeltnisse,
 * damit der Inhalt in beiden Richtungen passt.
 */
@Composable
fun rememberDesignScale(): Float {
    val config = LocalConfiguration.current
    val widthRatio = config.screenWidthDp / DESIGN_WIDTH_DP
    val heightRatio = config.screenHeightDp / DESIGN_CORE_HEIGHT_DP
    return minOf(widthRatio, heightRatio).coerceIn(MIN_SCALE, MAX_SCALE)
}

/**
 * Skaliert die Dichte, damit der Entwurf auf grossen Bildschirmen proportional
 * groesser wird: dp und sp wachsen gemeinsam, die Seiten rechnen unveraendert in
 * ihren Entwurfsmassen weiter.
 *
 * Die Fenster-Insets bleiben davon unberuehrt, die arbeiten intern in Pixeln.
 */
@Composable
fun ScaledDesign(scale: Float, content: @Composable () -> Unit) {
    val density = LocalDensity.current
    CompositionLocalProvider(
        LocalDensity provides Density(
            density = density.density * scale,
            fontScale = density.fontScale,
        ),
        content = content,
    )
}

/**
 * Zentrierte Inhaltsspalte in Entwurfsbreite. Hintergruende bleiben randlos und
 * fuellen das ganze Fenster, der Inhalt laeuft auf breiten Bildschirmen nicht
 * auseinander.
 */
@Composable
fun ContentColumn(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .widthIn(max = DesignWidth)
            .fillMaxSize(),
        content = content,
    )
}

/** Wie [ContentColumn], aber horizontal im umgebenden [Box] zentriert. */
@Composable
fun BoxScope.CenteredContentColumn(content: @Composable BoxScope.() -> Unit) {
    ContentColumn(modifier = Modifier.align(Alignment.TopCenter), content = content)
}
