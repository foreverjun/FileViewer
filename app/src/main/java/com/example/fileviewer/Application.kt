package com.example.fileviewer

import android.app.Application
import android.content.pm.PackageManager
import android.os.Environment
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.*

class FilesManagerApplication : Application() {
    var workRequestId: UUID? = null
    override fun onCreate() {
        super.onCreate()
        if (applicationContext.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val workRequest = OneTimeWorkRequestBuilder<HashWorker>()
                .setInputData(workDataOf("path" to Environment.getExternalStorageDirectory().absolutePath))
                .build()
            WorkManager.getInstance(applicationContext).enqueue(workRequest)
            workRequestId = workRequest.id
        }

    }
}