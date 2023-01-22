package com.puntogris.neonmaze.ui

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.puntogris.neonmaze.R
import com.puntogris.neonmaze.models.Cell
import com.puntogris.neonmaze.models.Maze
import com.puntogris.neonmaze.utils.Constants.MAZE_COLUMNS
import com.puntogris.neonmaze.utils.Direction
import com.puntogris.neonmaze.utils.Utils
import com.puntogris.neonmaze.utils.Utils.getWallPaint
import com.puntogris.neonmaze.utils.Utils.playerPaint
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

private const val MARGIN_SIZE = 20
private const val MIDDLE_SCREEN_RATIO = 2
private const val FADE_ANIMATION_DURATION = 500L
private const val FADE_ANIMATION_INTERVAL = 8000L
private const val ALPHA_VISIBLE = 255
private const val WALL_SIZE = 3

class GameView(
    context: Context,
    attrs: AttributeSet
) : View(context, attrs), ValueAnimator.AnimatorUpdateListener {

    private val exit: Cell = Cell()

    private var cellSize: Float = 0F

    private val marginMazeScreen: Float = cellSize / MAZE_COLUMNS + MARGIN_SIZE

    private var playersOnline: List<Cell> = emptyList()

    private var onPlayerMovedListener: (Cell) -> Unit = {}

    private var playerCell = Cell()

    private var mazeCells: Array<Array<Cell>> = emptyArray()

    private val mazeCols: Int
        get() = mazeCells.size

    private val mazeRows: Int
        get() = mazeCells.first().size

    private val hMargin: Float
        get() = (width - (mazeCols * cellSize)) / MIDDLE_SCREEN_RATIO

    private val vMargin: Float
        get() = (height - (mazeRows * cellSize)) / MIDDLE_SCREEN_RATIO

    private var showMaze = true

    private var mazeAlpha = ALPHA_VISIBLE

    private val animator = ValueAnimator.ofInt(ALPHA_VISIBLE).apply {
        duration = FADE_ANIMATION_DURATION
        addUpdateListener(this@GameView)
    }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, Paint())
        Timer().scheduleAtFixedRate(FADE_ANIMATION_INTERVAL, FADE_ANIMATION_INTERVAL) {
            showMaze = !showMaze
            post { animator.start() }
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (mazeCells.isEmpty()) {
            return
        }
        with(canvas) {
            translate(hMargin, vMargin)
            drawOnlinePlayerText()
            drawMazeCellWalls()
            drawMazeExit()
            drawPlayerCell(playerCell)
            drawOnlinePlayersCell()
        }
    }

    fun setMaze(maze: Maze) {
        mazeCells = maze.getMazeMatrix()
        playerCell = mazeCells[0][0]
        exit.col = mazeCols.dec()
        exit.row = mazeRows.dec()
        cellSize = (width / mazeCols.inc()).toFloat()
        invalidate()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            return true
        }
        if (event?.action == MotionEvent.ACTION_MOVE) {
            movePlayer(playerCell.getMoveDirection(event, cellSize, hMargin, vMargin))
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun movePlayer(direction: Direction) {
        getPlayerNewCell(direction)?.let { newCell ->
            playerCell = newCell
            onPlayerMovedListener(newCell)
        }
        invalidate()
    }

    private fun getPlayerNewCell(direction: Direction): Cell? {
        return playerCell.run {
            when {
                direction == Direction.UP && !topWall -> mazeCells[col][row.dec()]
                direction == Direction.DOWN && !bottomWall -> mazeCells[col][row.inc()]
                direction == Direction.LEFT && !leftWall -> mazeCells[col.dec()][row]
                direction == Direction.RIGHT && !rightWall -> mazeCells[col.inc()][row]
                else -> null
            }
        }
    }

    fun updatePlayersOnline(newPlayersOnline: List<Cell>) {
        playersOnline = newPlayersOnline
        invalidate()
    }

    fun setPlayerMoveListener(listener: (Cell) -> Unit) {
        onPlayerMovedListener = listener
    }

    override fun onAnimationUpdate(animation: ValueAnimator) {
        mazeAlpha = if (showMaze) {
            animation.animatedValue as Int
        } else {
            ALPHA_VISIBLE - animation.animatedValue as Int
        }
        invalidate()
    }

    private fun Canvas.drawOnlinePlayerText() {
        drawText(
            context.getString(R.string.amount_players_online, playersOnline.size),
            width / 4.toFloat(),
            (mazeRows.inc()) * cellSize,
            getWallPaint()
        )
    }

    private fun Canvas.drawMazeCellWalls() {
        val maze = arrayListOf<Float>().apply {
            mazeCells.flatten().forEach {
                if (it.topWall) {
                    add(it.col * cellSize - WALL_SIZE)
                    add(it.row * cellSize)
                    add(it.col.inc() * cellSize + WALL_SIZE)
                    add(it.row * cellSize)
                }
                if (it.leftWall) {
                    add(it.col * cellSize)
                    add(it.row * cellSize - WALL_SIZE)
                    add(it.col * cellSize)
                    add(it.row.inc() * cellSize)
                }
                if (it.bottomWall) {
                    add(it.col * cellSize - WALL_SIZE)
                    add(it.row.inc() * cellSize)
                    add(it.col.inc() * cellSize + WALL_SIZE)
                    add(it.row.inc() * cellSize)
                }
                if (it.rightWall) {
                    add(it.col.inc() * cellSize)
                    add(it.row * cellSize - WALL_SIZE)
                    add(it.col.inc() * cellSize)
                    add(it.row.inc() * cellSize)
                }
            }
        }
        drawLines(maze.toFloatArray(), getWallPaint(mazeAlpha))
    }

    private fun Canvas.drawPlayerCell(cell: Cell) {
        with(cell) {
            playerPaint.color = Color.parseColor(color)
            playerPaint.setShadowLayer(12f, 0f, 0f, Color.parseColor(color))
            drawOval(
                col * cellSize + marginMazeScreen,
                row * cellSize + marginMazeScreen,
                (col + 1) * cellSize - marginMazeScreen,
                (row + 1) * cellSize - marginMazeScreen,
                playerPaint
            )
        }
    }

    private fun Canvas.drawOnlinePlayersCell() {
        playersOnline.forEach { drawPlayerCell(it) }
    }

    private fun Canvas.drawMazeExit() {
        with(exit) {
            drawRect(
                col * cellSize + marginMazeScreen,
                row * cellSize + marginMazeScreen,
                col.inc() * cellSize - marginMazeScreen,
                row.inc() * cellSize - marginMazeScreen,
                Utils.exitPaint
            )
        }
    }
}
