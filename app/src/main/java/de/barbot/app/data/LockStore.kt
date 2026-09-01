package de.barbot.app.data

import android.content.Context
import android.os.SystemClock

/**
 * Haelt die laufende Sperrzeit ausserhalb des Prozesses fest, damit sie ein
 * Schliessen und Neustarten der App ueberlebt.
 *
 * Gespeichert wird das Ende der Sperre doppelt: einmal auf der Wanduhr und
 * einmal auf der Laufzeit seit dem Systemstart. Beide fuer sich haben eine
 * Luecke - die Wanduhr laesst sich in den Einstellungen vorstellen, die
 * Laufzeit faengt nach einem Neustart des Geraets wieder bei null an. Zusammen
 * decken sie den jeweils anderen Fall ab.
 */
class LockStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Schreibt das Ende der Sperre. Bewusst mit `commit()` statt `apply()`:
     * der Fall, den wir abfangen wollen, ist genau das sofortige Wegwischen der
     * App - ein asynchroner Schreibvorgang koennte dabei verloren gehen.
     */
    fun save(drinkCode: Int, lockMillis: Long) {
        prefs.edit()
            .putInt(KEY_DRINK_CODE, drinkCode)
            .putLong(KEY_WALL_CLOCK_END, System.currentTimeMillis() + lockMillis)
            .putLong(KEY_UPTIME_END, SystemClock.elapsedRealtime() + lockMillis)
            .commit()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    /**
     * Restliche Sperrzeit in Millisekunden, 0 wenn keine mehr laeuft.
     *
     * [lockMillis] ist die volle Sperrdauer und dient als Obergrenze: was auch
     * immer an krummen Zeitwerten herauskommt, laenger als eine ganze Sperre
     * kann nie gesperrt werden. Damit kann sich die App nicht festfahren.
     */
    fun remainingMillis(lockMillis: Long): Long {
        val wallClockEnd = prefs.getLong(KEY_WALL_CLOCK_END, 0L)
        val uptimeEnd = prefs.getLong(KEY_UPTIME_END, 0L)
        if (wallClockEnd == 0L && uptimeEnd == 0L) return 0L

        val byUptime = uptimeEnd - SystemClock.elapsedRealtime()

        // Nach einem Neustart des Geraets zaehlt die Laufzeit wieder von vorn.
        // Bleibt rechnerisch mehr uebrig als eine ganze Sperre, kann der Wert
        // nicht aus dieser Sitzung stammen - dann zaehlt die Wanduhr.
        val remaining = if (byUptime <= lockMillis) {
            byUptime
        } else {
            wallClockEnd - System.currentTimeMillis()
        }

        return remaining.coerceIn(0L, lockMillis)
    }

    /** Code des Getraenks, das die Sperre ausgeloest hat. */
    fun lockedDrinkCode(): Int? =
        prefs.getInt(KEY_DRINK_CODE, NO_DRINK).takeIf { it != NO_DRINK }

    private companion object {
        const val PREFS_NAME = "barbot_lock"
        const val KEY_DRINK_CODE = "drink_code"
        const val KEY_WALL_CLOCK_END = "wall_clock_end"
        const val KEY_UPTIME_END = "uptime_end"
        const val NO_DRINK = -1
    }
}
