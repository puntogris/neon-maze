package com.puntogris.neonmaze.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.google.firebase.firestore.DocumentSnapshot
import com.puntogris.neonmaze.livedata.FirestoreQueryLiveData
import com.puntogris.neonmaze.models.Cell


object FirestoreQueryCellTransformation {

    fun transform(liveData: FirestoreQueryLiveData): LiveData<List<Cell>>  {
        return Transformations.map(liveData) { snap: List<DocumentSnapshot?> ->
            snap.map { cell ->
                FirestoreQueryCellsDeserializer.deserialize(cell)
            }
        }
    }

}