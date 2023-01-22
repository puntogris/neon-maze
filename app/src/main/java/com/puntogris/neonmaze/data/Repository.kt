package com.puntogris.neonmaze.data

import androidx.lifecycle.LiveData
import com.puntogris.neonmaze.models.Cell
import com.puntogris.neonmaze.models.Seed

interface Repository {

    fun getCurrentMazeSeed(): LiveData<Seed>

    fun getAllPlayers(seed: Seed): LiveData<List<Cell>>

    fun deletePlayerFirestore(playerId: String)

    fun setNewMazeSeed()

    suspend fun updatePlayerPosition(player: Cell, seed: Seed)

    suspend fun createPlayerFirestore(): Cell
}