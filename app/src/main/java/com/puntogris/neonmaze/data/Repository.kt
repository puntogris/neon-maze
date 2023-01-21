package com.puntogris.neonmaze.data

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.neonmaze.livedata.FirestoreDocumentLiveData
import com.puntogris.neonmaze.livedata.FirestoreQueryLiveData
import com.puntogris.neonmaze.models.Cell
import com.puntogris.neonmaze.utils.Utils

class Repository : IRepository {
    private val firestore = Firebase.firestore

    override fun getMazeInfo(): FirestoreDocumentLiveData {
        val ref = firestore.collection("maze").document("information")
        return FirestoreDocumentLiveData(ref)
    }

    override fun getAllPlayers(): FirestoreQueryLiveData {
        val ref = firestore.collection("players")
        return FirestoreQueryLiveData(ref)
    }

    override fun updatePlayerPosition(player: Cell) {
        firestore.collection("players").document(player.id)
            .update("col", player.col, "row", player.row)
    }

    override fun createPlayerFirestore(): Cell {
        val color = Utils.getRandomColor()
        val player = hashMapOf(
            "row" to 0,
            "col" to 0,
            "color" to color
        )
        val ref = firestore.collection("players").document()
        ref.set(player)
        return Cell(0, 0, ref.id, color)
    }

    override fun deletePlayerFirestore(playerId: String) {
        firestore.collection("players").document(playerId).delete()
    }

    override fun getMazeSeedFirestore(): FirestoreDocumentLiveData {
        val ref = firestore.collection("maze").document("information")
        return FirestoreDocumentLiveData(ref)
    }

    override fun setNewMazeSeedFirestore() {
        firestore.collection("maze").document("information").update("seed", FieldValue.increment(1))
    }
}
