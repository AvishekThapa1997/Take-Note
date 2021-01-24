package com.app.takenote.extensions

import androidx.work.*
import com.app.takenote.utility.*
import com.app.takenote.worker.ImageUploadWorker
import com.app.takenote.worker.ImageUrlUploadWorker
import com.app.takenote.worker.NotificationWorker
import java.util.concurrent.TimeUnit

fun WorkManager.setWork(userId: String, filePath: String): OneTimeWorkRequest {
    val imageUploadWorker = setUpImageUploadWorker(userId, filePath)
    beginUniqueWork(
        PROFILE_IMAGE_UPLOAD, ExistingWorkPolicy.REPLACE, mutableListOf(
            imageUploadWorker
        )
    ).then(setImageUrlUploadWorker()).enqueue()
    return imageUploadWorker
}

fun setUpImageUploadWorker(userId: String, filePath: String): OneTimeWorkRequest {
    val imageUploadWorker = OneTimeWorkRequestBuilder<ImageUploadWorker>()
    return imageUploadWorker.apply {
        setInputMerger(OverwritingInputMerger::class.java)
        setInputData(workDataOf(IMAGE_FILE_PATH to filePath, USER_ID to userId))
    }.build()
}

fun setImageUrlUploadWorker() =
    OneTimeWorkRequestBuilder<ImageUrlUploadWorker>().build()

fun setReminderWorker(
    title: String,
    noteId: String,
    delayDuration: Long
): OneTimeWorkRequest {
    val reminderWorker = OneTimeWorkRequestBuilder<NotificationWorker>()
    return reminderWorker.apply {
        setInitialDelay(delayDuration, TimeUnit.MILLISECONDS)
        setInputData(workDataOf(NOTE_TITLE to title, NOTE_ID to noteId))
    }.build()
}