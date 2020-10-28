package com.app.takenote.repositoryimpl

import android.util.Log
import com.app.takenote.repository.BaseRepository
import com.app.takenote.utility.NetworkCheckerUtility
import org.koin.core.KoinComponent
import org.koin.core.inject

open class BaseRepositoryImpl : BaseRepository, KoinComponent {
    private val networkCheckerUtility: NetworkCheckerUtility by inject()
    override fun clearRegisterNetworkConnection() {
        networkCheckerUtility.clearRegisterNetwork()
    }
    override fun isNetworkAvailable() = networkCheckerUtility.isNetworkAvailable
}