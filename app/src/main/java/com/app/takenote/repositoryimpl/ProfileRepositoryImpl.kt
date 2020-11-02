package com.app.takenote.repositoryimpl


import com.app.takenote.repository.ProfileRepository
import com.app.takenote.utility.UNABLE_TO_UPLOAD
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileInputStream

class ProfileRepositoryImpl(private val storageReference: StorageReference) : ProfileRepository {
    override fun uploadImage(
        primaryId: String,
        filePath: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
    ) {
        val fileStream = stream(filePath)
        val fileReference = storageReference.child("${primaryId}/profile.jpg")
        fileReference.putStream(fileStream)
            .addOnCompleteListener { uploadTask ->
                if (uploadTask.isSuccessful) {
                    fileReference.downloadUrl.addOnSuccessListener { imageUrl ->
                        onSuccess(imageUrl.toString())
                    }
                }
            }.addOnFailureListener {
                onError(UNABLE_TO_UPLOAD)
            }
    }

    private fun stream(filePath: String) = FileInputStream(File(filePath))
}