package com.app.takenote.repository

interface ProfileRepository{
    fun uploadImage(
        primaryId: String,
        filePath: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
    )
}