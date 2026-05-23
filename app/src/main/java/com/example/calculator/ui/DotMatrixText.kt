package com.example.calculator.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object NDotFont {
    private val glyphs = mapOf(
        '0' to arrayOf(
            " ### ",
            "#   #",
            "#  ##",
            "# # #",
            "##  #",
            "#   #",
            " ### "
        ),
        '1' to arrayOf(
            "  #  ",
            " ##  ",
            "  #  ",
            "  #  ",
            "  #  ",
            "  #  ",
            " ### "
        ),
        '2' to arrayOf(
            " ### ",
            "#   #",
            "    #",
            "  ## ",
            " #   ",
            "#    ",
            "#####"
        ),
        '3' to arrayOf(
            "#### ",
            "    #",
            "    #",
            " ### ",
            "    #",
            "    #",
            "#### "
        ),
        '4' to arrayOf(
            "#   #",
            "#   #",
            "#   #",
            "#####",
            "    #",
            "    #",
            "    #"
        ),
        '5' to arrayOf(
            "#####",
            "#    ",
            "#    ",
            "#### ",
            "    #",
            "    #",
            "#### "
        ),
        '6' to arrayOf(
            "  ## ",
            " #   ",
            "#    ",
            "#### ",
            "#   #",
            "#   #",
            " ### "
        ),
        '7' to arrayOf(
            "#####",
            "    #",
            "   # ",
            "  #  ",
            " #   ",
            " #   ",
            " #   "
        ),
        '8' to arrayOf(
            " ### ",
            "#   #",
            "#   #",
            " ### ",
            "#   #",
            "#   #",
            " ### "
        ),
        '9' to arrayOf(
            " ### ",
            "#   #",
            "#   #",
            " ####",
            "    #",
            "   # ",
            " ##  "
        ),
        'A' to arrayOf(
            " ### ",
            "#   #",
            "#   #",
            "#####",
            "#   #",
            "#   #",
            "#   #"
        ),
        'B' to arrayOf(
            "#### ",
            "#   #",
            "#   #",
            "#### ",
            "#   #",
            "#   #",
            "#### "
        ),
        'C' to arrayOf(
            " ####",
            "#    ",
            "#    ",
            "#    ",
            "#    ",
            "#    ",
            " ####"
        ),
        'D' to arrayOf(
            "#### ",
            "#   #",
            "#   #",
            "#   #",
            "#   #",
            "#   #",
            "#### "
        ),
        'E' to arrayOf(
            "#####",
            "#    ",
            "#    ",
            "#### ",
            "#    ",
            "#    ",
            "#####"
        ),
        'F' to arrayOf(
            "#####",
            "#    ",
            "#    ",
            "#### ",
            "#    ",
            "#    ",
            "#    "
        ),
        'G' to arrayOf(
            " ####",
            "#    ",
            "#    ",
            "#  ##",
            "#   #",
            "#   #",
            " ####"
        ),
        'H' to arrayOf(
            "#   #",
            "#   #",
            "#   #",
            "#####",
            "#   #",
            "#   #",
            "#   #"
        ),
        'I' to arrayOf(
            " ### ",
            "  #  ",
            "  #  ",
            "  #  ",
            "  #  ",
            "  #  ",
            " ### "
        ),
        'J' to arrayOf(
            "  ###",
            "    #",
            "    #",
            "    #",
            "    #",
            "#   #",
            " ### "
        ),
        'K' to arrayOf(
            "#   #",
            "#  # ",
            "# #  ",
            "##   ",
            "# #  ",
            "#  # ",
            "#   #"
        ),
        'L' to arrayOf(
            "#    ",
            "#    ",
            "#    ",
            "#    ",
            "#    ",
            "#    ",
            "#####"
        ),
        'M' to arrayOf(
            "#   #",
            "## ##",
            "# # #",
            "#   #",
            "#   #",
            "#   #",
            "#   #"
        ),
        'N' to arrayOf(
            "#   #",
            "##  #",
            "# # #",
            "#  ##",
            "#   #",
            "#   #",
            "#   #"
        ),
        'O' to arrayOf(
            " ### ",
            "#   #",
            "#   #",
            "#   #",
            "#   #",
            "#   #",
            " ### "
        ),
        'P' to arrayOf(
            "#### ",
            "#   #",
            "#   #",
            "#### ",
            "#    ",
            "#    ",
            "#    "
        ),
        'Q' to arrayOf(
            " ### ",
            "#   #",
            "#   #",
            "#   #",
            "# # #",
            "#  # ",
            " ##  "
        ),
        'R' to arrayOf(
            "#### ",
            "#   #",
            "#   #",
            "#### ",
            "# #  ",
            "#  # ",
            "#   #"
        ),
        'S' to arrayOf(
            " ####",
            "#    ",
            " ### ",
            "    #",
            "    #",
            "    #",
            "#### "
        ),
        'T' to arrayOf(
            "#####",
            "  #  ",
            "  #  ",
            "  #  ",
            "  #  ",
            "  #  ",
            "  #  "
        ),
        'U' to arrayOf(
            "#   #",
            "#   #",
            "#   #",
            "#   #",
            "#   #",
            "#   #",
            " ### "
        ),
        'V' to arrayOf(
            "#   #",
            "#   #",
            "#   #",
            "#   #",
            " # # ",
            " # # ",
            "  #  "
        ),
        'W' to arrayOf(
            "#   #",
            "#   #",
            "#   #",
            "# # #",
            "## ##",
            "## ##",
            "#   #"
        ),
        'X' to arrayOf(
            "#   #",
            " # # ",
            " # # ",
            "  #  ",
            " # # ",
            " # # ",
            "#   #"
        ),
        'Y' to arrayOf(
            "#   #",
            "#   #",
            " # # ",
            "  #  ",
            "  #  ",
            "  #  ",
            "  #  "
        ),
        'Z' to arrayOf(
            "#####",
            "    #",
            "   # ",
            "  #  ",
            " #   ",
            "#    ",
            "#####"
        ),
        ' ' to arrayOf(
            "     ",
            "     ",
            "     ",
            "     ",
            "     ",
            "     ",
            "     "
        ),
        '.' to arrayOf(
            "     ",
            "     ",
            "     ",
            "     ",
            "     ",
            " ##  ",
            " ##  "
        ),
        ':' to arrayOf(
            "     ",
            " ##  ",
            " ##  ",
            "     ",
            " ##  ",
            " ##  ",
            "     "
        ),
        '-' to arrayOf(
            "     ",
            "     ",
            "     ",
            " ### ",
            "     ",
            "     ",
            "     "
        ),
        '+' to arrayOf(
            "     ",
            "  #  ",
            "  #  ",
            "#####",
            "  #  ",
            "  #  ",
            "     "
        ),
        '=' to arrayOf(
            "     ",
            "     ",
            "#####",
            "     ",
            "#####",
            "     ",
            "     "
        ),
        '(' to arrayOf(
            "  ## ",
            " #   ",
            " #   ",
            " #   ",
            " #   ",
            " #   ",
            "  ## "
        ),
        ')' to arrayOf(
            " ##  ",
            "   # ",
            "   # ",
            "   # ",
            "   # ",
            "   # ",
            " ##  "
        ),
        '%' to arrayOf(
            "##  #",
            "## # ",
            "  #  ",
            " #   ",
            "  #  ",
            " # ##",
            "#  ##"
        ),
        '*' to arrayOf(
            "     ",
            " # # ",
            "  #  ",
            "#####",
            "  #  ",
            " # # ",
            "     "
        ),
        '/' to arrayOf(
            "    #",
            "   # ",
            "  #  ",
            " #   ",
            "#    ",
            "     ",
            "     "
        ),
        '?' to arrayOf(
            " ### ",
            "#   #",
            "    #",
            "   # ",
            "  #  ",
            "     ",
            "  #  "
        ),
        ',' to arrayOf(
            "     ",
            "     ",
            "     ",
            "     ",
            "     ",
            "  ## ",
            "  #  "
        ),
        '!' to arrayOf(
            "  #  ",
            "  #  ",
            "  #  ",
            "  #  ",
            "  #  ",
            "     ",
            "  #  "
        ),
        '^' to arrayOf(
            "  #  ",
            " # # ",
            "#   #",
            "     ",
            "     ",
            "     ",
            "     "
        )
    )

    fun getGlyph(char: Char): Array<String> {
        val upper = char.uppercaseChar()
        return glyphs[upper] ?: glyphs['?']!!
    }
}

