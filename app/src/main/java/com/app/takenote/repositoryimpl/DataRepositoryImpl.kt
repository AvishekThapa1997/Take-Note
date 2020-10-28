package com.app.takenote.repositoryimpl

import android.util.Log
import com.app.takenote.pojo.User
import com.app.takenote.repository.BaseRepository
import com.app.takenote.repository.DataRepository
import com.app.takenote.utility.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.util.Executors

class DataRepositoryImpl(private val fireStore: FirebaseFirestore) : DataRepository,
    BaseRepositoryImpl() {
    override fun getCurrentUserData(
        primaryId: String,
        onSuccess: (User) -> Unit,
        onError: (String) -> Unit
    ) {
        if (isNetworkAvailable()) {
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
        } else {
            onError(NO_INTERNET_CONNECTION)
        }
    }

    override fun updateData(
        primaryId: String,
        updatedData: Map<String, String>,
        onSuccess: ((User) -> Unit)?,
        onError: ((String) -> Unit)?
    ) {
        if (isNetworkAvailable()) {
            fireStore.collection(COLLECTION_NAME).document(primaryId).update(updatedData)
                .addOnCompleteListener { task ->
                    Log.i("TAG", "updateData: Success")
                    if (onSuccess != null && onError != null && task.isSuccessful)
                        getCurrentUserData(primaryId, onSuccess, onError)
                    else
                        onError?.invoke(SOMETHING_WENT_WRONG)
                }.addOnFailureListener {
                    Log.i("TAG", "updateData: ")
                    onError?.invoke(SOMETHING_WENT_WRONG)
                }
        } else {
            onError?.invoke(NO_INTERNET_CONNECTION)
        }
    }

    override fun clearRegisterNetworkConnection() {
        super.clearRegisterNetworkConnection()
    }

    override fun storeCurrentUserData(
        primaryId: String,
        email: String,
        password: String,
        onSuccess: (User) -> Unit,
        onError: (String) -> Unit
    ) {
        if (isNetworkAvailable()) {
            val userData =
                mutableMapOf(
                    EMAIL to email,
                    PASSWORD to encodeString(password),
                    FULL_NAME to "",
                    IMAGE_URL to ""
                )
            fireStore.collection(COLLECTION_NAME).document(primaryId).set(userData)
                .addOnCompleteListener { storeDataTask ->
                    if (storeDataTask.isSuccessful)
                        onSuccess(User(email, primaryId))
                    else
                        onError(UNABLE_TO_REGISTER_USER)

                }.addOnFailureListener {
                    onError(SOMETHING_WENT_WRONG)
                }
        } else {
            onError(NO_INTERNET_CONNECTION)
        }
    }
}