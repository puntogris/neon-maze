package com.puntogris.neonmaze.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.component1
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.puntogris.neonmaze.R
import com.puntogris.neonmaze.models.Cell
import com.puntogris.neonmaze.utils.Direction
import com.puntogris.neonmaze.utils.Utils
import kotlin.math.abs

class GameView(context: Context, attrs: AttributeSet) : View(context, attrs){

    private var exit = Cell()
    private val mazeCols = 10
    private var mazeRows = 17
    private var cellSize: Float = 0F
    private var hMargin: Float = 0F
    private var vMargin: Float = 0F
    private val marginMazeScreen: Float = cellSize / 10 + 20
    var playersOnline = MutableLiveData<List<Cell>>()

    private var _playerCell = MutableLiveData<Cell>()
    val playerCell: LiveData<Cell> = _playerCell

    private var mazeCells = MutableLiveData<Array<Array<Cell>>>().value

    init{
        setLayerType(LAYER_TYPE_SOFTWARE, Paint())
    }

    @SuppressLint("Range")
    override fun onDraw(canvas: Canvas) {
        cellSize = calculateCellSize()
        hMargin = ((width - (mazeCols * cellSize)) / 2)
        vMargin = ((height - ((mazeRows + 2) * cellSize)) / 2)

        canvas.apply {
            translate(hMargin,vMargin)
            drawText(context.getString(R.string.amount_players_online,playersOnline.value?.size), 420f, (mazeRows+1) * cellSize , Utils.wallPaint)
            mazeCells?.flatten()?.map { cell -> cell.drawMazeCellWalls(canvas, cellSize)}
            exit.drawMazeExit(canvas, cellSize, marginMazeScreen)

        }
        playerCell.value?.drawPlayerCell(canvas, cellSize, marginMazeScreen)

        playersOnline.value?.forEach{ player ->
            if (player.id != playerCell.value?.id)
                player.drawPlayerCell(canvas, cellSize, marginMazeScreen)
        }
    }

    private fun calculateCellSize():Float{
        return if(width / height.toFloat() < mazeCols / mazeRows.toFloat()){
            (width / (mazeCols+1)).toFloat()
        }else{
            (height / (mazeRows+1)).toFloat()
        }
    }
    fun setMaze(maze: Array<Array<Cell>>){
        mazeCells = maze
        _playerCell.value = maze[0][0]
        exit = Cell(mazeCols - 1, mazeRows - 1)
        invalidate()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if(event?.action == MotionEvent.ACTION_DOWN)
            return true

        if(event!!.action == MotionEvent.ACTION_MOVE){
            val x:Float = event.x
            val y:Float = event.y
            val playerCenterX:Float = hMargin + (playerCell.value!!.col + 0.5f)*cellSize
            val playerCenterY:Float = vMargin + (playerCell.value!!.row + 0.5f)*cellSize
            val dx: Float = x - playerCenterX
            val dy:Float = y - playerCenterY
            val absDx = abs(dx)
            val absDy:Float = abs(dy)
            if (absDx > cellSize || absDy > cellSize){
                if(absDx > absDy) //move in x direction
                    if(dx > 0) movePlayer(Direction.RIGHT)
                    else movePlayer(Direction.LEFT)
                else //move in y direction
                    if(dy > 0) movePlayer(Direction.DOWN)
                    else movePlayer(Direction.UP)
            }
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun movePlayer(direction: Direction){
        _playerCell.value?.apply {
            when(direction){
                Direction.UP -> if(!topWall) _playerCell.postValue(mazeCells!![col][row - 1])
                Direction.DOWN -> if(!bottomWall) _playerCell.postValue(mazeCells!![col][row + 1])
                Direction.LEFT -> if(!leftWall) _playerCell.postValue(mazeCells!![col - 1][row])
                Direction.RIGHT -> if(!rightWall) _playerCell.postValue(mazeCells!![col + 1][row])
            }
        }
        invalidate()
    }

    fun restartPlayerPosition(){
        _playerCell.postValue(mazeCells!![0][0])
        invalidate()
    }

}