package com.example.fileviewer

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.fileviewer.database.FileHashesDao
import com.example.fileviewer.database.FileHashesDatabase
import com.example.fileviewer.database.FilesHashes
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import java.util.zip.CRC32
import java.util.zip.Checksum

class HashWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        //I look through all the files in width search, calculate their hash and if it differs, I mark it as modified
        val db = FileHashesDatabase.getInstance(applicationContext)
        val dao = db.fileHashesDao()
        val path = inputData.getString("path") ?: Environment.getExternalStorageDirectory().absolutePath
        dao.setAllIsModifiedToFalse()
        dao.setAllIsDeletedToTrue()

        val queue = ArrayDeque<File>()
        queue.add(File(path))
        while (queue.isNotEmpty()) {
            val file = queue.removeFirst()

            if (file.isDirectory) {
                file.listFiles()?.forEach {
                    queue.add(it)
                }
            } else {
                val hash = getFileHash(file)
                if (hash != null) {
                    val fileHashes = dao.getByPath(file.absolutePath)
                    if (fileHashes?.firstName != null) {
                        dao.updateHash(file.absolutePath, hash)
                    } else {
                        dao.insert(FilesHashes(file.absolutePath, hash, hash, false, false))
                    }

                }
            }
        }
        dao.deleteIsDeletedFiles()

        return Result.success()
    }
}

fun getFileHash(file: File): Long? {
    return try {
        val crC32 = CRC32()
        val fis = FileInputStream(file)
        val buffer = ByteArray(1024)
        var nread: Int
        while (fis.read(buffer).also { nread = it } != -1) {
            crC32.update(buffer, 0, nread)
        }
        fis.close()
        crC32.value
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}