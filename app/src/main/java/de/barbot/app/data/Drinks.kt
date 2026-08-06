package de.barbot.app.data

/**
 * Ein Drink, den der BarBot mixen kann.
 *
 * [code] ist die Zahl, die per Bluetooth an den Roboter geschickt wird. Die Codes
 * sind fest verdrahtet und muessen mit der Firmware des BarBots uebereinstimmen.
 * Inhalte 1:1 aus dem Design-Projekt ("BarBot App.dc.html").
 */
data class Drink(
    val code: Int,
    /** Zweistellige Anzeige des Codes, so wie im Design ("01"). */
    val label: String,
    val name: String,
    /** Vollstaendige Zutatenliste fuer die Infoseite. */
    val ingredients: String,
    /** Kurzform fuer die Auswahlliste. */
    val ingredientsShort: String,
    /** "250 ml · 10,5 % vol" */
    val meta: String,
)

val DRINKS: List<Drink> = listOf(
    Drink(
        code = 1, label = "01", name = "Mojito",
        ingredients = "Weißer Rum 5 cl · Limettensaft 3 cl · Rohrzuckersirup 2 cl · Minze · Soda",
        ingredientsShort = "Rum, Limette, Minze, Soda",
        meta = "250 ml · 10,5 % vol",
    ),
    Drink(
        code = 2, label = "02", name = "Caipirinha",
        ingredients = "Cachaça 5 cl · Limette 4 cl · Rohrzucker · Crushed Ice",
        ingredientsShort = "Cachaça, Limette, Zucker",
        meta = "220 ml · 14 % vol",
    ),
    Drink(
        code = 3, label = "03", name = "Gin Tonic",
        ingredients = "Gin 5 cl · Tonic Water 15 cl · Limette · Eis",
        ingredientsShort = "Gin, Tonic, Limette",
        meta = "300 ml · 8 % vol",
    ),
    Drink(
        code = 4, label = "04", name = "Cuba Libre",
        ingredients = "Brauner Rum 5 cl · Cola 15 cl · Limette · Eis",
        ingredientsShort = "Rum, Cola, Limette",
        meta = "280 ml · 9 % vol",
    ),
    Drink(
        code = 5, label = "05", name = "Tequila Sunrise",
        ingredients = "Tequila 4 cl · Orangensaft 12 cl · Grenadine · Eis",
        ingredientsShort = "Tequila, O-Saft, Grenadine",
        meta = "260 ml · 11 % vol",
    ),
    Drink(
        code = 6, label = "06", name = "Virgin Colada",
        ingredients = "Ananassaft 12 cl · Kokossirup 3 cl · Sahne · Crushed Ice",
        ingredientsShort = "Ananas, Kokos, Sahne",
        meta = "250 ml · 0 % vol",
    ),
)

fun drinkByCode(code: Int): Drink? = DRINKS.firstOrNull { it.code == code }
