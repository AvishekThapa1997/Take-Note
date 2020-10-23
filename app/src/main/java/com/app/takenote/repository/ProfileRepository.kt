package com.app.takenote.repository

interface ProfileRepository {
    suspend fun uploadImage(
        primaryId: String,
        filePath: String,
        onSuccess: (imageUrl: String) -> Unit,
        onError: (errorMessage: String) -> Unit
    )
}