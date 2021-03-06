package com.puntogris.neonmaze.models

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Parcelable
import android.view.MotionEvent
import com.puntogris.neonmaze.utils.Direction
import com.puntogris.neonmaze.utils.Utils
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import kotlin.math.abs

@Parcelize
class Cell(var col:Int = 0, var row: Int = 0,var id:String = "",var color: String = "#ffffff") :
    Parcelable {
    @IgnoredOnParcel
    var topWall = true
    @IgnoredOnParcel
    var leftWall = true
    @IgnoredOnParcel
    var bottomWall = true
    @IgnoredOnParcel
    var rightWall = true
    @IgnoredOnParcel
    var visited = false

    fun drawMazeCellWalls(canvas: Canvas, cellSize:Float){
        if (topWall)
            canvas.drawLine(
                col * cellSize - 3,
                row * cellSize,
                (col + 1) * cellSize + 3,
                row * cellSize,
                Utils.wallPaint)
        if (leftWall)
            canvas.drawLine(
                col * cellSize,
                row * cellSize - 3,
                col * cellSize,
                (row + 1) * cellSize,
                Utils.wallPaint)
        if (bottomWall)
            canvas.drawLine(
                col * cellSize - 3,
                (row + 1) * cellSize,
                (col + 1) * cellSize + 3,
                (row + 1) * cellSize,
                Utils.wallPaint)
        if (rightWall)
            canvas.drawLine(
                (col + 1) * cellSize,
                row * cellSize - 3,
                (col + 1) * cellSize,
                (row + 1) * cellSize,
                Utils.wallPaint)
    }

    @SuppressLint("Range")
    fun drawPlayerCell(canvas: Canvas, cellSize: Float, marginMazeScreen:Float){
        Utils.playerPaint.color = Color.parseColor(color)
        Utils.playerPaint.setShadowLayer(12f,0f,0f, Color.parseColor(color))
        canvas.drawOval(
            col * cellSize + marginMazeScreen,
            row * cellSize + marginMazeScreen,
            (col + 1) * cellSize - marginMazeScreen,
            (row + 1) * cellSize - marginMazeScreen,
            Utils.playerPaint
        )
    }

    fun drawMazeExit(canvas: Canvas,cellSize:Float, marginMazeScreen:Float){
        canvas.drawRect(
            col * cellSize + marginMazeScreen,
            row * cellSize + marginMazeScreen,
            (col + 1) * cellSize - marginMazeScreen,
            (row + 1) * cellSize - marginMazeScreen,
            Utils.exitPaint
        )
    }

    fun getMoveDirection(event: MotionEvent, cellSize: Float, hMargin: Float, vMargin: Float): Direction{
        val x: Float = event.x
        val y: Float = event.y
        val playerCenterX: Float = hMargin + (col + 0.5f)*cellSize
        val playerCenterY: Float = vMargin + (row + 0.5f)*cellSize
        val dx: Float = x - playerCenterX
        val dy: Float = y - playerCenterY
        val absDx: Float = abs(dx)
        val absDy: Float = abs(dy)

        return if (absDx > cellSize || absDy > cellSize){
            if(absDx > absDy)
            //move in x direction
                if(dx > 0) Direction.RIGHT
                else Direction.LEFT
            else
            //move in y direction
                if(dy > 0) Direction.DOWN
                else Direction.UP
        }
        else Direction.NONE
    }


}
