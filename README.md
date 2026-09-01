# BarBot App

Android-App (Kotlin + Jetpack Compose) fuer den BarBot-Mixroboter. Die App
verbindet sich per Bluetooth mit dem Roboter und schickt beim Bestellen eines
Drinks genau eine Zahl. Danach ist die Bestellung 2 Minuten gesperrt.

## Aufbau

Vier Seiten, umgeschaltet ueber `Screen` im `BarBotViewModel`:

| Seite | Datei | Inhalt |
|---|---|---|
| 1 Start | `ui/screens/StartScreen.kt` | Logo, Drink-Hero, Start-Button |
| 2 Verbinden | `ui/screens/ConnectScreen.kt` | Weisse Karte mit gekoppelten Geraeten, Statuszeile, "Erneut suchen" |
| 3 Getraenk waehlen | `ui/screens/ChooseScreen.kt` | Limettengruener Kopf, Drink-Liste |
| 4 Info | `ui/screens/DrinkInfoScreen.kt` | Drink gross, Zutaten, "Jetzt mischen" |

Der Sperrbalken (`ui/components/Common.kt`, `LockBar`) sitzt am unteren Rand von
Seite 3 und 4 und erscheint nur, solange die Sperre laeuft. Wie im Design
*fuellt* sich der Streifen mit der abgelaufenen Zeit.

## Ablauf

Aus dem Design uebernommen: die Infoseite ist die Bestaetigung **vor** dem
Senden.

1. Drink in der Liste antippen -> Infoseite mit Zutaten, es geht noch nichts raus
2. "Jetzt mischen" -> Code wird gesendet, Sperre startet
3. Infoseite zeigt "Auftrag gesendet", die Leiste steht auf "Gesperrt", unten
   laeuft der Balken; die Liste ist abgeblendet und zeigt die Restzeit

## Logik

Bewusst minimal gehalten:

* **Kein Callback.** `BarBotBluetooth.sendNumber()` schreibt die Drink-Nummer als
  ASCII-Text mit Zeilenumbruch (`"3\n"`) in den RFCOMM-Stream und liest nichts zurueck.
* **Fester Timeout.** Nach jeder Bestellung laufen 2 Minuten Sperrzeit
  (`BarBotViewModel.LOCK_SECONDS`). Die Restzeit haengt an
  `SystemClock.elapsedRealtime()`, ueberlebt also Bildschirmdrehungen.
* **Bluetooth Classic (SPP).** UUID `00001101-0000-1000-8000-00805F9B34FB` -
  passt zu HC-05/HC-06-Modulen und zu `BluetoothSerial` auf dem ESP32.
  Es wird nur zu bereits gekoppelten Geraeten verbunden, deshalb braucht die App
  keine Scan- und keine Standortberechtigung.

## Drinks anpassen

`app/src/main/java/de/barbot/app/data/Drinks.kt` ist die zentrale Konfiguration:
Bestueckung der Pumpen, alle Getraenke mit Mischverhaeltnis, Bild und der Nummer,
die per Bluetooth rausgeht. Die Oberflaeche baut sich vollstaendig aus `DRINKS`
auf - ein Getraenk aendern oder hinzufuegen heisst, dort einen Eintrag anzupassen.

Bestueckung: Vodka und Gin an den Spirituosenpumpen, Tonic Water, Ginger Ale,
Tropical Juice und Mate als Filler. Rohrzucker, Eis, Zitronen-/Limettensaft,
Grenadine und Deko kommen von Hand dazu.

| Code | Cocktail | Verhaeltnis |
|---|---|---|
| 1 | Tropical Screwdriver | 6 cl Vodka + 12 cl Tropical Juice |
| 2 | Vodka Mate | 6 cl Vodka + 12 cl Mate |
| 3 | Gin Tonic | 6 cl Gin + 12 cl Tonic Water |
| 4 | Moscow Mule | 6 cl Vodka + 12 cl Ginger Ale |
| 5 | Gin Ginger Mate | 3 cl Gin + 6 cl Ginger Ale + 6 cl Mate |
| 6 | Gin Buck | 6 cl Gin + 12 cl Ginger Ale |
| 7 | Tropical Mule | 6 cl Vodka + 6 cl Ginger Ale + 6 cl Tropical Juice |
| 8 | Tropical Tonic Vodka | 6 cl Vodka + 6 cl Tonic Water + 6 cl Tropical Juice |
| 9 | Vodka Tonic | 6 cl Vodka + 12 cl Tonic Water |
| 10 | Tropical Gin Tonic | 6 cl Gin + 6 cl Tropical Juice + 6 cl Tonic Water |

