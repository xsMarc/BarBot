# BarBot App

Einfache Android-App (Kotlin + Jetpack Compose) fuer den BarBot-Mixroboter.
Die App verbindet sich per Bluetooth mit dem Roboter und schickt beim Antippen
eines Drinks genau eine Zahl. Danach ist die Bestellung 2 Minuten gesperrt.

## Aufbau

Vier Seiten, umgeschaltet ueber `Screen` im `BarBotViewModel`:

| Seite | Datei | Inhalt |
|---|---|---|
| 1 Start | `ui/screens/StartScreen.kt` | Logo, Kurzanleitung, Button "Los geht's" |
| 2 Verbinden | `ui/screens/ConnectScreen.kt` | Liste der gekoppelten Bluetooth-Geraete, Verbinden |
| 3 Getraenk waehlen | `ui/screens/ChooseScreen.kt` | Raster mit allen Drinks, Sperrbalken unten |
| 4 Info | `ui/screens/DrinkInfoScreen.kt` | Bestaetigung des gewaehlten Drinks, Sperrbalken unten |

Der Sperrbalken (`ui/components/Common.kt`, `LockBar`) sitzt fest am unteren
Bildschirmrand auf Seite 3 und 4 und wird nur eingeblendet, wenn in den letzten
2 Minuten ein Drink bestellt wurde.

## Logik

Bewusst minimal gehalten:

* **Kein Callback.** `BarBotBluetooth.sendNumber()` schreibt die Drink-Nummer als
  ASCII-Text mit Zeilenumbruch (`"3\n"`) in den RFCOMM-Stream und liest nichts zurueck.
* **Fester Timeout.** Nach jeder Bestellung laufen 2 Minuten Sperrzeit
  (`BarBotViewModel.LOCK_MILLIS`), in denen kein weiterer Drink angefragt werden kann.
  Die Restzeit haengt an `SystemClock.elapsedRealtime()`, ueberlebt also
  Bildschirmdrehungen.
* **Bluetooth Classic (SPP).** UUID `00001101-0000-1000-8000-00805F9B34FB` -
  passt zu HC-05/HC-06-Modulen und zu `BluetoothSerial` auf dem ESP32.
  Es wird nur zu bereits gekoppelten Geraeten verbunden, deshalb braucht die App
  keine Scan- und keine Standortberechtigung.

## Drinks anpassen

Alle Drinks sind hart codiert in `app/src/main/java/de/barbot/app/data/Drinks.kt`.
`code` ist die Zahl, die an den Roboter geht:

| Code | Drink | Code | Drink |
|---|---|---|---|
| 1 | Mojito | 5 | Sex on the Beach |
| 2 | Cuba Libre | 6 | Wodka Lemon |
| 3 | Gin Tonic | 7 | Caipirinha |
| 4 | Tequila Sunrise | 8 | Ipanema (alkoholfrei) |

Einen Drink aendern oder hinzufuegen heisst: einen Eintrag in `DRINKS` anpassen.
Sonst ist nichts zu tun - die Auswahlseite baut sich aus dieser Liste auf.

Die Sperrzeit steht in `BarBotViewModel.LOCK_MILLIS`.

## Bauen

```bash
./gradlew assembleDebug     # app/build/outputs/apk/debug/app-debug.apk
./gradlew assembleRelease   # app/build/outputs/apk/release/app-release.apk
```

Der Release-Build ist der Einfachheit halber mit dem Debug-Key signiert, damit die
APK direkt installierbar ist. Fuer eine echte Veroeffentlichung in
`app/build.gradle.kts` einen eigenen Keystore eintragen.

Ohne lokales Android SDK: der Workflow `.github/workflows/build-apk.yml` baut beide
APKs bei jedem Push und haengt sie als Artefakt `barbot-apk` an den Lauf.

## Auf dem Handy

1. BarBot einmalig in den Bluetooth-Einstellungen des Handys koppeln.
2. App oeffnen, "Los geht's", BarBot in der Liste antippen, "Verbinden".
3. Drink waehlen - der BarBot bekommt die Nummer, die App sperrt 2 Minuten.

Mindestens Android 7.0 (API 24). Ab Android 12 fragt die App beim Oeffnen der
Verbindungsseite nach der Berechtigung "Geraete in der Naehe".
