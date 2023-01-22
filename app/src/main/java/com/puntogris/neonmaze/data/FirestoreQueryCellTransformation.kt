package com.puntogris.neonmaze.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.google.firebase.firestore.DocumentSnapshot
import com.puntogris.neonmaze.livedata.FirestoreQueryLiveData
import com.puntogris.neonmaze.models.Cell
import com.puntogris.neonmaze.utils.Constants.COLOR_FIELD
import com.puntogris.neonmaze.utils.Constants.COLUMN_FIELD
import com.puntogris.neonmaze.utils.Constants.ROW_FIELD

object FirestoreQueryCellTransformation {
    fun transform(liveData: FirestoreQueryLiveData): LiveData<List<Cell>>  {
        return Transformations.map(liveData) { snap ->
            snap.map(::deserialize)
        }
    }

    private fun deserialize(input: DocumentSnapshot?): Cell {
        val col = input?.get(COLUMN_FIELD).toString().toInt()
        val row = input?.get(ROW_FIELD).toString().toInt()
        val color = input?.get(COLOR_FIELD).toString()
        val id = input?.id.toString()
        return Cell(col, row, id, color)
    }
}
