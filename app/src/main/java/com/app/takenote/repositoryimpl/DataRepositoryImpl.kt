package com.app.takenote.repositoryimpl

import com.app.takenote.pojo.User
import com.app.takenote.repository.DataRepository
import com.app.takenote.utility.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.util.Executors

class DataRepositoryImpl(private val fireStore: FirebaseFirestore) : DataRepository {
    override fun getCurrentUserData(
        primaryId: String,
        onSuccess: (User) -> Unit,
        onError: (String) -> Unit
    ) {
        fireStore.collection(COLLECTION_NAME).document(primaryId).get()
            .addOnSuccessListener(Executors.BACKGROUND_EXECUTOR) { document ->
                onSuccess(
                    User(
                        document[FULL_NAME].toString(),
                        document[EMAIL].toString(),
                        document[IMAGE_URL].toString(),
                        primaryId,
                    )
                )
            }.addOnFailureListener(Executors.BACKGROUND_EXECUTOR) {
                error(SOMETHING_WENT_WRONG)
            }
    }

    override fun updateData(
        primaryId: String,
        updatedData: Map<String, String>,
        onSuccess: ((User) -> Unit)?,
        onError: ((String) -> Unit)?
    ) {
        fireStore.collection(COLLECTION_NAME).document(primaryId).update(updatedData)
            .addOnCompleteListener {
                if (onSuccess != null && onError != null)
                    getCurrentUserData(primaryId, onSuccess, onError)
            }.addOnFailureListener {
                onError?.invoke(SOMETHING_WENT_WRONG)
            }
    }

    override fun storeCurrentUserData(
        primaryId: String,
        email: String,
        password: String,
        onSuccess: (User) -> Unit,
        onError: (String) -> Unit
    ) {
        val userData =
            mutableMapOf(EMAIL to email, PASSWORD to encodeString(password), FULL_NAME to "", IMAGE_URL to "")
        fireStore.collection(COLLECTION_NAME).document(primaryId).set(userData)
            .addOnCompleteListener(Executors.BACKGROUND_EXECUTOR) { storeDataTask ->
                if (storeDataTask.isSuccessful)
                    onSuccess(User(email, primaryId))
                else
                    onError(UNABLE_TO_REGISTER_USER)

            }.addOnFailureListener(Executors.BACKGROUND_EXECUTOR) {
                onError(SOMETHING_WENT_WRONG)
            }
    }
}