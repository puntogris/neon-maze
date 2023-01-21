package com.puntogris.neonmaze.data

import com.google.firebase.firestore.DocumentSnapshot

internal interface DocumentSnapshotDeserializer<T> :
    Deserializer<DocumentSnapshot, T>
