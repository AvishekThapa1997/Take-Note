package com.app.takenote.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.app.takenote.repository.DataRepository
import com.app.takenote.repositoryimpl.DataRepositoryImpl
import com.app.takenote.utility.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class NotificationWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters), KoinComponent {
    private val mContext: Context = context
    private val dataRepository: DataRepository by inject<DataRepositoryImpl>()
    override suspend fun doWork(): Result {
        // val title = inputData.getString(NOTE_TITLE) ?: Result.failure()
        val id = inputData.getString(NOTE_ID) ?: failure("")
        val body = inputData.getString(NOTE_BODY) ?: failure(id.toString())
        val title = inputData.getString(NOTE_TITLE) ?: failure(id.toString())
        showNotification(mContext, title.toString(), body.toString())
        dataRepository.updateNote(mapOf(REMINDER_TIME to ""), id.toString())
        return Result.success()
    }

    private fun failure(noteId: String): Result {
        if (noteId.isNotEmpty()) {
            dataRepository.updateNote(mapOf(REMINDER_TIME to ""), noteId)
        }
        return Result.failure()
    }
}