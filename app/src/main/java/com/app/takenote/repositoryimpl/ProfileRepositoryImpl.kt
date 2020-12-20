package com.app.takenote.repositoryimpl


import android.net.Uri
import com.app.takenote.repository.ProfileRepository
import com.app.takenote.utility.UNABLE_TO_UPLOAD
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileInputStream

class ProfileRepositoryImpl(private val storageReference: StorageReference) : ProfileRepository {
    override suspend fun uploadImage(
        primaryId: String,
        filePath: String,
    ): String {
        val fileStream = stream(filePath)
        val fileReference = storageReference.child("${primaryId}/profile.jpg")
        return try {
            val imageUri: Uri =
                fileReference.putStream(fileStream).await().storage.downloadUrl.await()
            imageUri.toString()
        } catch (exception: Exception) {
            ""
        }
//        fileReference.putStream(fileStream).addOnCompleteListener { uploadTask ->
//            if (uploadTask.isSuccessful) {
//                fileReference.downloadUrl.addOnSuccessListener { imageUrl ->
//                    onSuccess(imageUrl.toString())
//                }
//            }
//        }.addOnFailureListener {
//            onError(UNABLE_TO_UPLOAD)
//        }
    }

    private fun stream(filePath: String) = FileInputStream(File(filePath))
}