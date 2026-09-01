package de.barbot.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import de.barbot.app.data.DRINKS
import de.barbot.app.data.Drink
import de.barbot.app.ui.CompactHeight
import de.barbot.app.ui.DesignWidth
import de.barbot.app.ui.components.DrinkArt
import de.barbot.app.ui.components.formatSeconds
import de.barbot.app.ui.theme.BarBotType
import de.barbot.app.ui.theme.CardWhite
import de.barbot.app.ui.theme.Field
import de.barbot.app.ui.theme.Ink35
import de.barbot.app.ui.theme.Ink40
import de.barbot.app.ui.theme.Lime

/**
 * Seite 3: Getraenk waehlen.
 *
 * Design: limettengruener Kopf mit abgerundeter Unterkante, darunter die Liste.
 * Waehrend der Sperrzeit sind die Zeilen abgeblendet und zeigen statt des
 * Pfeils die Restzeit; unten laeuft der Sperrbalken.
 */
@Composable
fun ChooseScreen(
    lockSecondsLeft: Int,
    onDrinkSelected: (Drink) -> Unit,
) {
    val locked = lockSecondsLeft > 0
    val navBarBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(CardWhite),
    ) {
        val compact = maxHeight < CompactHeight

        Column(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 19.dp, bottomEnd = 19.dp))
                    .background(Lime)
                    // Gruen zuerst, Inset danach: der Kopf reicht bis unter die
                    // Statusleiste, der Titel bleibt darunter mittig.
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .height(if (compact) 64.dp else 99.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "Getränk wählen", style = BarBotType.Display)
            }

            LazyColumn(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .widthIn(max = DesignWidth)
                    .fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 19.dp,
                    end = 19.dp,
                    top = 15.dp,
                    // Bei laufender Sperre bringt der Sperrbalken darunter den
                    // unteren Inset schon mit.
                    bottom = 20.dp + if (locked) 0.dp else navBarBottom,
                ),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                items(DRINKS, key = { it.code }) { drink ->
                    DrinkRow(
                        drink = drink,
                        locked = locked,
                        lockSecondsLeft = lockSecondsLeft,
                        onClick = { onDrinkSelected(drink) },
                    )
                }
            }
        }
    }
}

@Composable
private fun DrinkRow(
    drink: Drink,
    locked: Boolean,
    lockSecondsLeft: Int,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(13.dp))
            .background(Field)
            .clickable(enabled = !locked, onClick = onClick)
            .alpha(if (locked) 0.55f else 1f)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        Box(
            modifier = Modifier
                .size(63.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(CardWhite),
            contentAlignment = Alignment.Center,
        ) {
            DrinkArt(
                imageRes = drink.image,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp),
                drinkWidthFraction = 0.74f,
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(text = drink.name, style = BarBotType.DrinkName)
            Spacer(Modifier.height(3.dp))
            Text(text = drink.ingredientsShort, style = BarBotType.BodySmall)
            Spacer(Modifier.height(3.dp))
            Text(text = drink.meta, style = BarBotType.MetaSmall)
        }

        if (locked) {
            Text(
                text = "noch ${formatSeconds(lockSecondsLeft)}",
                style = BarBotType.BodySmall.copy(color = Ink40),
                modifier = Modifier.padding(end = 4.dp),
            )
        } else {
            Text(
                text = "→",
                style = BarBotType.Action.copy(color = Ink35),
                modifier = Modifier.padding(end = 4.dp),
            )
        }
    }
}
