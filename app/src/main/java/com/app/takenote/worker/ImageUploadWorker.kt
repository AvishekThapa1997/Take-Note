package com.app.takenote.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.app.takenote.extensions.isEmptyOrIsBlank
import com.app.takenote.repository.DataRepository
import com.app.takenote.repository.ProfileRepository
import com.app.takenote.repositoryimpl.DataRepositoryImpl
import com.app.takenote.repositoryimpl.ProfileRepositoryImpl
import com.app.takenote.utility.*
import org.koin.core.KoinComponent
import org.koin.core.get

class ImageUploadWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters), KoinComponent {
    private val profileRepository: ProfileRepository = get<ProfileRepositoryImpl>()
    override suspend fun doWork(): Result {
        val filePath = inputData.getString(IMAGE_FILE_PATH) ?: return Result.failure(
            setErrorResult()
        )
        val userId = inputData.getString(USER_ID) ?: return Result.failure(
            setErrorResult()
        )
        val imageUrl = profileRepository.uploadImage(userId, filePath)
        return if (!imageUrl.isEmptyOrIsBlank()) {
            Result.success(workDataOf(IMAGE_URL to imageUrl, USER_ID to userId))
        } else
            Result.failure(setErrorResult())
    }

    private fun setErrorResult() = workDataOf(WORKER_RESULT to SOMETHING_WENT_WRONG)
}