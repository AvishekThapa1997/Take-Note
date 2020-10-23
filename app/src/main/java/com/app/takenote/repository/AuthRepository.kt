package com.app.takenote.repository

import com.app.takenote.pojo.User

interface AuthRepository {
    suspend  fun loginUser(
        email: String,
        password: String,
        success: (User) -> Unit,
        error: (String) -> Unit
    )

    suspend fun signUpUser(
        email: String,
        password: String,
        success: (User) -> Unit,
        error: (String) -> Unit
    )
}