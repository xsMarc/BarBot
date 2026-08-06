package de.barbot.app

import android.app.Application
import android.os.SystemClock
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.barbot.app.bluetooth.BarBotBluetooth
import de.barbot.app.bluetooth.PairedDevice
import de.barbot.app.data.Drink
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Die vier Seiten der App. */
enum class Screen { START, CONNECT, CHOOSE, INFO }

enum class ConnectionState { DISCONNECTED, CONNECTING, CONNECTED }

class BarBotViewModel(application: Application) : AndroidViewModel(application) {

    private val bluetooth = BarBotBluetooth(application)

    var screen by mutableStateOf(Screen.START)
        private set

    var devices by mutableStateOf<List<PairedDevice>>(emptyList())
        private set

    var selectedDevice by mutableStateOf<PairedDevice?>(null)
        private set

    var connectionState by mutableStateOf(ConnectionState.DISCONNECTED)
        private set

    /** Fehlermeldung der Verbindungsseite, null wenn alles in Ordnung ist. */
    var connectionError by mutableStateOf<String?>(null)
        private set

    /** Der Drink, dessen Infoseite gerade offen ist. */
    var selectedDrink by mutableStateOf<Drink?>(null)
        private set

    /** True, sobald der Code fuer [selectedDrink] rausgegangen ist. */
    var orderSent by mutableStateOf(false)
        private set

    /** Der Drink, der die laufende Sperre ausgeloest hat - Text im Sperrbalken. */
    var lockedDrink by mutableStateOf<Drink?>(null)
        private set

    /** Restliche Sperrzeit in Sekunden, 0 = nicht gesperrt. */
    var lockSecondsLeft by mutableStateOf(0)
        private set

    val isLocked: Boolean
        get() = lockSecondsLeft > 0

    /** Steht ein Socket zum BarBot? Steuert den dauerhaften Offline-Hinweis. */
    val isConnected: Boolean
        get() = connectionState == ConnectionState.CONNECTED

    val isBluetoothSupported: Boolean
        get() = bluetooth.isBluetoothSupported

    val isBluetoothEnabled: Boolean
        get() = bluetooth.isBluetoothEnabled

    private var lockJob: Job? = null

    fun hasConnectPermission(): Boolean = bluetooth.hasConnectPermission()

    // ---------------------------------------------------------------- Navigation

    fun goTo(target: Screen) {
        screen = target
    }

    /** Zurueck-Pfeil / Systemzurueck. Gibt false zurueck, wenn die App beendet werden soll. */
    fun goBack(): Boolean = when (screen) {
        Screen.START -> false
        Screen.CONNECT -> { screen = Screen.START; true }
        Screen.CHOOSE -> { screen = Screen.CONNECT; true }
        Screen.INFO -> { screen = Screen.CHOOSE; true }
    }

    // ---------------------------------------------------------------- Bluetooth

    fun refreshDevices() {
        devices = bluetooth.pairedDevices()
        if (devices.none { it.address == selectedDevice?.address }) {
            selectedDevice = null
        }
    }

    fun selectDevice(device: PairedDevice) {
        selectedDevice = device
        connectionError = null
    }

    fun connect() {
        val device = selectedDevice ?: return
        if (connectionState == ConnectionState.CONNECTING) return

        connectionState = ConnectionState.CONNECTING
        connectionError = null

        viewModelScope.launch {
            val result = bluetooth.connect(device.address)
            if (result.isSuccess) {
                connectionState = ConnectionState.CONNECTED
                screen = Screen.CHOOSE
            } else {
                connectionState = ConnectionState.DISCONNECTED
                connectionError = result.exceptionOrNull()?.message
                    ?: "Verbindung zum BarBot fehlgeschlagen"
            }
        }
    }

    /**
     * "Ohne Verbindung fortfahren": weiter zur Auswahl, ohne dass ein Socket steht.
     * Die App bleibt voll bedienbar, das Senden laeuft dann aber ins Leere - darauf
     * weist der Offline-Hinweis auf den Folgeseiten hin.
     */
    fun continueWithoutConnection() {
        connectionError = null
        screen = Screen.CHOOSE
    }

    // ---------------------------------------------------------------- Drinks

    /** Drink aus der Liste antippen: oeffnet die Infoseite, sendet noch nichts. */
    fun openDrink(drink: Drink) {
        if (isLocked) return
        selectedDrink = drink
        orderSent = false
        screen = Screen.INFO
    }

    /**
     * "Jetzt mischen": schickt die Nummer des Drinks an den BarBot und startet
     * die Sperrzeit. Kein Callback, keine Bestaetigung - nur senden und sperren.
     */
    fun orderDrink() {
        val drink = selectedDrink ?: return
        if (isLocked) return

        orderSent = true
        lockedDrink = drink
        startLock()

        viewModelScope.launch {
            bluetooth.sendNumber(drink.code)
        }
    }

    private fun startLock() {
        lockJob?.cancel()
        val endsAt = SystemClock.elapsedRealtime() + LOCK_MILLIS
        lockJob = viewModelScope.launch {
            while (true) {
                val remaining = endsAt - SystemClock.elapsedRealtime()
                if (remaining <= 0) {
                    lockSecondsLeft = 0
                    break
                }
                // Aufrunden, damit die Anzeige bei 2:00 startet und erst bei 0 verschwindet.
                lockSecondsLeft = ((remaining + 999) / 1000).toInt()
                delay(250)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        bluetooth.disconnect()
    }

    companion object {
        /** Sperrzeit nach jeder Bestellung: 2 Minuten. */
        const val LOCK_SECONDS = 2 * 60
        const val LOCK_MILLIS = LOCK_SECONDS * 1000L
    }
}
