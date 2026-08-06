package de.barbot.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import de.barbot.app.R
import de.barbot.app.ui.theme.BarBotType
import de.barbot.app.ui.theme.CardWhite
import de.barbot.app.ui.theme.FieldSoft
import de.barbot.app.ui.theme.Ink
import de.barbot.app.ui.theme.Ink45
import de.barbot.app.ui.theme.Lime
import de.barbot.app.ui.theme.TrackGrey

/**
 * Bildschirmfuellender Hintergrund aus dem Design.
 * Das Design nutzt "center/cover", entspricht [ContentScale.Crop].
 */
@Composable
fun ScreenBackground(
    resId: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(resId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        content()
    }
}

/**
 * Das Drink-Bild vor seinem runden Schein - im Design die Kombination aus
 * Bg_Drink.png (contain) und dem freigestellten Drink darueber.
 *
 * [drinkWidthFraction] entspricht der Breitenangabe im Design (84 % bzw. 74 %).
 */
@Composable
fun DrinkArt(
    modifier: Modifier = Modifier,
    drinkWidthFraction: Float = 0.84f,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(R.drawable.bg_drink),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize(),
        )
        Image(
            painter = painterResource(R.drawable.drink_mojito),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth(drinkWidthFraction)
                .fillMaxHeight(),
        )
    }
}

/** Weisse Zurueck-Kachel oben links (158x158 px, Radius 34 px im Design). */
@Composable
fun BackTile(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(52.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(CardWhite)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_back_arrow),
            contentDescription = "Zurueck",
            modifier = Modifier.height(25.dp),
        )
    }
}

/**
 * Die weisse Aktionsleiste am unteren Rand: links Text, rechts ein Zeichen.
 * Im Design 904x150 px mit Radius 34 px.
 */
@Composable
fun ActionBar(
    text: String,
    trailing: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    background: androidx.compose.ui.graphics.Color = CardWhite,
    textStyle: TextStyle = BarBotType.Action,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(background)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 19.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = text, style = textStyle)
        Text(text = trailing, style = textStyle)
    }
}

/**
 * Der dauerhafte Sperrbalken am unteren Bildschirmrand (210 px hoch im Design):
 * oben ein 16 px hoher Fortschrittsstreifen, darunter Drinkname und Restzeit.
 *
 * Wie im Design fuellt sich der Streifen mit der abgelaufenen Zeit.
 */
@Composable
fun LockBar(
    drinkName: String,
    secondsLeft: Int,
    totalSeconds: Int,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = secondsLeft > 0,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = modifier,
    ) {
        val elapsed = if (totalSeconds > 0) 1f - secondsLeft.toFloat() / totalSeconds else 0f
        val progress by animateFloatAsState(
            targetValue = elapsed.coerceIn(0f, 1f),
            animationSpec = tween(durationMillis = 400),
            label = "lockProgress",
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(FieldSoft),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(TrackGrey),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(5.dp)
                        .background(Lime),
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "$drinkName wird gemischt…",
                    style = BarBotType.Strong.copy(color = Ink),
                )
                Text(
                    text = "noch ${formatSeconds(secondsLeft)}",
                    style = BarBotType.Strong.copy(color = Ink45),
                )
            }
        }
    }
}

/** Sekunden als "m:ss". */
fun formatSeconds(seconds: Int): String {
    val safe = seconds.coerceAtLeast(0)
    return "%d:%02d".format(safe / 60, safe % 60)
}
