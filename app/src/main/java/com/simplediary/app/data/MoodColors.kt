package com.simplediary.app.data

/**
 * Fixed mood types: one color = one type.
 * Default is white (= no mood color applied).
 */
object MoodColors {
    const val WHITE = 0xFFFFFFFFL
    const val BLACK = 0xFF1A1A1AL
    const val BLUE = 0xFF5BA4D9L
    const val YELLOW = 0xFFF2C94CL
    const val RED = 0xFFE57373L
    const val GREEN = 0xFF66BB6AL
    const val PURPLE = 0xFFAB47BCL

    /** No color selected. */
    const val DEFAULT = WHITE

    /** Each entry is a distinct mood type (no light/dark variants). */
    val palette: List<Long> = listOf(
        WHITE,
        BLACK,
        BLUE,
        YELLOW,
        RED,
        GREEN,
        PURPLE
    )

    fun isWhite(color: Long): Boolean = color == WHITE

    fun isDark(color: Long): Boolean = color == BLACK || color == PURPLE
}