@Composable
fun DotMatrixText(
    text: String,
    modifier: Modifier = Modifier,
    dotSize: Dp = 2.dp,
    dotSpacing: Dp = 1.dp,
    activeColor: Color = Color.White,
    inactiveColor: Color = Color.White.copy(alpha = 0.05f)
) {
    val glyphWidth = 5
    val glyphHeight = 7

    // Canvas dimensions are derived from custom constraints
    val charWidthDp = (dotSize * glyphWidth) + (dotSpacing * (glyphWidth - 1))
    val charHeightDp = (dotSize * glyphHeight) + (dotSpacing * (glyphHeight - 1))
    val letterSpacingDp = dotSize * 2

    val totalWidthDp = if (text.isEmpty()) 0.dp else {
        (charWidthDp * text.length) + (letterSpacingDp * (text.length - 1))
    }

    Canvas(
        modifier = modifier
            .size(width = totalWidthDp, height = charHeightDp)
            .padding(vertical = 1.dp)
    ) {
        val sizePx = dotSize.toPx()
        val spacingPx = dotSpacing.toPx()
        val letterSpacingPx = letterSpacingDp.toPx()
        val charWidthPx = charWidthDp.toPx()

        for (charIndex in text.indices) {
            val char = text[charIndex]
            val glyph = NDotFont.getGlyph(char)
            val charStartOffset = charIndex * (charWidthPx + letterSpacingPx)

            for (row in 0 until glyphHeight) {
                val rowStr = glyph[row]
                for (col in 0 until glyphWidth) {
                    val isActive = rowStr.getOrNull(col) == '#'
                    val color = if (isActive) activeColor else inactiveColor
                    
                    if (isActive || inactiveColor != Color.Transparent) {
                        drawCircle(
                            color = color,
                            radius = sizePx / 2f,
                            center = Offset(
                                x = charStartOffset + (col * (sizePx + spacingPx)) + (sizePx / 2f),
                                y = (row * (sizePx + spacingPx)) + (sizePx / 2f)
                            )
                        )
                    }
                }
            }
        }
    }
}
