package de.barbot.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.barbot.app.BarBotViewModel
import de.barbot.app.data.Drink
import de.barbot.app.ui.components.BarBotBackdrop
import de.barbot.app.ui.components.BarBotTopBar
import de.barbot.app.ui.components.DrinkGlass
import de.barbot.app.ui.components.LockBar
import de.barbot.app.ui.components.OutlineButton
import de.barbot.app.ui.theme.BarBotMint
import de.barbot.app.ui.theme.BarBotSurface
import de.barbot.app.ui.theme.BarBotText
import de.barbot.app.ui.theme.BarBotTextMuted

/** Seite 4: Bestaetigung nach der Drink-Auswahl. Ebenfalls mit Sperrbalken. */
@Composable
fun DrinkInfoScreen(
    drink: Drink,
    lockSecondsLeft: Int,
    onBack: () -> Unit,
) {
    BarBotBackdrop {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                BarBotTopBar(title = null, onBack = onBack)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 28.dp)
                        .padding(bottom = if (lockSecondsLeft > 0) 170.dp else 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "BESTELLUNG GESENDET",
                        style = MaterialTheme.typography.labelMedium,
                        color = BarBotMint,
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = drink.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = BarBotText,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = drink.ingredients,
                        style = MaterialTheme.typography.bodyMedium,
                        color = BarBotTextMuted,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(18.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(drink.color.copy(alpha = 0.22f), Color.Transparent),
                                ),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        DrinkGlass(
                            shape = drink.glass,
                            color = drink.color,
                            modifier = Modifier.size(190.dp),
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    InfoRow(label = "Gesendeter Code", value = drink.code.toString())
                    Spacer(Modifier.height(10.dp))
                    InfoRow(
                        label = "Status",
                        value = if (lockSecondsLeft > 0) "BarBot mixt" else "Fertig",
                    )

                    Spacer(Modifier.height(20.dp))

                    Text(
                        text = "Stell dein Glas unter den Auslauf. Nach Ablauf der Zeit kannst du " +
                            "den naechsten Drink bestellen.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BarBotTextMuted,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(20.dp))

                    OutlineButton(text = "Zurueck zur Auswahl", onClick = onBack)
                }
            }

            LockBar(
                secondsLeft = lockSecondsLeft,
                totalSeconds = BarBotViewModel.LOCK_SECONDS,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BarBotSurface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = BarBotTextMuted,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = BarBotText,
        )
    }
}
