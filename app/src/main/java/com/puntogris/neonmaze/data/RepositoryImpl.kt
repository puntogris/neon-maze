package com.puntogris.neonmaze.data

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.neonmaze.livedata.FirestoreDocumentLiveData
import com.puntogris.neonmaze.livedata.FirestoreQueryLiveData
import com.puntogris.neonmaze.models.Cell
import com.puntogris.neonmaze.utils.Constants.COLUMN_FIELD
import com.puntogris.neonmaze.utils.Constants.INFORMATION_DOCUMENT
import com.puntogris.neonmaze.utils.Constants.MAZE_COLLECTION
import com.puntogris.neonmaze.utils.Constants.PLAYERS_COLLECTION
import com.puntogris.neonmaze.utils.Constants.ROW_FIELD
import com.puntogris.neonmaze.utils.Constants.SEED_FIELD
import com.puntogris.neonmaze.utils.Utils
import javax.inject.Inject

class RepositoryImpl @Inject constructor() : Repository {

    private val firestore = Firebase.firestore

    override fun getMazeInfo(): FirestoreDocumentLiveData {
        val ref = firestore.collection(MAZE_COLLECTION).document(INFORMATION_DOCUMENT)
        return FirestoreDocumentLiveData(ref)
    }

    override fun getAllPlayers(): FirestoreQueryLiveData {
        val ref = firestore.collection(PLAYERS_COLLECTION)
        return FirestoreQueryLiveData(ref)
    }

    override fun updatePlayerPosition(player: Cell) {
        if (player.id.isNotEmpty()) {
            firestore.collection(PLAYERS_COLLECTION)
                .document(player.id)
                .update(COLUMN_FIELD, player.col, ROW_FIELD, player.row)
        }
    }

    override fun createPlayerFirestore(): Cell {
        val ref = firestore.collection(PLAYERS_COLLECTION).document()
        val player = Cell(0, 0, ref.id, Utils.getRandomColor())
        ref.set(player)
        return player
    }

    override fun deletePlayerFirestore(playerId: String) {
        firestore.collection(PLAYERS_COLLECTION).document(playerId).delete()
    }

    override fun getMazeSeedFirestore(): FirestoreDocumentLiveData {
        val ref = firestore.collection(MAZE_COLLECTION).document(INFORMATION_DOCUMENT)
        return FirestoreDocumentLiveData(ref)
    }

    override fun setNewMazeSeedFirestore() {
        firestore.collection(MAZE_COLLECTION)
            .document(INFORMATION_DOCUMENT)
            .update(SEED_FIELD, FieldValue.increment(1))
    }
}
