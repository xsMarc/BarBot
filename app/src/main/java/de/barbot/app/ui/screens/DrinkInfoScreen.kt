package de.barbot.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import de.barbot.app.BarBotViewModel
import de.barbot.app.R
import de.barbot.app.data.Drink
import de.barbot.app.ui.components.ActionBar
import de.barbot.app.ui.components.BackTile
import de.barbot.app.ui.components.DrinkArt
import de.barbot.app.ui.components.LockBar
import de.barbot.app.ui.components.ScreenBackground
import de.barbot.app.ui.components.formatSeconds
import de.barbot.app.ui.theme.BarBotType
import de.barbot.app.ui.theme.CardWhite
import de.barbot.app.ui.theme.DividerGrey
import de.barbot.app.ui.theme.Ink40
import de.barbot.app.ui.theme.Ink55

/**
 * Seite 4: Infoseite zum gewaehlten Drink.
 *
 * Zwei Zustaende wie im Design: vor dem Absenden mit der Leiste "Jetzt mischen",
 * danach mit "Auftrag gesendet", gesperrter Leiste und Sperrbalken unten.
 */
@Composable
fun DrinkInfoScreen(
    drink: Drink,
    lockSecondsLeft: Int,
    ordered: Boolean,
    connected: Boolean,
    deviceName: String?,
    onOrder: () -> Unit,
    onBack: () -> Unit,
) {
    val locked = lockSecondsLeft > 0

    ScreenBackground(resId = R.drawable.bg_screen) {
        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = if (locked) 140.dp else 100.dp),
            ) {
                BackTile(
                    onClick = onBack,
                    modifier = Modifier.padding(start = 19.dp, top = 27.dp),
                )

                DrinkArt(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(width = 307.dp, height = 280.dp),
                    drinkWidthFraction = 0.84f,
                )

                Spacer(Modifier.height(14.dp))

                Column(modifier = Modifier.padding(horizontal = 29.dp)) {
                    Text(text = drink.name, style = BarBotType.Hero)
                    Spacer(Modifier.height(5.dp))
                    Text(
                        text = "${drink.meta} · Code ${drink.label}",
                        style = BarBotType.Meta,
                    )

                    Spacer(Modifier.height(18.dp))

                    if (ordered) {
                        SentCard(drink = drink, deviceName = deviceName, connected = connected)
                    } else {
                        IngredientsCard(drink = drink)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 29.dp)
                    .padding(bottom = if (locked) 84.dp else 50.dp),
            ) {
                if (locked) {
                    ActionBar(
                        text = "Gesperrt",
                        trailing = formatSeconds(lockSecondsLeft),
                        onClick = {},
                        enabled = false,
                        background = CardWhite.copy(alpha = 0.55f),
                        textStyle = BarBotType.Action.copy(color = Ink40),
                    )
                } else {
                    ActionBar(text = "Jetzt mischen", trailing = "→", onClick = onOrder)
                }
            }

            LockBar(
                drinkName = drink.name,
                secondsLeft = lockSecondsLeft,
                totalSeconds = BarBotViewModel.LOCK_SECONDS,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
private fun IngredientsCard(drink: Drink) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(17.dp))
            .background(CardWhite)
            .padding(horizontal = 16.dp, vertical = 15.dp),
    ) {
        Text(text = "Zutaten", style = BarBotType.SectionLabel)
        Spacer(Modifier.height(4.dp))
        Text(text = drink.ingredients, style = BarBotType.Body)
        Spacer(Modifier.height(7.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DividerGrey),
        )
        Spacer(Modifier.height(7.dp))
        Text(
            text = "Glas unter den Auslauf stellen · Mischzeit ca. 40 s · danach 2 min gesperrt",
            style = BarBotType.BodySmall.copy(color = Ink55),
        )
    }
}

@Composable
private fun SentCard(drink: Drink, deviceName: String?, connected: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(17.dp))
            .background(CardWhite)
            .padding(16.dp),
    ) {
        Text(
            text = if (connected) "Auftrag gesendet" else "Nicht übertragen",
            style = BarBotType.SectionLabel,
        )
        Spacer(Modifier.height(7.dp))
        Text(
            text = if (connected) {
                "Code ${drink.label} wurde an ${deviceName ?: "den BarBot"} übertragen. " +
                    "Stelle ein Glas unter den Auslauf."
            } else {
                "Ohne Verbindung zum BarBot ging Code ${drink.label} nicht raus. " +
                    "Die Sperrzeit läuft trotzdem."
            },
            style = BarBotType.Body.copy(fontSize = BarBotType.Body.fontSize),
        )
    }
}
