package com.app.takenote.repository

interface BaseRepository {
     fun clearRegisterNetworkConnection()
     fun isNetworkAvailable() : Boolean
}