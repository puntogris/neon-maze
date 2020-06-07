package com.puntogris.neonmaze.livedata

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*

class FirestoreDocumentLiveData(private val documentReference: DocumentReference): LiveData<DocumentSnapshot>(),
    EventListener<DocumentSnapshot> {

    private var listenerRegistration: ListenerRegistration? = null

    override fun onActive() {
        listenerRegistration = documentReference.addSnapshotListener(this)
    }

    override fun onInactive() {
        listenerRegistration!!.remove()
    }

    override fun onEvent(snapshot: DocumentSnapshot?, e: FirebaseFirestoreException?) {
        postValue(snapshot!!)
    }

}