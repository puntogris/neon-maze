package com.puntogris.neonmaze.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.puntogris.neonmaze.data.Repository
import com.puntogris.neonmaze.models.Cell
import com.puntogris.neonmaze.models.Maze
import com.puntogris.neonmaze.models.Seed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val playerCell = MutableStateFlow(Cell())

    private val seed: LiveData<Seed> = repository.getCurrentMazeSeed()

    val onlinePlayers: LiveData<List<Cell>> = repository.getAllPlayers()

    val currentMaze = seed.map { Maze(playerCell.value, it.value) }

    suspend fun createPlayer() {
        playerCell.value = repository.createPlayerFirestore()
    }

    fun deletePlayer() {
        repository.deletePlayerFirestore(playerCell.value.id)
    }

    fun onPlayerChangedPosition(position: Cell) {
        val exitReached = currentMaze.value?.checkExit(position) ?: false
        if (exitReached) {
            repository.setNewMazeSeed()
        } else {
            updatePlayerPosition(position)
        }
    }

    private fun updatePlayerPosition(player: Cell) {
        playerCell.value.apply {
            col = player.col
            row = player.row
        }
        viewModelScope.launch {
            repository.updatePlayerPosition(playerCell.value)
        }
    }
}
