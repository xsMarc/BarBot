package de.barbot.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.barbot.app.data.GlassShape
import de.barbot.app.ui.components.BarBotBackdrop
import de.barbot.app.ui.components.DrinkGlass
import de.barbot.app.ui.components.PrimaryButton
import de.barbot.app.ui.theme.BarBotAmber
import de.barbot.app.ui.theme.BarBotMint
import de.barbot.app.ui.theme.BarBotSurface
import de.barbot.app.ui.theme.BarBotSurfaceElevated
import de.barbot.app.ui.theme.BarBotText
import de.barbot.app.ui.theme.BarBotTextMuted

/** Seite 1: Startseite beim Oeffnen der App. */
@Composable
fun StartScreen(onStart: () -> Unit) {
    BarBotBackdrop {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
                .padding(top = 72.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "BARBOT",
                style = MaterialTheme.typography.displaySmall,
                color = BarBotText,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Dein Roboter an der Bar",
                style = MaterialTheme.typography.bodyLarge,
                color = BarBotTextMuted,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.weight(1f))

            RobotMark(modifier = Modifier.size(220.dp))

            Spacer(Modifier.weight(1f))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(BarBotSurface)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "So funktioniert es",
                    style = MaterialTheme.typography.titleMedium,
                    color = BarBotText,
                )
                Step(number = "1", text = "Per Bluetooth mit dem BarBot verbinden")
                Step(number = "2", text = "Drink auswaehlen")
                Step(number = "3", text = "2 Minuten warten, bis der naechste dran ist")
            }

            Spacer(Modifier.height(24.dp))

            PrimaryButton(text = "LOS GEHT'S", onClick = onStart)
        }
    }
}

@Composable
private fun Step(number: String, text: String) {
    androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(BarBotSurfaceElevated),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = number,
                style = MaterialTheme.typography.labelMedium,
                color = BarBotAmber,
            )
        }
        Spacer(Modifier.size(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = BarBotTextMuted,
        )
    }
}

/** Gezeichnetes Roboter-Logo: Kopf mit Antenne, daneben ein Glas. */
@Composable
private fun RobotMark(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Leuchtender Sockel
            drawCircle(
                color = BarBotAmber.copy(alpha = 0.10f),
                radius = w * 0.46f,
                center = Offset(w / 2f, h / 2f),
            )

            // Antenne
            drawLine(
                color = BarBotTextMuted,
                start = Offset(w * 0.42f, h * 0.20f),
                end = Offset(w * 0.42f, h * 0.10f),
                strokeWidth = w * 0.018f,
            )
            drawCircle(
                color = BarBotMint,
                radius = w * 0.035f,
                center = Offset(w * 0.42f, h * 0.09f),
            )

            // Kopf
            val headW = w * 0.46f
            val headH = h * 0.36f
            val headLeft = w * 0.19f
            val headTop = h * 0.20f
            drawRoundRect(
                color = BarBotSurfaceElevated,
                topLeft = Offset(headLeft, headTop),
                size = Size(headW, headH),
                cornerRadius = CornerRadius(w * 0.09f, w * 0.09f),
            )

            // Augen
            val eyeY = headTop + headH * 0.42f
            drawCircle(BarBotAmber, radius = w * 0.038f, center = Offset(headLeft + headW * 0.31f, eyeY))
            drawCircle(BarBotAmber, radius = w * 0.038f, center = Offset(headLeft + headW * 0.69f, eyeY))

            // Mund
            drawRoundRect(
                color = BarBotTextMuted.copy(alpha = 0.6f),
                topLeft = Offset(headLeft + headW * 0.30f, headTop + headH * 0.68f),
                size = Size(headW * 0.40f, headH * 0.08f),
                cornerRadius = CornerRadius(w * 0.02f, w * 0.02f),
            )

            // Koerper / Arm zum Glas
            drawRoundRect(
                color = BarBotSurface,
                topLeft = Offset(headLeft + headW * 0.10f, headTop + headH + h * 0.03f),
                size = Size(headW * 0.80f, h * 0.30f),
                cornerRadius = CornerRadius(w * 0.06f, w * 0.06f),
            )
            drawLine(
                color = BarBotSurfaceElevated,
                start = Offset(headLeft + headW * 0.88f, headTop + headH + h * 0.10f),
                end = Offset(w * 0.76f, h * 0.62f),
                strokeWidth = w * 0.035f,
                cap = androidx.compose.ui.graphics.StrokeCap.Round,
            )

            // Dezenter Schatten unter dem Roboter
            drawOval(
                color = Color.Black.copy(alpha = 0.35f),
                topLeft = Offset(w * 0.22f, h * 0.90f),
                size = Size(w * 0.40f, h * 0.05f),
            )
        }

        // Das Glas in der Roboterhand
        DrinkGlass(
            shape = GlassShape.COCKTAIL,
            color = BarBotMint,
            modifier = Modifier
                .size(74.dp)
                .align(Alignment.CenterEnd),
        )
    }
}
