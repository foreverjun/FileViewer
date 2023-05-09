package com.example.fileviewer

import android.app.Application
import android.os.Environment
import androidx.room.Room
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.fileviewer.database.FileHashesDatabase
import java.util.*

class FilesManagerApplication : Application() {
    var workRequestId: UUID? = null
    override fun onCreate() {
        super.onCreate()
        val workRequest = OneTimeWorkRequestBuilder<HashWorker>()
            .setInputData(workDataOf("path" to Environment.getExternalStorageDirectory().absolutePath))
            .build()
        WorkManager.getInstance(applicationContext).enqueue(workRequest)
        workRequestId = workRequest.id
    }
}