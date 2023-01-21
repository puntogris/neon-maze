package com.puntogris.neonmaze.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.puntogris.neonmaze.livedata.FirestoreDocumentLiveData

object FirestoreMazeDeserializerTransformation {

    fun transform(liveData: FirestoreDocumentLiveData): LiveData<Long> {
        return Transformations.map(liveData){ snap ->
            val seed = snap.get("seed").toString().toLong()
            seed
        }
    }
}
