package com.puntogris.neonmaze.models

import com.puntogris.neonmaze.utils.Constants.MAZE_COLUMNS
import com.puntogris.neonmaze.utils.Constants.MAZE_ROWS
import java.util.*

class Maze(private val player: Cell, seed: Long) {
    private val mazeCols = MAZE_COLUMNS
    private val mazeRows = MAZE_ROWS
    private val exit = Cell(mazeCols - 1, mazeRows - 1)
    private val cells: Array<Array<Cell>> = Array(mazeCols) { Array(mazeRows) { Cell() } }
    private var random: Random = Random(seed)

    fun createMaze(): Array<Array<Cell>> {
        val stack = Stack<Cell>()
        var current: Cell
        var next: Cell?

        repeat(mazeCols) { col ->
            repeat(mazeRows) {row ->
                cells[col][row] = Cell(col, row, player.id, player.color)
            }
        }
        current = cells[0][0]
        current.visited = true

        do {
            next = getNeighbour(current)
            if (next != null) {
                removeWall(current, next)
                stack.push(current)
                current = next
                current.visited = true
            } else {
                current = stack.pop()
            }

        } while (!stack.empty())
        return cells
    }

    private fun getNeighbour(cell: Cell): Cell? {
        val neighbourList: ArrayList<Cell> = arrayListOf()
        if (cell.col > 0 && !cells[cell.col - 1][cell.row].visited) {
            neighbourList.add(cells[cell.col - 1][cell.row])
        }
        if (cell.col < mazeCols - 1 && !cells[cell.col + 1][cell.row].visited) {
            neighbourList.add(cells[cell.col + 1][cell.row])
        }
        if (cell.row > 0 && !cells[cell.col][cell.row - 1].visited) {
            neighbourList.add(cells[cell.col][cell.row - 1])
        }
        if (cell.row < mazeRows - 1 && !cells[cell.col][cell.row + 1].visited) {
            neighbourList.add(cells[cell.col][cell.row + 1])
        }
        return if (neighbourList.size > 0) {
            neighbourList[random.nextInt(neighbourList.size)]
        } else {
            null
        }
    }

    private fun removeWall(current: Cell, next: Cell) {
        if (current.col == next.col && current.row == next.row + 1) {
            current.topWall = false
            next.bottomWall = false
        }
        if (current.col == next.col && current.row == next.row - 1) {
            current.bottomWall = false
            next.topWall = false
        }
        if (current.col == next.col + 1 && current.row == next.row) {
            current.leftWall = false
            next.rightWall = false
        }
        if (current.col == next.col - 1 && current.row == next.row) {
            current.rightWall = false
            next.leftWall = false
        }
    }

    fun checkExit(player: Cell) = player.col == exit.col && player.row == exit.row
}
