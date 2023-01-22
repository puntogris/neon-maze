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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RepositoryImpl @Inject constructor() : Repository {

    private val firestore = Firebase.firestore

    override fun getCurrentMazeSeed(): LiveData<Seed> {
        val ref = firestore.collection(MAZE_COLLECTION).document(INFORMATION_DOCUMENT)
        return FirestoreMazeDeserializerTransformation.transform(
            FirestoreDocumentLiveData(ref)
        )
    }

    override fun getAllPlayers(seed: Seed): LiveData<List<Cell>> {
        val ref = firestore.collection(PLAYERS_COLLECTION).whereEqualTo(SEED_FIELD, seed.value)
        return FirestoreQueryCellTransformation.transform(
            FirestoreQueryLiveData(ref)
        )
    }

    override suspend fun updatePlayerPosition(player: Cell, seed: Seed) {
        withContext(Dispatchers.IO) {
            firestore.collection(PLAYERS_COLLECTION)
                .document(player.id)
                .update(
                    COLUMN_FIELD, player.col,
                    ROW_FIELD, player.row,
                    SEED_FIELD, seed.value
                )
                .await()
        }
    }

    override suspend fun createPlayerFirestore(): Cell = withContext(Dispatchers.IO) {
        val ref = firestore.collection(PLAYERS_COLLECTION).document()
        val player = Cell(id = ref.id)
        ref.set(player).await()
        player
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
