package com.puntogris.neonmaze.data

import androidx.lifecycle.LiveData
import com.puntogris.neonmaze.livedata.FirestoreDocumentLiveData
import com.puntogris.neonmaze.models.Cell
import com.puntogris.neonmaze.models.Seed

interface Repository {

    fun getCurrentMazeSeed(): LiveData<Seed>

    fun getAllPlayers(): LiveData<List<Cell>>

    fun updatePlayerPosition(player: Cell)

    fun deletePlayerFirestore(playerId: String)

    fun setNewMazeSeed()

    suspend fun createPlayerFirestore(): Cell
}