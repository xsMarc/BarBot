package de.barbot.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.barbot.app.R
import de.barbot.app.ui.CompactHeight
import de.barbot.app.ui.components.DrinkArt
import de.barbot.app.ui.components.ScreenBackground
import de.barbot.app.ui.theme.BarBotType
import de.barbot.app.ui.theme.Lime

/**
 * Seite 1: Startseite.
 *
 * Aufbau aus dem Design: Logo oben, freigestellter Drink mittig auf 47 % Hoehe,
 * Start-Button unten mit 150 px (50 dp) Abstand zum Rand.
 */
@Composable
fun StartScreen(onStart: () -> Unit) {
    ScreenBackground(resId = R.drawable.bg_fruits) {
        // Das Fruchtbild laeuft hinter Status- und Navigationsleiste durch,
        // nur der Inhalt haelt Abstand.
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars),
        ) {
            // Im Querformat bleibt kaum Hoehe: Drinkbild und Abstaende schrumpfen
            // mit, statt dass sich Logo und Button ueberlagern.
            val compact = maxHeight < CompactHeight
            val artHeight = (maxHeight * 0.42f).coerceIn(120.dp, 307.dp)
            val artWidth = artHeight * (333f / 307f)

            Image(
                painter = painterResource(R.drawable.logo_glass),
                contentDescription = "BarBot",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = if (compact) 12.dp else 32.dp)
                    .width(135.dp)
                    .height(if (compact) 52.dp else 75.dp),
            )

            // Im Design sitzt der Drink mittig auf 47 % der Hoehe.
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(modifier = Modifier.weight(0.47f))
                DrinkArt(
                    modifier = Modifier.size(width = artWidth, height = artHeight),
                    drinkWidthFraction = 0.84f,
                )
                Box(modifier = Modifier.weight(0.53f))
            }

            StartButton(
                onClick = onStart,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = if (compact) 18.dp else 50.dp),
            )
        }
    }
}

/**
 * Der Start-Button. Im Design ist das Button_Start.png (768x137 px); hier als
 * Flaeche nachgebaut, damit die App ohne dieses Asset auskommt.
 */
@Composable
private fun StartButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(256.dp)
            .height(46.dp)
            .clip(RoundedCornerShape(23.dp))
            .background(Lime)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "Los geht's", style = BarBotType.Display.copy(fontSize = 20.sp))
    }
}
