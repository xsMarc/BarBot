package de.barbot.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.barbot.app.R
import de.barbot.app.ui.theme.BarBotAmber
import de.barbot.app.ui.theme.BarBotBackground
import de.barbot.app.ui.theme.BarBotOutline
import de.barbot.app.ui.theme.BarBotSurface
import de.barbot.app.ui.theme.BarBotSurfaceElevated
import de.barbot.app.ui.theme.BarBotText
import de.barbot.app.ui.theme.BarBotTextMuted

/** Dunkler Hintergrund mit warmem Lichtkegel oben - der Look der ganzen App. */
@Composable
fun BarBotBackdrop(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BarBotBackground)
            .background(
                Brush.radialGradient(
                    colors = listOf(BarBotAmber.copy(alpha = 0.16f), Color.Transparent),
                    radius = 900f,
                ),
            ),
    ) {
        content()
    }
}

/** Zurueck-Pfeil oben links, optional mit Titel daneben. */
@Composable
fun BarBotTopBar(
    title: String? = null,
    onBack: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(BarBotSurface)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_back_arrow),
                    contentDescription = "Zurueck",
                    tint = BarBotText,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.width(14.dp))
        }
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = BarBotText,
                modifier = Modifier.weight(1f),
            )
        } else {
            Spacer(Modifier.weight(1f))
        }
        trailing?.invoke()
    }
}

/** Grosse, gut treffbare Hauptaktion. */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val alpha = if (enabled) 1f else 0.35f
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(BarBotAmber.copy(alpha = alpha))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFF231703).copy(alpha = if (enabled) 1f else 0.6f),
        )
    }
}

/** Sekundaere Aktion: nur Umrandung. */
@Composable
fun OutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val alpha = if (enabled) 1f else 0.4f
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(BarBotSurfaceElevated.copy(alpha = alpha))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = BarBotText.copy(alpha = alpha),
        )
    }
}

/**
 * Der dauerhafte Balken am unteren Bildschirmrand: laesst die 2 Minuten
 * Sperrzeit ablaufen. Wird auf der Auswahl- und der Info-Seite gezeigt,
 * solange [secondsLeft] groesser als 0 ist.
 */
@Composable
fun LockBar(
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
        val progress by animateFloatAsState(
            targetValue = if (totalSeconds > 0) secondsLeft.toFloat() / totalSeconds else 0f,
            animationSpec = tween(durationMillis = 300),
            label = "lockProgress",
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, BarBotBackground.copy(alpha = 0.95f)),
                    ),
                )
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 24.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "BarBot ist beschaeftigt",
                    style = MaterialTheme.typography.titleMedium,
                    color = BarBotText,
                )
                Text(
                    text = formatSeconds(secondsLeft),
                    style = MaterialTheme.typography.titleMedium,
                    color = BarBotAmber,
                )
            }
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape)
                    .background(BarBotOutline),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .height(10.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(BarBotAmber, BarBotAmber.copy(alpha = 0.6f)),
                            ),
                        ),
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Solange kann kein weiterer Drink bestellt werden.",
                style = MaterialTheme.typography.bodyMedium,
                color = BarBotTextMuted,
                textAlign = TextAlign.Start,
            )
        }
    }
}

fun formatSeconds(seconds: Int): String {
    val safe = seconds.coerceAtLeast(0)
    return "%d:%02d".format(safe / 60, safe % 60)
}