| Code | Mocktail | Verhaeltnis |
|---|---|---|
| 11 | Ginger Mate | 9 cl Ginger Ale + 9 cl Mate |
| 12 | Tropical Tonic | 9 cl Tropical Juice + 9 cl Tonic Water |
| 13 | Tropical Ginger | 9 cl Tropical Juice + 9 cl Ginger Ale |
| 14 | Tropical Spark | 6 cl Tropical Juice + 6 cl Ginger Ale + 6 cl Tonic Water |

Die Prozentangaben in der App sind aus den Verhaeltnissen gerechnet und gehen von
37,5 % vol fuer Vodka und Gin aus (`SPIRIT_ABV_PERCENT`).

Die Sperrzeit steht in `BarBotViewModel.LOCK_SECONDS`.

## Bilder

In `app/src/main/res/drawable-nodpi/` liegen **Platzhalter**. Die echten Assets
aus dem Design-Projekt lassen sich nicht ueber das MCP laden (dessen Limit von
256 KiB schneidet sie mittendrin ab), deshalb sind sie hier nachgezeichnet.
Zum Austauschen einfach die Datei mit gleichem Namen ueberschreiben - im Code
aendert sich nichts:

| Datei im Projekt | Original im Design |
|---|---|
| `bg_fruits.png` | `uploads/Background_Fruits.png` |
| `bg_screen.png` | `uploads/BG.png` |
| `bg_drink.png` | `uploads/Bg_Drink.png` |
| `drink_mojito.png` | `uploads/Drink_Mojito_cut.png` |
| `logo_glass.png` | `uploads/Logo_GlassBG.png` |
| `ic_back_arrow.png` | `uploads/BackArrow.png` (bereits das Original) |

`uploads/Button_Start.png` wird nicht gebraucht - der Start-Button ist als
Flaeche nachgebaut (`StartScreen.kt`).

Jedes Getraenk hat in `Drinks.kt` ein eigenes Feld `image`. Ein eigenes Bild
einhaengen heisst: freigestelltes PNG nach `res/drawable-nodpi/` legen und beim
Getraenk eintragen. Solange keins da ist, steht dort `drink_mojito.png` - alle
Getraenke zeigen deshalb aktuell dasselbe Bild.

## Bauen

```bash
export JAVA_HOME=/pfad/zu/jdk17     # AGP 8.7 laeuft nicht auf JDK 24
./gradlew assembleDebug             # app/build/outputs/apk/debug/app-debug.apk
./gradlew assembleRelease           # app/build/outputs/apk/release/app-release.apk
```

Der Release-Build ist der Einfachheit halber mit dem Debug-Key signiert, damit die
APK direkt installierbar ist. Fuer eine echte Veroeffentlichung in
`app/build.gradle.kts` einen eigenen Keystore eintragen.

**Liegt das Projekt auf einem exFAT-/NTFS-Volume** (z. B. einer externen Platte),
legt macOS neben jedem Verzeichnis eine AppleDouble-Datei `._name` an; der
Ressourcen-Parser von AGP haelt die faelschlich fuer ein Verzeichnis und bricht
ab. Dann das Build-Verzeichnis auf eine APFS-Platte umlenken:

```bash
./gradlew -PbarbotBuildDir="$HOME/Library/Caches/BarBotBuild" assembleDebug
```

Ohne die Property bleibt alles beim Gradle-Standard.

Der Workflow `.github/workflows/build-apk.yml` baut beide APKs bei jedem Push und
haengt sie als Artefakt `barbot-apk` an den Lauf.

## Auf dem Handy

1. BarBot einmalig in den Bluetooth-Einstellungen des Handys koppeln.
2. App oeffnen, "Los geht's", BarBot in der Liste antippen, "Verbinden".
3. Drink waehlen, "Jetzt mischen" - der BarBot bekommt die Nummer, die App
   sperrt 2 Minuten.

Mindestens Android 7.0 (API 24). Ab Android 12 fragt die App beim Oeffnen der
Verbindungsseite nach der Berechtigung "Geraete in der Naehe".
