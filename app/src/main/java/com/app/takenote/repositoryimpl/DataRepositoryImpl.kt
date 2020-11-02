package com.app.takenote.repositoryimpl


import com.app.takenote.pojo.User
import com.app.takenote.repository.DataRepository
import com.app.takenote.utility.*
import com.google.firebase.firestore.FirebaseFirestore


class DataRepositoryImpl(private val fireStore: FirebaseFirestore) : DataRepository {
    override fun getCurrentUserData(
        primaryId: String,
        onSuccess: (User) -> Unit,
        onError: (String) -> Unit
    ) {
        fireStore.collection(COLLECTION_NAME).document(primaryId).get()
            .addOnSuccessListener { document ->
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

    override fun updateData(
        primaryId: String,
        updatedData: Map<String, String>,
        onError: ((String) -> Unit)
    ) {
        fireStore.collection(COLLECTION_NAME).document(primaryId).update(updatedData)
            .addOnFailureListener {
                onError.invoke(SOMETHING_WENT_WRONG)
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
}

