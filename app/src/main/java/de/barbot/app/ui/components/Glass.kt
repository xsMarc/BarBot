package de.barbot.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import de.barbot.app.data.GlassShape
import de.barbot.app.ui.theme.BarBotText

/**
 * Gezeichnete Drink-Illustration. Bewusst als Vektor gemalt, damit die App
 * ohne Bilddateien auskommt und jeder Drink seine eigene Farbe bekommt.
 */
@Composable
fun DrinkGlass(
    shape: GlassShape,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        when (shape) {
            GlassShape.HIGHBALL -> drawHighball(color)
            GlassShape.COCKTAIL -> drawCocktail(color)
            GlassShape.TUMBLER -> drawTumbler(color)
        }
    }
}

private val glassOutline = BarBotText.copy(alpha = 0.55f)
private const val OUTLINE_WIDTH = 0.035f

private fun DrawScope.liquidBrush(color: Color, top: Float, bottom: Float) = Brush.verticalGradient(
    colors = listOf(color.copy(alpha = 0.95f), color.copy(alpha = 0.65f)),
    startY = top,
    endY = bottom,
)

private fun DrawScope.drawHighball(color: Color) {
    val w = size.width
    val h = size.height
    val glassWidth = w * 0.40f
    val left = (w - glassWidth) / 2f
    val top = h * 0.14f
    val bottom = h * 0.94f
    val radius = CornerRadius(glassWidth * 0.18f, glassWidth * 0.18f)

    val body = RoundRect(
        rect = Rect(Offset(left, top), Size(glassWidth, bottom - top)),
        bottomLeft = radius,
        bottomRight = radius,
        topLeft = CornerRadius.Zero,
        topRight = CornerRadius.Zero,
    )

    // Fluessigkeit
    val liquidTop = top + (bottom - top) * 0.18f
    val liquid = RoundRect(
        rect = Rect(Offset(left, liquidTop), Size(glassWidth, bottom - liquidTop)),
        bottomLeft = radius,
        bottomRight = radius,
        topLeft = CornerRadius.Zero,
        topRight = CornerRadius.Zero,
    )
    drawPath(Path().apply { addRoundRect(liquid) }, liquidBrush(color, liquidTop, bottom))

    // Glas
    drawPath(
        path = Path().apply { addRoundRect(body) },
        color = glassOutline,
        style = Stroke(width = w * OUTLINE_WIDTH),
    )

    // Strohhalm
    drawLine(
        color = color.copy(alpha = 0.9f),
        start = Offset(left + glassWidth * 0.68f, top - h * 0.06f),
        end = Offset(left + glassWidth * 0.42f, bottom - h * 0.10f),
        strokeWidth = w * 0.035f,
    )

    // Zitronenscheibe am Glasrand
    drawCircle(
        color = color.copy(alpha = 0.9f),
        radius = w * 0.075f,
        center = Offset(left + glassWidth, top + h * 0.02f),
    )
}

private fun DrawScope.drawCocktail(color: Color) {
    val w = size.width
    val h = size.height
    val rimY = h * 0.20f
    val rimLeft = w * 0.20f
    val rimRight = w * 0.80f
    val bowlBottom = h * 0.58f
    val centerX = w / 2f

    val bowl = Path().apply {
        moveTo(rimLeft, rimY)
        lineTo(rimRight, rimY)
        lineTo(centerX, bowlBottom)
        close()
    }

    // Fluessigkeit als kleineres, gleich geformtes Dreieck
    val inset = 0.10f
    val liquid = Path().apply {
        moveTo(rimLeft + (rimRight - rimLeft) * inset, rimY + h * 0.045f)
        lineTo(rimRight - (rimRight - rimLeft) * inset, rimY + h * 0.045f)
        lineTo(centerX, bowlBottom - h * 0.03f)
        close()
    }
    drawPath(liquid, liquidBrush(color, rimY, bowlBottom))

    drawPath(bowl, glassOutline, style = Stroke(width = w * OUTLINE_WIDTH))

    // Stiel und Fuss
    drawLine(
        color = glassOutline,
        start = Offset(centerX, bowlBottom),
        end = Offset(centerX, h * 0.86f),
        strokeWidth = w * OUTLINE_WIDTH,
    )
    drawLine(
        color = glassOutline,
        start = Offset(w * 0.32f, h * 0.90f),
        end = Offset(w * 0.68f, h * 0.90f),
        strokeWidth = w * OUTLINE_WIDTH,
    )

    // Olive / Deko
    drawCircle(color = color, radius = w * 0.06f, center = Offset(centerX + w * 0.12f, rimY + h * 0.05f))
}

private fun DrawScope.drawTumbler(color: Color) {
    val w = size.width
    val h = size.height
    val glassWidth = w * 0.56f
    val left = (w - glassWidth) / 2f
    val top = h * 0.32f
    val bottom = h * 0.90f
    val radius = CornerRadius(glassWidth * 0.14f, glassWidth * 0.14f)

    val liquidTop = top + (bottom - top) * 0.22f
    val liquid = RoundRect(
        rect = Rect(Offset(left, liquidTop), Size(glassWidth, bottom - liquidTop)),
        bottomLeft = radius,
        bottomRight = radius,
        topLeft = CornerRadius.Zero,
        topRight = CornerRadius.Zero,
    )
    drawPath(Path().apply { addRoundRect(liquid) }, liquidBrush(color, liquidTop, bottom))

    val body = RoundRect(
        rect = Rect(Offset(left, top), Size(glassWidth, bottom - top)),
        bottomLeft = radius,
        bottomRight = radius,
        topLeft = CornerRadius.Zero,
        topRight = CornerRadius.Zero,
    )
    drawPath(Path().apply { addRoundRect(body) }, glassOutline, style = Stroke(width = w * OUTLINE_WIDTH))

    // Eiswuerfel
    val cube = glassWidth * 0.26f
    drawRoundRect(
        color = Color.White.copy(alpha = 0.22f),
        topLeft = Offset(left + glassWidth * 0.14f, liquidTop + h * 0.03f),
        size = Size(cube, cube),
        cornerRadius = CornerRadius(cube * 0.25f, cube * 0.25f),
    )
    drawRoundRect(
        color = Color.White.copy(alpha = 0.16f),
        topLeft = Offset(left + glassWidth * 0.52f, liquidTop + h * 0.10f),
        size = Size(cube, cube),
        cornerRadius = CornerRadius(cube * 0.25f, cube * 0.25f),
    )
}
