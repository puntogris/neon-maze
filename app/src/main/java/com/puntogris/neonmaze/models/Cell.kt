package com.puntogris.neonmaze.models

import android.view.MotionEvent
import com.puntogris.neonmaze.utils.Direction
import com.puntogris.neonmaze.utils.Utils
import kotlin.math.abs

private const val MARGIN_OFFSET = 0.5F

data class Cell(
    var col: Int = 0,
    var row: Int = 0,
    var id: String = "",
    var color: String = Utils.getRandomColor(),
    var topWall: Boolean = true,
    var leftWall: Boolean = true,
    var bottomWall: Boolean = true,
    var rightWall: Boolean = true,
    var visited: Boolean = false
) {

    fun getMoveDirection(
        event: MotionEvent,
        cellSize: Float,
        hMargin: Float,
        vMargin: Float
    ): Direction {
        val playerCenterX = hMargin + (col + MARGIN_OFFSET) * cellSize
        val playerCenterY = vMargin + (row + MARGIN_OFFSET) * cellSize
        val dx = event.x - playerCenterX
        val dy = event.y - playerCenterY
        val absDx = abs(dx)
        val absDy = abs(dy)

        return when {
            absDx < cellSize && absDy < cellSize -> Direction.NONE
            absDx > absDy && dx > 0 -> Direction.RIGHT
            absDx > absDy -> Direction.LEFT
            dy > 0 -> Direction.DOWN
            else -> Direction.UP
        }
    }
}
