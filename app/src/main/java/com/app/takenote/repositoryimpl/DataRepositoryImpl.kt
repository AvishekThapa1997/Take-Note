package com.app.takenote.repositoryimpl


import android.util.Log
import com.app.takenote.pojo.Note
import com.app.takenote.pojo.User
import com.app.takenote.repository.DataRepository
import com.app.takenote.utility.*
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source


class DataRepositoryImpl(private val fireStore: FirebaseFirestore) : DataRepository {
    override fun getCurrentUserData(
        primaryId: String,
        onSuccess: (User) -> Unit,
        onError: (String) -> Unit,
        source: Source?
    ) {
        val task: Task<DocumentSnapshot> = if (source == null)
            fireStore.collection(COLLECTION_NAME).document(primaryId).get()
        else
            fireStore.collection(COLLECTION_NAME).document(primaryId).get(source)
        task.addOnSuccessListener { document ->
            onSuccess(
                User(
                    document[FULL_NAME].toString(),
                    document[EMAIL].toString(),
                    document[IMAGE_URL].toString(),
                    primaryId,
                )
            )
        }.addOnFailureListener {
            onError(SOMETHING_WENT_WRONG)
        }
    }

    override fun updateUserData(
        primaryId: String,
        updatedData: Map<String, String>,
        onError: ((String) -> Unit)?
    ) {
        fireStore.collection(COLLECTION_NAME).document(primaryId).update(updatedData)
            .addOnFailureListener {
                onError?.invoke(SOMETHING_WENT_WRONG)
            }
    }

    override fun storeCurrentUserData(
        userData: Map<String, String>,
        primaryId: String,
        onError: (String) -> Unit
    ) {
        fireStore.collection(COLLECTION_NAME).document(primaryId).set(userData)
            .addOnFailureListener {
                onError(SOMETHING_WENT_WRONG)
            }
    }

    override fun storeNote(note: Note, onError: (String) -> Unit, onSuccess: (Note) -> Unit) {
        fireStore.collection(NOTE_COLLECTION).document().apply {
            val currentNote = note.copy(id = id)
            val mappedData = currentNote.toMap()
            set(mappedData).addOnFailureListener { _ ->
                onError(SOMETHING_WENT_WRONG)
            }
            onSuccess(currentNote)
        }
    }

    override fun updateNote(
        updatedData: Map<String, String>,
        noteId: String,
        onError: ((String) -> Unit)?
    ) {
        fireStore.collection(NOTE_COLLECTION).document(noteId).update(updatedData)
            .addOnFailureListener {
                onError?.invoke(SOMETHING_WENT_WRONG)
            }
    }

    override fun deleteNote(noteId: String, onError: (String) -> Unit) {
        fireStore.collection(NOTE_COLLECTION).document(noteId).delete().addOnFailureListener {
            onError(SOMETHING_WENT_WRONG)
        }
    }
}

