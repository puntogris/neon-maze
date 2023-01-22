package com.puntogris.neonmaze.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.puntogris.neonmaze.R
import com.puntogris.neonmaze.models.Cell
import com.puntogris.neonmaze.utils.Direction
import com.puntogris.neonmaze.utils.Utils

class GameView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private lateinit var exit: Cell
    private var cellSize = 0F
    private val marginMazeScreen: Float = cellSize / 10 + 20
    private var playersOnline = MutableLiveData<List<Cell>>()
    private var _playerCell = MutableLiveData<Cell>()
    val playerCell: LiveData<Cell> = _playerCell

    private var mazeCells: Array<Array<Cell>> = emptyArray()

    private val mazeCols: Int
        get() = mazeCells.size

    private val mazeRows: Int
        get() = mazeCells.first().size

    private val hMargin: Float
        get() = (width - (mazeCols * cellSize)) / 2

    private val vMargin: Float
        get() = (height - (mazeRows * cellSize)) / 2

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, Paint())
    }

    override fun onDraw(canvas: Canvas) {
        cellSize = calculateCellSize()
        with(canvas) {
            translate(hMargin, vMargin)
            drawText(
                context.getString(R.string.amount_players_online, playersOnline.value?.size),
                width / 4.toFloat(),
                (mazeRows + 1) * cellSize,
                Utils.wallPaint
            )
            mazeCells.flatten().map { cell -> cell.drawMazeCellWalls(this, cellSize) }
            exit.drawMazeExit(this, cellSize, marginMazeScreen)
        }
        playerCell.value?.drawPlayerCell(canvas, cellSize, marginMazeScreen)
        playersOnline.value?.forEach { player ->
            if (notCurrentPlayer(player)) {
                player.drawPlayerCell(canvas, cellSize, marginMazeScreen)
            }
        }
    }

    private fun calculateCellSize(): Float {
        return if (width / height < mazeCols / mazeRows.toFloat()) {
            width / mazeCols.inc()
        } else {
            height / mazeRows.inc()
        }.toFloat()
    }

    fun setMaze(maze: Array<Array<Cell>>) {
        mazeCells = maze
        _playerCell.value = maze[0][0]
        exit = Cell(mazeCols.dec(), mazeRows.dec())
        invalidate()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            return true
        }
        if (event?.action == MotionEvent.ACTION_MOVE) {
            playerCell.value?.let {
                movePlayer(it.getMoveDirection(event, cellSize, hMargin, vMargin))
            }
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun movePlayer(direction: Direction) {
        getPlayerNewCell(direction)?.let { newCell ->
            _playerCell.postValue(newCell)
        }
        invalidate()
    }

    private fun getPlayerNewCell(direction: Direction): Cell? {
        return playerCell.value?.run {
            when {
                direction == Direction.UP && !topWall -> mazeCells[col][row.dec()]
                direction == Direction.DOWN && !bottomWall -> mazeCells[col][row.inc()]
                direction == Direction.LEFT && !leftWall -> mazeCells[col.dec()][row]
                direction == Direction.RIGHT && !rightWall -> mazeCells[col.inc()][row]
                else -> null
            }
        }
    }

    fun restartPlayerPosition() {
        _playerCell.value = mazeCells[0][0]
        invalidate()
    }

    fun updatePlayersOnline(newPlayersOnline: List<Cell>) {
        playersOnline.value = newPlayersOnline
        invalidate()
    }

    //Checks in order to not draw the current player when it draws all the online players
    private fun notCurrentPlayer(onlinePlayer: Cell) = onlinePlayer.id != playerCell.value?.id
}
