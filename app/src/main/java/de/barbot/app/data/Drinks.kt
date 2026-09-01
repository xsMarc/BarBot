package de.barbot.app.data

import androidx.annotation.DrawableRes
import de.barbot.app.R

/**
 * Zentrale Konfiguration des BarBots: was er an Bord hat und was er daraus mischt.
 *
 * Hier und nur hier werden Getraenke gepflegt - Name, Zutaten, Mischverhaeltnis,
 * Bild und die Nummer, die per Bluetooth rausgeht. Die Oberflaeche baut sich
 * vollstaendig aus [DRINKS] auf, es ist also nichts weiter anzupassen.
 */

// ---------------------------------------------------------------- Bestueckung

/** Spirituosen an den Pumpen. */
val SPIRITS: List<String> = listOf("Vodka", "Gin")

/** Filler an den Pumpen. */
val MIXERS: List<String> = listOf("Tonic Water", "Ginger Ale", "Tropical Juice", "Mate")

/**
 * Wird von Hand dazugegeben, nicht gepumpt - steht hier, damit die Bestueckung
 * an einer Stelle dokumentiert ist.
 */
val EXTRAS: List<String> = listOf(
    "Rohrzucker",
    "Eis",
    "Zitronen-/Limettensaft",
    "Grenadine",
    "Deko: Zitrone, Orange",
)

/**
 * Grundlage der Prozentangaben unten. Steht Vodka oder Gin mit einer anderen
 * Staerke im Regal, hier anpassen und die Werte in [DRINKS] nachziehen.
 */
const val SPIRIT_ABV_PERCENT = 37.5

// ---------------------------------------------------------------- Getraenke

enum class DrinkCategory { COCKTAIL, MOCKTAIL }

/**
 * Ein Getraenk, das der BarBot mischen kann.
 *
 * [code] ist die Zahl, die per Bluetooth an den Roboter geht. Die Codes sind fest
 * verdrahtet und muessen mit der Firmware des BarBots uebereinstimmen - eine
 * Umsortierung der Liste aendert sie nicht.
 *
 * [image] ist das freigestellte Drinkbild. Jedes Getraenk hat eine eigene Datei in
 * `res/drawable-nodpi/`; aktuell steckt in allen dasselbe Testbild. Ein echtes
 * Bild einhaengen heisst deshalb nur: die Datei mit gleichem Namen ueberschreiben,
 * im Code aendert sich nichts.
 */
data class Drink(
    val code: Int,
    /** Zweistellige Anzeige des Codes, so wie im Design ("01"). */
    val label: String,
    val name: String,
    /** Vollstaendiges Mischverhaeltnis fuer die Infoseite. */
    val ingredients: String,
    /** Kurzform fuer die Auswahlliste. */
    val ingredientsShort: String,
    /** "180 ml · 12,5 % vol" */
    val meta: String,
    val category: DrinkCategory,
    @DrawableRes val image: Int,
)

/**
 * Alle Getraenke in der Reihenfolge der Auswahlseite: erst die Cocktails,
 * dann die alkoholfreien.
 *
 * Die Namen der in der Vorlage noch unbenannten Mischungen ("?", "??", "???",
 * "&", "&&", "&&&") stehen dort bereits in der Verhaeltnistabelle und sind von
 * dort uebernommen - sie beschreiben jeweils die Zutaten.
 */
