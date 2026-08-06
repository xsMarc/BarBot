package de.barbot.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.barbot.app.BarBotViewModel
import de.barbot.app.data.DRINKS
import de.barbot.app.data.Drink
import de.barbot.app.ui.components.BarBotBackdrop
import de.barbot.app.ui.components.BarBotTopBar
import de.barbot.app.ui.components.DrinkGlass
import de.barbot.app.ui.components.LockBar
import de.barbot.app.ui.theme.BarBotOutline
import de.barbot.app.ui.theme.BarBotSurface
import de.barbot.app.ui.theme.BarBotText
import de.barbot.app.ui.theme.BarBotTextMuted

/** Seite 3: Getraenk waehlen. Mit Sperrbalken am unteren Rand. */
@Composable
fun ChooseScreen(
    lockSecondsLeft: Int,
    onDrinkSelected: (Drink) -> Unit,
    onBack: () -> Unit,
) {
    val locked = lockSecondsLeft > 0

    BarBotBackdrop {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                BarBotTopBar(title = "Getraenk waehlen", onBack = onBack)

                Text(
                    text = if (locked) {
                        "Der BarBot mixt gerade - gleich bist du wieder dran."
                    } else {
                        "Tippe auf einen Drink und der BarBot legt los."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = BarBotTextMuted,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )

                Spacer(Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = if (locked) 170.dp else 28.dp,
                    ),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    items(DRINKS, key = { it.code }) { drink ->
                        DrinkCard(
                            drink = drink,
                            enabled = !locked,
                            onClick = { onDrinkSelected(drink) },
                        )
                    }
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
private fun DrinkCard(
    drink: Drink,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BarBotSurface)
            .border(1.dp, BarBotOutline, RoundedCornerShape(20.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .alpha(if (enabled) 1f else 0.4f)
            .padding(bottom = 14.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
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
                modifier = Modifier.size(96.dp),
            )
            if (!drink.alcoholic) {
                Text(
                    text = "ALKOHOLFREI",
                    style = MaterialTheme.typography.labelMedium,
                    color = drink.color,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(drink.color.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                )
            }
        }
        Column(modifier = Modifier.padding(horizontal = 14.dp)) {
            Text(
                text = drink.name,
                style = MaterialTheme.typography.titleMedium,
                color = BarBotText,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = drink.ingredients,
                style = MaterialTheme.typography.bodyMedium,
                color = BarBotTextMuted,
            )
        }
    }
}
