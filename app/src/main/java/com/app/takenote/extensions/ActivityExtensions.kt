package com.app.takenote.extensions

import androidx.work.*
import com.app.takenote.utility.IMAGE_FILE_PATH
import com.app.takenote.utility.USER_ID
import com.app.takenote.worker.ImageUploadWorker
import com.app.takenote.worker.ImageUrlUploadWorker
import java.util.concurrent.TimeUnit

fun setUpImageUploadWorker(userId: String, filePath: String): OneTimeWorkRequest {
    val imageUploadWorker = OneTimeWorkRequestBuilder<ImageUploadWorker>()
    return imageUploadWorker.apply {
        setInputMerger(OverwritingInputMerger::class.java)
        setInputData(workDataOf(IMAGE_FILE_PATH to filePath, USER_ID to userId))
    }.build()
}

fun setImageUrlUploadWorker() =
    OneTimeWorkRequestBuilder<ImageUrlUploadWorker>().apply {
        setBackoffCriteria(
            BackoffPolicy.LINEAR,
            5000,
            TimeUnit.MILLISECONDS
        )
    }.build()