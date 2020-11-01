package com.app.takenote.repository

import com.app.takenote.pojo.User

interface AuthRepository:BaseRepository {
    suspend  fun loginUser(
        email: String,
        password: String,
        success: (String?) -> Unit,
        error: (String) -> Unit
    )

    suspend fun signUpUser(
        email: String,
        password: String,
        success: (String?) -> Unit,
        error: (String) -> Unit
    )
}