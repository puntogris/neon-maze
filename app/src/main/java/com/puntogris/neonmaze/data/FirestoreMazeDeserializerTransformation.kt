package com.puntogris.neonmaze.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.puntogris.neonmaze.livedata.FirestoreDocumentLiveData
import com.puntogris.neonmaze.utils.Constants.SEED_FIELD
import com.puntogris.neonmaze.models.Seed

object FirestoreMazeDeserializerTransformation {

    fun transform(liveData: FirestoreDocumentLiveData): LiveData<Seed> {
        return Transformations.map(liveData) { data ->
            Seed(data.getLong(SEED_FIELD) ?: 0L)
        }
    }
}
