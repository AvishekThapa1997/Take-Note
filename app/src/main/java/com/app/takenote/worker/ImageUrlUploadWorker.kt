package com.app.takenote.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.app.takenote.repository.DataRepository
import com.app.takenote.repositoryimpl.DataRepositoryImpl
import com.app.takenote.utility.IMAGE_URL
import com.app.takenote.utility.USER_ID
import com.app.takenote.utility.WORKER_RESULT
import org.koin.core.KoinComponent
import org.koin.core.get

class ImageUrlUploadWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters), KoinComponent {
    private val dataRepository: DataRepository = get<DataRepositoryImpl>()
    override suspend fun doWork(): Result {
        val imageUrl = inputData.getString(IMAGE_URL) ?: return Result.failure()
        val userId = inputData.getString(USER_ID) ?: return Result.failure()
        dataRepository.updateUserData(userId, mutableMapOf(IMAGE_URL to imageUrl))
        return Result.success()
    }
}