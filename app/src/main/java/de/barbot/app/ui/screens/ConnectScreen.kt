package de.barbot.app.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import de.barbot.app.ConnectionState
import de.barbot.app.bluetooth.PairedDevice
import de.barbot.app.ui.components.BarBotBackdrop
import de.barbot.app.ui.components.BarBotTopBar
import de.barbot.app.ui.components.OutlineButton
import de.barbot.app.ui.components.PrimaryButton
import de.barbot.app.ui.theme.BarBotAmber
import de.barbot.app.ui.theme.BarBotError
import de.barbot.app.ui.theme.BarBotMint
import de.barbot.app.ui.theme.BarBotOutline
import de.barbot.app.ui.theme.BarBotSurface
import de.barbot.app.ui.theme.BarBotSurfaceElevated
import de.barbot.app.ui.theme.BarBotText
import de.barbot.app.ui.theme.BarBotTextMuted

/** Seite 2: Bluetooth-Verbindung zum BarBot herstellen. */
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
    onBack: () -> Unit,
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

    BarBotBackdrop {
        Column(modifier = Modifier.fillMaxSize()) {
            BarBotTopBar(title = "Verbinden", onBack = onBack)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            ) {
                StatusCard(connectionState = connectionState, deviceName = selectedDevice?.name)

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "GEKOPPELTE GERAETE",
                    style = MaterialTheme.typography.labelMedium,
                    color = BarBotTextMuted,
                )
                Spacer(Modifier.height(10.dp))
            }

            val hint = when {
                !bluetoothSupported -> "Dieses Geraet hat kein Bluetooth."
                !permissionGranted -> "Die App braucht die Bluetooth-Berechtigung, um den BarBot zu finden."
                !bluetoothEnabled -> "Bluetooth ist ausgeschaltet. Bitte in den Systemeinstellungen einschalten."
                devices.isEmpty() -> "Keine gekoppelten Geraete gefunden. Koppel den BarBot zuerst in den Bluetooth-Einstellungen deines Handys."
                else -> null
            }

            Box(modifier = Modifier.weight(1f)) {
                if (hint != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                    ) {
                        Text(
                            text = hint,
                            style = MaterialTheme.typography.bodyMedium,
                            color = BarBotTextMuted,
                        )
                        if (!permissionGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            Spacer(Modifier.height(16.dp))
                            OutlineButton(
                                text = "Berechtigung erteilen",
                                onClick = { permissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT) },
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            start = 20.dp,
                            end = 20.dp,
                            bottom = 12.dp,
                        ),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(devices, key = { it.address }) { device ->
                            DeviceRow(
                                device = device,
                                selected = device.address == selectedDevice?.address,
                                onClick = { onSelect(device) },
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = BarBotError,
                    )
                    Spacer(Modifier.height(12.dp))
                }
                OutlineButton(text = "Liste aktualisieren", onClick = onRefresh)
                Spacer(Modifier.height(12.dp))
                PrimaryButton(
                    text = if (connectionState == ConnectionState.CONNECTING) "VERBINDE ..." else "VERBINDEN",
                    onClick = onConnect,
                    enabled = selectedDevice != null && connectionState != ConnectionState.CONNECTING,
                )
            }
        }
    }
}

@Composable
private fun StatusCard(connectionState: ConnectionState, deviceName: String?) {
    val (dotColor, label) = when (connectionState) {
        ConnectionState.CONNECTED -> BarBotMint to "Verbunden"
        ConnectionState.CONNECTING -> BarBotAmber to "Verbinde ..."
        ConnectionState.DISCONNECTED -> BarBotTextMuted to "Nicht verbunden"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(BarBotSurface)
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(dotColor),
        )
        Spacer(Modifier.size(14.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = BarBotText,
            )
            Text(
                text = deviceName ?: "Waehle den BarBot aus der Liste",
                style = MaterialTheme.typography.bodyMedium,
                color = BarBotTextMuted,
            )
        }
    }
}

@Composable
private fun DeviceRow(
    device: PairedDevice,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) BarBotSurfaceElevated else BarBotSurface)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) BarBotAmber else BarBotOutline,
                shape = RoundedCornerShape(16.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = device.name,
                style = MaterialTheme.typography.titleMedium,
                color = BarBotText,
            )
            Text(
                text = device.address,
                style = MaterialTheme.typography.bodyMedium,
                color = BarBotTextMuted,
            )
        }
        if (selected) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(BarBotAmber),
            )
        } else {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .border(2.dp, BarBotOutline, CircleShape)
                    .height(20.dp),
            )
        }
    }
}
