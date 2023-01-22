package com.puntogris.neonmaze.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.puntogris.neonmaze.livedata.FirestoreDocumentLiveData
import com.puntogris.neonmaze.utils.Constants.SEED_FIELD

object FirestoreMazeDeserializerTransformation {

    fun transform(liveData: FirestoreDocumentLiveData): LiveData<Long> {
        return liveData.map { snap ->
            snap.getLong(SEED_FIELD) ?: 0L
        }
    }
}