val DRINKS: List<Drink> = listOf(

    // -- Cocktails ------------------------------------------------------------

    Drink(
        code = 1, label = "01", name = "Tropical Screwdriver",
        ingredients = "Vodka 6 cl · Tropical Juice 12 cl",
        ingredientsShort = "Vodka, Tropical Juice",
        meta = "180 ml · 12,5 % vol",
        category = DrinkCategory.COCKTAIL,
        image = R.drawable.drink_tropical_screwdriver,
    ),
    Drink(
        code = 2, label = "02", name = "Vodka Mate",
        ingredients = "Vodka 6 cl · Mate 12 cl",
        ingredientsShort = "Vodka, Mate",
        meta = "180 ml · 12,5 % vol",
        category = DrinkCategory.COCKTAIL,
        image = R.drawable.drink_vodka_mate,
    ),
    Drink(
        code = 3, label = "03", name = "Gin Tonic",
        ingredients = "Gin 6 cl · Tonic Water 12 cl",
        ingredientsShort = "Gin, Tonic Water",
        meta = "180 ml · 12,5 % vol",
        category = DrinkCategory.COCKTAIL,
        image = R.drawable.drink_gin_tonic,
    ),
    Drink(
        // Klassisch mit Ginger Beer; der BarBot hat Ginger Ale an der Pumpe.
        code = 4, label = "04", name = "Moscow Mule",
        ingredients = "Vodka 6 cl · Ginger Ale 12 cl",
        ingredientsShort = "Vodka, Ginger Ale",
        meta = "180 ml · 12,5 % vol",
        category = DrinkCategory.COCKTAIL,
        image = R.drawable.drink_moscow_mule,
    ),
    Drink(
        // In der Vorlage "???"
        code = 5, label = "05", name = "Gin Ginger Mate",
        ingredients = "Gin 3 cl · Ginger Ale 6 cl · Mate 6 cl",
        ingredientsShort = "Gin, Ginger Ale, Mate",
        meta = "150 ml · 7,5 % vol",
        category = DrinkCategory.COCKTAIL,
        image = R.drawable.drink_gin_ginger_mate,
    ),
    Drink(
        // In der Vorlage "??"
        code = 6, label = "06", name = "Gin Buck",
        ingredients = "Gin 6 cl · Ginger Ale 12 cl",
        ingredientsShort = "Gin, Ginger Ale",
        meta = "180 ml · 12,5 % vol",
        category = DrinkCategory.COCKTAIL,
        image = R.drawable.drink_gin_buck,
    ),
    Drink(
        // In der Vorlage "?"
        code = 7, label = "07", name = "Tropical Mule",
        ingredients = "Vodka 6 cl · Ginger Ale 6 cl · Tropical Juice 6 cl",
        ingredientsShort = "Vodka, Ginger Ale, Tropical Juice",
        meta = "180 ml · 12,5 % vol",
        category = DrinkCategory.COCKTAIL,
        image = R.drawable.drink_tropical_mule,
    ),
    Drink(
        // In der Vorlage "&&&"
        code = 8, label = "08", name = "Tropical Tonic Vodka",
        ingredients = "Vodka 6 cl · Tonic Water 6 cl · Tropical Juice 6 cl",
        ingredientsShort = "Vodka, Tonic Water, Tropical Juice",
        meta = "180 ml · 12,5 % vol",
        category = DrinkCategory.COCKTAIL,
        image = R.drawable.drink_tropical_tonic_vodka,
    ),
    Drink(
        code = 9, label = "09", name = "Vodka Tonic",
        ingredients = "Vodka 6 cl · Tonic Water 12 cl",
        ingredientsShort = "Vodka, Tonic Water",
        meta = "180 ml · 12,5 % vol",
        category = DrinkCategory.COCKTAIL,
        image = R.drawable.drink_vodka_tonic,
    ),
    Drink(
        // In der Vorlage "&&"
        code = 10, label = "10", name = "Tropical Gin Tonic",
        ingredients = "Gin 6 cl · Tropical Juice 6 cl · Tonic Water 6 cl",
        ingredientsShort = "Gin, Tropical Juice, Tonic Water",
        meta = "180 ml · 12,5 % vol",
        category = DrinkCategory.COCKTAIL,
        image = R.drawable.drink_tropical_gin_tonic,
    ),

    // -- Mocktails ------------------------------------------------------------

    Drink(
        code = 11, label = "11", name = "Ginger Mate",
        ingredients = "Ginger Ale 9 cl · Mate 9 cl",
        ingredientsShort = "Ginger Ale, Mate",
        meta = "180 ml · 0 % vol",
        category = DrinkCategory.MOCKTAIL,
        image = R.drawable.drink_ginger_mate,
    ),
    Drink(
        code = 12, label = "12", name = "Tropical Tonic",
        ingredients = "Tropical Juice 9 cl · Tonic Water 9 cl",
        ingredientsShort = "Tropical Juice, Tonic Water",
        meta = "180 ml · 0 % vol",
        category = DrinkCategory.MOCKTAIL,
        image = R.drawable.drink_tropical_tonic,
    ),
    Drink(
        code = 13, label = "13", name = "Tropical Ginger",
        ingredients = "Tropical Juice 9 cl · Ginger Ale 9 cl",
        ingredientsShort = "Tropical Juice, Ginger Ale",
        meta = "180 ml · 0 % vol",
        category = DrinkCategory.MOCKTAIL,
        image = R.drawable.drink_tropical_ginger,
    ),
    Drink(
        // In der Vorlage "&"
        code = 14, label = "14", name = "Tropical Spark",
        ingredients = "Tropical Juice 6 cl · Ginger Ale 6 cl · Tonic Water 6 cl",
        ingredientsShort = "Tropical Juice, Ginger Ale, Tonic Water",
        meta = "180 ml · 0 % vol",
        category = DrinkCategory.MOCKTAIL,
        image = R.drawable.drink_tropical_spark,
    ),
)

fun drinkByCode(code: Int): Drink? = DRINKS.firstOrNull { it.code == code }
