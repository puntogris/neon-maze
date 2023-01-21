package com.puntogris.neonmaze.models

import java.util.*

class Maze(private val player: Cell, seed: Long) {
    private val mazeCols = 10
    private val mazeRows = 17
    private val exit = Cell(mazeCols - 1, mazeRows - 1)
    private val cells: Array<Array<Cell>> = Array(mazeCols) { Array(mazeRows) { Cell() } }
    private var random: Random = Random(seed)

    fun createMaze(): Array<Array<Cell>> {
        val stack = Stack<Cell>()
        var current: Cell
        var next: Cell?

        for (col in 0 until mazeCols) {
            for (row in 0 until mazeRows) {
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

        val neigbourList: ArrayList<Cell> = ArrayList()
        //vecino izquierda
        if (cell.col > 0) {
            if (!cells[cell.col - 1][cell.row].visited) {
                neigbourList.add(cells[cell.col - 1][cell.row])
            }
        }
        //vecino derecha
        if (cell.col < mazeCols - 1) {
            if (!cells[cell.col + 1][cell.row].visited) {
                neigbourList.add(cells[cell.col + 1][cell.row])
            }
        }
        //vecino arriba
        if (cell.row > 0) {
            if (!cells[cell.col][cell.row - 1].visited) {
                neigbourList.add(cells[cell.col][cell.row - 1])
            }
        }
        //vecino abajo
        if (cell.row < mazeRows - 1) {
            if (!cells[cell.col][cell.row + 1].visited) {
                neigbourList.add(cells[cell.col][cell.row + 1])
            }
        }
        return if (neigbourList.size > 0) {
            val index = random.nextInt(neigbourList.size)
            neigbourList[index]
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
