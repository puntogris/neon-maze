package com.puntogris.neonmaze.data

import com.google.firebase.firestore.DocumentSnapshot
import com.puntogris.neonmaze.models.Cell

internal object FirestoreQueryCellsDeserializer :
    DocumentSnapshotDeserializer<Cell> {

    override fun deserialize(input: DocumentSnapshot?): Cell {
        val col = input?.get("col").toString().toInt()
        val row = input?.get("row").toString().toInt()
        val color = input?.get("color").toString()
        val id = input?.id.toString()
        return Cell(col, row, id, color)
    }
}