package de.barbot.app.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import de.barbot.app.ConnectionState
import de.barbot.app.R
import de.barbot.app.bluetooth.PairedDevice
import de.barbot.app.ui.components.ActionBar
import de.barbot.app.ui.components.ScreenBackground
import de.barbot.app.ui.theme.BadgeGrey
import de.barbot.app.ui.theme.BarBotType
import de.barbot.app.ui.theme.CardWhite
import de.barbot.app.ui.theme.Field
import de.barbot.app.ui.theme.FieldDim
import de.barbot.app.ui.theme.Ink
import de.barbot.app.ui.theme.Ink45
import de.barbot.app.ui.theme.Ink50
import de.barbot.app.ui.theme.Lime

/**
 * Seite 2: Mit dem BarBot verbinden.
 *
 * Design: Titel oben links, darunter eine weisse Karte mit den gefundenen
 * Geraeten, unten die Statuszeile und die Leiste "Erneut suchen".
 */
@Composable
fun ConnectScreen(
    devices: List<PairedDevice>,
    selectedDevice: PairedDevice?,
    connectionState: ConnectionState,
    errorMessage: String?,
    bluetoothSupported: Boolean,
    bluetoothEnabled: Boolean,
    hasPermission: () -> Boolean,
    onRefresh: () -> Unit,
    onSelect: (PairedDevice) -> Unit,
    onConnect: () -> Unit,
    onContinueWithoutConnection: () -> Unit,
) {
    var permissionGranted by remember { mutableStateOf(hasPermission()) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        permissionGranted = granted
        if (granted) onRefresh()
    }

    LaunchedEffect(Unit) {
        if (permissionGranted) {
            onRefresh()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
        }
    }

    ScreenBackground(resId = R.drawable.bg_screen) {
        // Der Verlauf laeuft hinter Status- und Navigationsleiste durch,
        // nur der Inhalt haelt Abstand. Geraeteliste und Aktionsblock liegen
        // untereinander statt uebereinander: so verdeckt im Querformat nichts
        // den jeweils anderen Teil, die Liste bekommt einfach weniger Hoehe.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars),
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 29.dp)
                    .padding(top = 32.dp, bottom = 24.dp),
            ) {
                Text(text = "Mit BarBot\nverbinden", style = BarBotType.Display)

                Spacer(Modifier.height(38.dp))

                DeviceCard(
                    devices = devices,
                    selectedDevice = selectedDevice,
                    connectionState = connectionState,
                    hint = statusHint(
                        bluetoothSupported = bluetoothSupported,
                        permissionGranted = permissionGranted,
                        bluetoothEnabled = bluetoothEnabled,
                        deviceCount = devices.size,
                    ),
                    errorMessage = errorMessage,
                    onSelect = onSelect,
                    onConnect = onConnect,
                    onRequestPermission = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            permissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
                        }
                    },
                    showPermissionButton = !permissionGranted &&
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 29.dp)
                    .padding(bottom = 50.dp),
            ) {
                StatusLine(connectionState = connectionState, bluetoothEnabled = bluetoothEnabled)
                Spacer(Modifier.height(18.dp))
                ActionBar(text = "Erneut suchen", trailing = "↻", onClick = onRefresh)
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Ohne Verbindung fortfahren",
                    style = BarBotType.Status.copy(textDecoration = TextDecoration.Underline),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable(onClick = onContinueWithoutConnection)
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                )
            }
        }
    }
}

private fun statusHint(
    bluetoothSupported: Boolean,
    permissionGranted: Boolean,
    bluetoothEnabled: Boolean,
    deviceCount: Int,
): String? = when {
    !bluetoothSupported -> "Dieses Geraet hat kein Bluetooth."
    !permissionGranted -> "Die App braucht die Bluetooth-Berechtigung, um den BarBot zu finden."
    !bluetoothEnabled -> "Bluetooth ist aus. Bitte in den Systemeinstellungen einschalten."
    deviceCount == 0 -> "Keine gekoppelten Geraete. Koppel den BarBot zuerst in den " +
        "Bluetooth-Einstellungen deines Handys."
    else -> null
}

@Composable
private fun DeviceCard(
    devices: List<PairedDevice>,
    selectedDevice: PairedDevice?,
    connectionState: ConnectionState,
    hint: String?,
    errorMessage: String?,
    onSelect: (PairedDevice) -> Unit,
    onConnect: () -> Unit,
    onRequestPermission: () -> Unit,
    showPermissionButton: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(17.dp))
            .background(CardWhite)
            .padding(horizontal = 19.dp, vertical = 21.dp),
        verticalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        Text(text = "Gefundene Geräte", style = BarBotType.SectionLabel)

        if (hint != null) {
            Text(text = hint, style = BarBotType.BodySmall)
            if (showPermissionButton) {
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Lime)
                        .clickable(onClick = onRequestPermission)
                        .padding(horizontal = 13.dp, vertical = 9.dp),
                ) {
                    Text(text = "Berechtigung erteilen", style = BarBotType.Pill)
                }
            }
        } else {
            devices.forEach { device ->
                DeviceRow(
                    device = device,
                    selected = device.address == selectedDevice?.address,
                    connectionState = connectionState,
                    onSelect = { onSelect(device) },
                    onConnect = onConnect,
                )
            }
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                style = BarBotType.BodySmall.copy(color = Ink50),
            )
        }
    }
}

/**
 * Eine Zeile der Geraeteliste: BT-Kachel, Name mit Status, "Verbinden"-Pille.
 * Ausgewaehlte Zeilen bekommen die limettengruene Kachel wie im Design.
 */
@Composable
private fun DeviceRow(
    device: PairedDevice,
    selected: Boolean,
    connectionState: ConnectionState,
    onSelect: () -> Unit,
    onConnect: () -> Unit,
) {
    val connected = selected && connectionState == ConnectionState.CONNECTED

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(11.dp))
            .background(Field)
            .clickable(onClick = onSelect)
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(if (selected) Lime else BadgeGrey),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "BT",
                style = BarBotType.Badge.copy(color = if (selected) Ink else Ink45),
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(text = device.name, style = BarBotType.Strong)
            Spacer(Modifier.height(2.dp))
            Text(
                text = when {
                    connected -> "Verbunden"
                    selected && connectionState == ConnectionState.CONNECTING -> "Verbinde …"
                    else -> "Gekoppelt"
                },
                style = BarBotType.BodySmall.copy(color = Ink50),
            )
        }

        Box(
            modifier = Modifier
                .height(31.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (selected) Lime else FieldDim)
                .clickable(enabled = connectionState != ConnectionState.CONNECTING) {
                    onSelect()
                    onConnect()
                }
                .padding(horizontal = 13.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (connected) "Verbunden" else "Verbinden",
                style = BarBotType.Pill,
            )
        }
    }
}

/** Punkt plus Text, im Design 1360 px von oben. */
@Composable
private fun StatusLine(connectionState: ConnectionState, bluetoothEnabled: Boolean) {
    val text = when {
        connectionState == ConnectionState.CONNECTED -> "Verbunden · bereit zum Mixen"
        connectionState == ConnectionState.CONNECTING -> "Verbindung wird aufgebaut …"
        !bluetoothEnabled -> "Bluetooth aus"
        else -> "Bluetooth aktiv · Suche läuft"
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(Ink)
                .alpha(if (bluetoothEnabled) 1f else 0.4f),
        )
        Spacer(Modifier.width(5.dp))
        Text(text = text, style = BarBotType.Status)
    }
}
