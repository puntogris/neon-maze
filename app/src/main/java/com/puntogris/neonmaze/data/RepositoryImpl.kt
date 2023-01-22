package com.puntogris.neonmaze.data

import androidx.lifecycle.LiveData
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
import com.puntogris.neonmaze.models.Seed
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RepositoryImpl @Inject constructor() : Repository {

    private val firestore = Firebase.firestore

    override fun getCurrentMazeSeed(): LiveData<Seed> {
        val ref = firestore.collection(MAZE_COLLECTION).document(INFORMATION_DOCUMENT)
        return FirestoreMazeDeserializerTransformation.transform(
            FirestoreDocumentLiveData(ref)
        )
    }

    override fun getAllPlayers(): LiveData<List<Cell>> {
        val ref = firestore.collection(PLAYERS_COLLECTION)
        return FirestoreQueryCellTransformation.transform(
            FirestoreQueryLiveData(ref)
        )
    }

    override fun updatePlayerPosition(player: Cell) {
        if (player.id.isNotEmpty()) {
            firestore.collection(PLAYERS_COLLECTION)
                .document(player.id)
                .update(COLUMN_FIELD, player.col, ROW_FIELD, player.row)
        }
    }

    override suspend fun createPlayerFirestore(): Cell {
        val ref = firestore.collection(PLAYERS_COLLECTION).document()
        val player = Cell(id = ref.id)
        ref.set(player).await()
        return player
    }

    override fun deletePlayerFirestore(playerId: String) {
        firestore.collection(PLAYERS_COLLECTION).document(playerId).delete()
    }

    override fun setNewMazeSeed() {
        firestore.collection(MAZE_COLLECTION)
            .document(INFORMATION_DOCUMENT)
            .update(SEED_FIELD, FieldValue.increment(1))
    }
}
