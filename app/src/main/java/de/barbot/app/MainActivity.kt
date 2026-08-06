package de.barbot.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import de.barbot.app.ui.screens.ChooseScreen
import de.barbot.app.ui.screens.ConnectScreen
import de.barbot.app.ui.screens.DrinkInfoScreen
import de.barbot.app.ui.screens.StartScreen
import de.barbot.app.ui.theme.BarBotTheme
import de.barbot.app.ui.theme.CardWhite

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            BarBotTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CardWhite,
                ) {
                    BarBotApp(onExit = { finish() })
                }
            }
        }
    }
}

@Composable
fun BarBotApp(onExit: () -> Unit) {
    val viewModel: BarBotViewModel = viewModel()

    BackHandler(enabled = true) {
        if (!viewModel.goBack()) onExit()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
    ) {
        when (viewModel.screen) {
            Screen.START -> StartScreen(
                onStart = { viewModel.goTo(Screen.CONNECT) },
            )

            Screen.CONNECT -> ConnectScreen(
                devices = viewModel.devices,
                selectedDevice = viewModel.selectedDevice,
                connectionState = viewModel.connectionState,
                errorMessage = viewModel.connectionError,
                bluetoothSupported = viewModel.isBluetoothSupported,
                bluetoothEnabled = viewModel.isBluetoothEnabled,
                hasPermission = viewModel::hasConnectPermission,
                onRefresh = viewModel::refreshDevices,
                onSelect = viewModel::selectDevice,
                onConnect = viewModel::connect,
            )

            Screen.CHOOSE -> ChooseScreen(
                lockedDrinkName = viewModel.lockedDrink?.name,
                lockSecondsLeft = viewModel.lockSecondsLeft,
                onDrinkSelected = viewModel::openDrink,
            )

            Screen.INFO -> {
                val drink = viewModel.selectedDrink
                if (drink == null) {
                    // Sollte nicht passieren - zur Sicherheit zurueck zur Auswahl.
                    LaunchedEffect(Unit) { viewModel.goTo(Screen.CHOOSE) }
                } else {
                    DrinkInfoScreen(
                        drink = drink,
                        lockSecondsLeft = viewModel.lockSecondsLeft,
                        ordered = viewModel.orderSent,
                        deviceName = viewModel.selectedDevice?.name,
                        onOrder = viewModel::orderDrink,
                        onBack = { viewModel.goBack() },
                    )
                }
            }
        }
    }
}
