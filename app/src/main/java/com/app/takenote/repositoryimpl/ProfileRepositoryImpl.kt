package com.app.takenote.repositoryimpl

import com.app.takenote.repository.ProfileRepository
import com.app.takenote.utility.PHOTO_UPLOADED_SUCCESSFULLY
import com.app.takenote.utility.SOMETHING_WENT_WRONG
import com.app.takenote.utility.UNABLE_TO_UPLOAD
import com.google.firebase.firestore.util.Executors
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileInputStream

class ProfileRepositoryImpl(private val storageReference: StorageReference) : ProfileRepository {

    override suspend fun uploadImage(
        primaryId: String,
        filePath: String,
        onSuccess: (imageUrl: String) -> Unit,
        onError: (errorMessage: String) -> Unit
    ) {
        val fileStream = stream(filePath)
        val fileReference = storageReference.child("${primaryId}/profile.jpg")
        fileReference.putStream(fileStream)
            .addOnCompleteListener(Executors.BACKGROUND_EXECUTOR) { uploadTask ->
                if (uploadTask.isSuccessful) {
                    fileReference.downloadUrl.addOnSuccessListener(Executors.BACKGROUND_EXECUTOR) { imageUrl ->
                        onSuccess(imageUrl.toString())
                    }
                } else {
                    onError(SOMETHING_WENT_WRONG)
                }
            }.addOnFailureListener(Executors.BACKGROUND_EXECUTOR) {
                onError(UNABLE_TO_UPLOAD)
            }
    }

    private fun stream(filePath: String) = FileInputStream(File(filePath))
}