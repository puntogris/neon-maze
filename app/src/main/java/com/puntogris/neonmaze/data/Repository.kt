package com.puntogris.neonmaze.data

import com.puntogris.neonmaze.livedata.FirestoreDocumentLiveData
import com.puntogris.neonmaze.livedata.FirestoreQueryLiveData
import com.puntogris.neonmaze.models.Cell

interface Repository {
    fun getMazeInfo(): FirestoreDocumentLiveData

    fun getAllPlayers(): FirestoreQueryLiveData

    fun updatePlayerPosition(player: Cell)

    fun createPlayerFirestore(): Cell

    fun deletePlayerFirestore(playerId: String)

    fun getMazeSeedFirestore(): FirestoreDocumentLiveData

    fun setNewMazeSeedFirestore()
}