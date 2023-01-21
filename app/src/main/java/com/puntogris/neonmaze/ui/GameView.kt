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

    private lateinit var exit: Cell
    private val mazeCols by lazy { mazeCells?.size!! }
    private val mazeRows by lazy { mazeCells?.get(0)?.size!!}
    private val hMargin by lazy { (width - (mazeCols * cellSize)) / 2 }
    private val vMargin by lazy { (height - (mazeRows  * cellSize)) / 2}
    private var cellSize = 0F
    private val marginMazeScreen: Float = cellSize / 10 + 20
    private var playersOnline = MutableLiveData<List<Cell>>()
    private var _playerCell = MutableLiveData<Cell>()
    val playerCell: LiveData<Cell> = _playerCell
    private var mazeCells = MutableLiveData<Array<Array<Cell>>>().value

    init{
        setLayerType(LAYER_TYPE_SOFTWARE, Paint())
    }

    @SuppressLint("Range")
    override fun onDraw(canvas: Canvas) {
        cellSize = calculateCellSize()
        canvas.apply {
            translate(hMargin,vMargin)
            drawText(context.getString(R.string.amount_players_online,playersOnline.value?.size),
                width / 4.toFloat(),
                (mazeRows + 1) * cellSize,
                Utils.wallPaint
            )
            mazeCells?.let {
                it.flatten().map { cell -> cell.drawMazeCellWalls(this, cellSize)}
            }
            exit.drawMazeExit(this, cellSize, marginMazeScreen)
        }
        playerCell.value?.drawPlayerCell(canvas, cellSize, marginMazeScreen)

        playersOnline.value?.forEach{ player ->
            if (notCurrentPlayer(player))
                player.drawPlayerCell(canvas, cellSize, marginMazeScreen)
        }
    }

    private fun calculateCellSize(): Float{
        return if(width / height.toFloat() < mazeCols / mazeRows.toFloat())
            (width / (mazeCols + 1)).toFloat()
        else
            (height / (mazeRows + 1)).toFloat()
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
            playerCell.value?.run {
                movePlayer(getMoveDirection(event, cellSize, hMargin, vMargin))
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
                Direction.NONE -> return
            }
        }
        invalidate()
    }

    fun restartPlayerPosition(){
        _playerCell.value = mazeCells!![0][0]
        invalidate()
    }

    fun updatePlayersOnline(newPlayersOnline: List<Cell>){
        playersOnline.value = newPlayersOnline
        invalidate()
    }

    //Checks in order to not draw the current player when it draws all the online players
    private fun notCurrentPlayer(onlinePlayer: Cell)= onlinePlayer.id != playerCell.value?.id
}
