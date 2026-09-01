package de.barbot.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import de.barbot.app.ui.ScaledDesign
import de.barbot.app.ui.components.LockBar
import de.barbot.app.ui.components.OfflineNotice
import de.barbot.app.ui.rememberDesignScale
import de.barbot.app.ui.screens.ChooseScreen
import de.barbot.app.ui.screens.ConnectScreen
import de.barbot.app.ui.screens.DrinkInfoScreen
import de.barbot.app.ui.screens.StartScreen
import de.barbot.app.ui.theme.BarBotTheme
import de.barbot.app.ui.theme.CardWhite
import de.barbot.app.ui.theme.Lime

/**
 * Rueckfall-Abdunklung der Navigationsleiste fuer Android 7 und 8.0 - dort kann
 * das System die Symbole der Navigationsleiste noch nicht dunkel faerben.
 */
private val NavBarFallbackScrim = android.graphics.Color.argb(0x80, 0x1B, 0x1B, 0x1B)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Randlos: die Seiten zeichnen selbst hinter Status- und Navigationsleiste.
        // Beide Leisten bleiben transparent; die Symbole (Uhr, Akku) werden dunkel
        // gestellt, weil das Design durchgehend hell ist - sonst waeren sie auf dem
        // hellen Hintergrund unsichtbar.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                NavBarFallbackScrim,
            ),
        )
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

    // Auf einem Telefon kommt hier 1 heraus - dort bleibt alles wie gezeichnet.
    // Auf einem Tablet waechst der Entwurf proportional mit, dp und sp gemeinsam.
    ScaledDesign(scale = rememberDesignScale()) {
        Column(modifier = Modifier.fillMaxSize()) {
            val afterConnectScreen =
                viewModel.screen == Screen.CHOOSE || viewModel.screen == Screen.INFO
            val showOffline = afterConnectScreen && !viewModel.isConnected

            // Dauerhafter Hinweis auf allen Seiten hinter dem Bluetooth-Screen,
            // solange keine Verbindung steht.
            if (showOffline) {
                // Der schwarze Hinweis schiebt sich unter die Statusleiste. Der Streifen
                // darueber bekommt das Limettengruen der Seiten, damit Uhr und Akku
                // lesbar bleiben - auf Schwarz waeren die dunklen Symbole unsichtbar.
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsTopHeight(WindowInsets.statusBars)
                        .background(Lime),
                )
                OfflineNotice()
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    // Den oberen Inset haben Streifen und Hinweis schon aufgebraucht,
                    // sonst wuerden die Seiten ihn ein zweites Mal einrechnen.
                    .then(
                        if (showOffline) {
                            Modifier.consumeWindowInsets(WindowInsets.statusBars)
                        } else {
                            Modifier
                        },
                    ),
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
                        onContinueWithoutConnection = viewModel::continueWithoutConnection,
                    )

                    Screen.CHOOSE -> ChooseScreen(
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
                                connected = viewModel.isConnected,
                                deviceName = viewModel.selectedDevice?.name,
                                onOrder = viewModel::orderDrink,
                                onBack = { viewModel.goBack() },
                            )
                        }
                    }
                }
            }

            // Der Sperrbalken liegt bewusst hier und nicht in den Seiten: so reicht
            // er auf jeder Breite bis an beide Raender und schiebt den Seiteninhalt
            // nach oben, statt ihn zu ueberdecken.
            if (afterConnectScreen) {
                LockBar(
                    drinkName = viewModel.lockedDrink?.name.orEmpty(),
                    secondsLeft = viewModel.lockSecondsLeft,
                    totalSeconds = BarBotViewModel.LOCK_SECONDS,
                )
            }
        }
    }
}
