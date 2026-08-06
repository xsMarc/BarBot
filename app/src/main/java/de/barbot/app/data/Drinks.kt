package de.barbot.app.data

import androidx.compose.ui.graphics.Color

/**
 * Ein Drink, den der BarBot mixen kann.
 *
 * [code] ist die Zahl, die per Bluetooth an den Roboter geschickt wird.
 * Die Codes sind fest verdrahtet und muessen mit der Firmware des BarBots
 * uebereinstimmen.
 */
data class Drink(
    val code: Int,
    val name: String,
    val ingredients: String,
    val alcoholic: Boolean,
    val glass: GlassShape,
    val color: Color,
)

enum class GlassShape { HIGHBALL, COCKTAIL, TUMBLER }

/** Alle verfuegbaren Drinks. Reihenfolge = Reihenfolge auf der Auswahlseite. */
val DRINKS: List<Drink> = listOf(
    Drink(
        code = 1,
        name = "Mojito",
        ingredients = "Rum · Limette · Minze · Soda",
        alcoholic = true,
        glass = GlassShape.HIGHBALL,
        color = Color(0xFF4CD68A),
    ),
    Drink(
        code = 2,
        name = "Cuba Libre",
        ingredients = "Rum · Cola · Limette",
        alcoholic = true,
        glass = GlassShape.HIGHBALL,
        color = Color(0xFF9A5B36),
    ),
    Drink(
        code = 3,
        name = "Gin Tonic",
        ingredients = "Gin · Tonic Water · Limette",
        alcoholic = true,
        glass = GlassShape.HIGHBALL,
        color = Color(0xFF7FC4E8),
    ),
    Drink(
        code = 4,
        name = "Tequila Sunrise",
        ingredients = "Tequila · Orange · Grenadine",
        alcoholic = true,
        glass = GlassShape.HIGHBALL,
        color = Color(0xFFF97C41),
    ),
    Drink(
        code = 5,
        name = "Sex on the Beach",
        ingredients = "Wodka · Pfirsich · Orange · Cranberry",
        alcoholic = true,
        glass = GlassShape.HIGHBALL,
        color = Color(0xFFF4557E),
    ),
    Drink(
        code = 6,
        name = "Wodka Lemon",
        ingredients = "Wodka · Bitter Lemon",
        alcoholic = true,
        glass = GlassShape.HIGHBALL,
        color = Color(0xFFE7D45C),
    ),
    Drink(
        code = 7,
        name = "Caipirinha",
        ingredients = "Cachaça · Limette · Rohrzucker",
        alcoholic = true,
        glass = GlassShape.TUMBLER,
        color = Color(0xFF8FD14F),
    ),
    Drink(
        code = 8,
        name = "Ipanema",
        ingredients = "Limette · Ginger Ale · Maracuja",
        alcoholic = false,
        glass = GlassShape.TUMBLER,
        color = Color(0xFFFFC24B),
    ),
)

fun drinkByCode(code: Int): Drink? = DRINKS.firstOrNull { it.code == code }
