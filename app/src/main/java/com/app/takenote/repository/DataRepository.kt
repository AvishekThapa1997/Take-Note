package com.app.takenote.repository

import com.app.takenote.pojo.User

interface DataRepository{
    fun storeCurrentUserData(
       userData : Map<String,String>,
       primaryId: String,
        onError: (String) -> Unit
    )

    fun getCurrentUserData(primaryId: String, onSuccess: (User) -> Unit, onError: (String) -> Unit)

    fun updateData(
        primaryId: String,
        updatedData: Map<String,String>,
        onError: ((String) -> Unit)
    )
}