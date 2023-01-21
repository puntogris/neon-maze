package com.puntogris.neonmaze.ui

import androidx.lifecycle.*
import com.puntogris.neonmaze.data.FirestoreMazeDeserializerTransformation
import com.puntogris.neonmaze.data.FirestoreQueryCellTransformation
import com.puntogris.neonmaze.data.Repository
import com.puntogris.neonmaze.models.Cell
import com.puntogris.neonmaze.models.Maze
import com.puntogris.neonmaze.utils.PlayerStates
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate
import com.puntogris.neonmaze.utils.PlayerStates.*

class GameViewModel: ViewModel() {

    private val repo = Repository()
    private val _seed = MutableLiveData<Long>()
    private val seed:LiveData<Long> = _seed
    private val playerCell = MutableLiveData<Cell>()
    private var playerState: PlayerStates = HasNewMoves

    val currentMaze = Transformations.switchMap(seed){ newSeed ->
        liveData { emit(Maze(playerCell.value!!, newSeed).createMaze()) }
    }
    //This will run every 1 second and check if the player has changed position
    //With this we limit the write speed to the database, 1 write per second per document
    val timerDatabaseUpdated: TimerTask =
         Timer().scheduleAtFixedRate(0,1000){
            if (playerState is HasNewMoves){
                playerCell.value?.run { repo.updatePlayerPosition(this) }
                playerState = NotNewMoves
            }
        }

    fun createPlayer(){
        playerCell.postValue(repo.createPlayerFirestore())
    }

    fun deletePlayer(){ repo.deletePlayerFirestore(playerCell.value!!.id) }

    fun getMazeSeed():LiveData<Long>{
        val data = repo.getMazeSeedFirestore()
        return FirestoreMazeDeserializerTransformation.transform(data)
    }

    fun updateMazeSeed(seed:Long){
        _seed.value = seed
    }

    fun getPlayersOnline():LiveData<List<Cell>> =
        FirestoreQueryCellTransformation.transform(repo.getAllPlayers())

    fun updatePlayerPos(player: Cell){
        playerCell.value?.run {
            col = player.col
            row = player.row
            playerState = HasNewMoves
        }

    }

    fun playerFoundExit(player: Cell)=
        Maze(playerCell.value!!, seed.value!!).checkExit(player)

    fun stopTimer(){
        timerDatabaseUpdated.cancel()
    }

    fun setNewSeed(){
        repo.setNewMazeSeedFirestore()
    }
}
