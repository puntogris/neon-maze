package com.puntogris.neonmaze.ui

import androidx.lifecycle.*
import com.puntogris.neonmaze.data.FirestoreMazeDeserializerTransformation
import com.puntogris.neonmaze.data.FirestoreQueryCellTransformation
import com.puntogris.neonmaze.data.Repository
import com.puntogris.neonmaze.models.Cell
import com.puntogris.neonmaze.models.Maze
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class GameViewModel: ViewModel() {
    private val repo = Repository()

    private val _seed = MutableLiveData<Long>()
    val seed: LiveData<Long> = _seed

    private val _playerCell = MutableLiveData<Cell>()
     val playerCell: LiveData<Cell> = _playerCell

    var playerHasMoved = false

    val maze = Transformations.switchMap(seed){ newSeed ->
        liveData { emit(Maze(_playerCell.value!!, newSeed).createMaze()) }
    }

    val startTimer: TimerTask =
         Timer().scheduleAtFixedRate(0,1000){
            if (playerHasMoved)
                repo.updatePlayerPosition(playerCell.value!!)
            playerHasMoved = false
        }

    fun createPlayer(){
        _playerCell.postValue(repo.createPlayerFirestore())
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
        _playerCell.value!!.col = player.col
        _playerCell.value!!.row = player.row

        playerHasMoved = true
    }

    fun checkIfPlayerFoundExit(player: Cell)=
        Maze(playerCell.value!!, seed.value!!).checkExit(player)

    fun stopTimer(){
        startTimer.cancel()
    }


}