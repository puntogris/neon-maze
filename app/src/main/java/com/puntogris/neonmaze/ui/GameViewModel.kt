package com.puntogris.neonmaze.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.puntogris.neonmaze.data.Repository
import com.puntogris.neonmaze.models.Cell
import com.puntogris.neonmaze.models.Maze
import com.puntogris.neonmaze.utils.PlayerStates
import com.puntogris.neonmaze.models.Seed
import com.puntogris.neonmaze.utils.Constants.DATABASE_UPDATE_INTERVAL
import kotlin.concurrent.scheduleAtFixedRate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Timer
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val playerCell = MutableStateFlow(Cell())

    private var playerState: PlayerStates = PlayerStates.HasNewMoves

    private val seed: LiveData<Seed> = repository.getCurrentMazeSeed()

    val onlinePlayers: LiveData<List<Cell>> = repository.getAllPlayers()

    val currentMaze = seed.map { Maze(playerCell.value, it.value) }

    val updateDatabaseTimer = Timer().scheduleAtFixedRate(0, DATABASE_UPDATE_INTERVAL) {
        if (playerState is PlayerStates.HasNewMoves) {
            repository.updatePlayerPosition(playerCell.value)
            playerState = PlayerStates.NotNewMoves
        }
    }

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
            playerState = PlayerStates.HasNewMoves
        }
    }

    fun stopTimer() {
        updateDatabaseTimer.cancel()
    }
}
