package de.barbot.app.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID

/** Ein gekoppeltes Bluetooth-Geraet, reduziert auf das, was die UI braucht. */
data class PairedDevice(val name: String, val address: String)

/**
 * Ganz simpler Bluetooth-Classic-Client (SPP / RFCOMM).
 *
 * Bewusst ohne Callback-Protokoll: die App oeffnet eine Verbindung zum BarBot
 * und schickt beim Drink-Wunsch genau eine Zahl. Es wird keine Antwort gelesen
 * und nicht auf eine Bestaetigung gewartet.
 */
class BarBotBluetooth(private val context: Context) {

    private val adapter: BluetoothAdapter?
        get() = (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter

    private var socket: BluetoothSocket? = null

    /** Adresse des aktuell verbundenen Geraets, oder null. */
    @Volatile
    var connectedAddress: String? = null
        private set

    val isBluetoothSupported: Boolean
        get() = adapter != null

    val isBluetoothEnabled: Boolean
        get() = adapter?.isEnabled == true

    /** Ab Android 12 braucht schon das Auflisten gekoppelter Geraete BLUETOOTH_CONNECT. */
    fun hasConnectPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_CONNECT,
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    fun pairedDevices(): List<PairedDevice> {
        if (!hasConnectPermission()) return emptyList()
        return try {
            adapter?.bondedDevices.orEmpty().map { device: BluetoothDevice ->
                PairedDevice(
                    name = device.name ?: "Unbekanntes Geraet",
                    address = device.address,
                )
            }.sortedBy { it.name.lowercase() }
        } catch (e: SecurityException) {
            Log.w(TAG, "Keine Berechtigung fuer bondedDevices", e)
            emptyList()
        }
    }

    /**
     * Verbindet mit dem gekoppelten Geraet [address].
     * Laeuft auf dem IO-Dispatcher und blockiert bis die Verbindung steht oder scheitert.
     */
    @SuppressLint("MissingPermission")
    suspend fun connect(address: String): Result<Unit> = withContext(Dispatchers.IO) {
        val adapter = adapter
            ?: return@withContext Result.failure(IOException("Bluetooth wird nicht unterstuetzt"))
        if (!adapter.isEnabled) {
            return@withContext Result.failure(IOException("Bluetooth ist ausgeschaltet"))
        }
        if (!hasConnectPermission()) {
            return@withContext Result.failure(IOException("Bluetooth-Berechtigung fehlt"))
        }

        disconnect()

        try {
            val device = adapter.getRemoteDevice(address)
            // Discovery bremst den Verbindungsaufbau aus; sicherheitshalber stoppen.
            runCatching { adapter.cancelDiscovery() }

            val newSocket = device.createRfcommSocketToServiceRecord(SPP_UUID)
            newSocket.connect()
            socket = newSocket
            connectedAddress = address
            Result.success(Unit)
        } catch (e: Exception) {
            disconnect()
            Log.w(TAG, "Verbindung fehlgeschlagen", e)
            Result.failure(e)
        }
    }

    /**
     * Schickt die Drink-Nummer an den BarBot: die Zahl als ASCII-Text mit "\n" am Ende,
     * z. B. "3\n". Fire and forget - es wird nichts zurueckgelesen.
     */
    suspend fun sendNumber(number: Int): Result<Unit> = withContext(Dispatchers.IO) {
        val stream = socket?.takeIf { it.isConnected }?.outputStream
            ?: return@withContext Result.failure(IOException("Nicht verbunden"))
        try {
            stream.write("$number\n".toByteArray(Charsets.US_ASCII))
            stream.flush()
            Result.success(Unit)
        } catch (e: IOException) {
            Log.w(TAG, "Senden fehlgeschlagen", e)
            disconnect()
            Result.failure(e)
        }
    }

    fun disconnect() {
        runCatching { socket?.close() }
        socket = null
        connectedAddress = null
    }

    private companion object {
        const val TAG = "BarBotBluetooth"

        /** Standard Serial Port Profile - das nutzen HC-05/HC-06 und ESP32 BluetoothSerial. */
        val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }
}
