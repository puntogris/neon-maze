package com.puntogris.neonmaze.models

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
class Cell(var col:Int = 0, var row: Int = 0,var id:String = "",var color: String = "") :
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

    fun drawMazeCellWalls(canvas: Canvas, cellSize:Float,wallPaint:Paint ){
        if (topWall)
            canvas.drawLine(
                col * cellSize - 3,
                row * cellSize,
                (col + 1) * cellSize + 3,
                row * cellSize,
                wallPaint)
        if (leftWall)
            canvas.drawLine(
                col * cellSize,
                row * cellSize - 3,
                col * cellSize,
                (row + 1) * cellSize,
                wallPaint)
        if (bottomWall)
            canvas.drawLine(
                col * cellSize - 3,
                (row + 1) * cellSize,
                (col + 1) * cellSize + 3,
                (row + 1) * cellSize,
                wallPaint)
        if (rightWall)
            canvas.drawLine(
                (col + 1) * cellSize,
                row * cellSize - 3,
                (col + 1) * cellSize,
                (row + 1) * cellSize,
                wallPaint)
    }

    @SuppressLint("Range")
    fun drawPlayerCell(canvas: Canvas, playerPaint:Paint, cellSize: Float, marginMazeScreen:Float){
        playerPaint.color = Color.parseColor(color)
        playerPaint.setShadowLayer(12f,0f,0f, Color.parseColor(color))
        canvas.drawOval(
            col * cellSize + marginMazeScreen,
            row * cellSize + marginMazeScreen,
            (col + 1) * cellSize - marginMazeScreen,
            (row + 1) * cellSize - marginMazeScreen,
            playerPaint
        )
    }

    fun restartCellPosition(){
        col = 0
        row = 0
    }
}
