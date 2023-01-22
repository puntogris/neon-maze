package com.puntogris.neonmaze.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.puntogris.neonmaze.data.FirestoreMazeDeserializerTransformation
import com.puntogris.neonmaze.data.FirestoreQueryCellTransformation
import com.puntogris.neonmaze.data.Repository
import com.puntogris.neonmaze.models.Cell
import com.puntogris.neonmaze.models.Maze
import com.puntogris.neonmaze.utils.PlayerStates
import kotlin.concurrent.scheduleAtFixedRate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import java.util.TimerTask
import java.util.Timer
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val _seed = MutableStateFlow(0L)
    private val seed: StateFlow<Long> = _seed
    private val playerCell = MutableStateFlow(Cell())
    private var playerState: PlayerStates = PlayerStates.HasNewMoves

    val currentMaze = seed.map { newSeed ->
        Maze(playerCell.value, newSeed).createMaze()
    }.asLiveData()

    //This will run every 1 second and check if the player has changed position
    //With this we limit the write speed to the database, 1 write per second per document
    val timerDatabaseUpdated: TimerTask =
        Timer().scheduleAtFixedRate(0, 1000) {
            if (playerState is PlayerStates.HasNewMoves) {
                repository.updatePlayerPosition(playerCell.value)
                playerState = PlayerStates.NotNewMoves
            }
        }

    fun createPlayer() {
        playerCell.value = repository.createPlayerFirestore()
    }

    fun deletePlayer() {
        repository.deletePlayerFirestore(playerCell.value.id)
    }

    fun getMazeSeed(): LiveData<Long> {
        val data = repository.getMazeSeedFirestore()
        return FirestoreMazeDeserializerTransformation.transform(data)
    }

    fun updateMazeSeed(seed: Long) {
        _seed.value = seed
    }

    fun getPlayersOnline(): LiveData<List<Cell>> {
        return FirestoreQueryCellTransformation.transform(repository.getAllPlayers())
    }

    fun updatePlayerPos(player: Cell) {
        playerCell.value.apply {
            col = player.col
            row = player.row
            playerState = PlayerStates.HasNewMoves
        }
    }

    fun playerFoundExit(player: Cell) = Maze(playerCell.value, seed.value).checkExit(player)

    fun stopTimer() {
        timerDatabaseUpdated.cancel()
    }

    fun setNewSeed() {
        repository.setNewMazeSeedFirestore()
    }
}
