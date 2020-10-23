package com.app.takenote.repository

import com.app.takenote.pojo.User

interface DataRepository {
    fun storeCurrentUserData(
        primaryId: String,
        email: String,
        password: String,
        onSuccess: (User) -> Unit,
        onError: (String) -> Unit
    )

    fun getCurrentUserData(primaryId: String, onSuccess: (User) -> Unit, onError: (String) -> Unit)

    fun updateData(
        primaryId: String,
        updatedData: Map<String, String>,
        onSuccess: ((User) -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    )
}