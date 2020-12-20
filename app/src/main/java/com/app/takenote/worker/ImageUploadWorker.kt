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
import java.util.stream.Stream

class ImageUploadWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters), KoinComponent {
    private val profileRepository: ProfileRepository = get<ProfileRepositoryImpl>()
    private val dataRepository: DataRepository = get<DataRepositoryImpl>()
    override suspend fun doWork(): Result {
        val filePath = inputData.getString(IMAGE_FILE_PATH) ?: return Result.failure(
            setResult(SOMETHING_WENT_WRONG)
        )
        val userId = inputData.getString(USER_ID) ?: return Result.failure(
            setResult(SOMETHING_WENT_WRONG)
        )
        val imageUrl = profileRepository.uploadImage(userId, filePath)
        return if (!imageUrl.isEmptyOrIsBlank()) {
            Result.success(workDataOf(IMAGE_URL to imageUrl, USER_ID to userId))
        } else
            Result.failure(setResult(SOMETHING_WENT_WRONG))
    }

    private fun setResult(data: String) = workDataOf(WORKER_RESULT to data)
}