package com.example.fileviewer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room

@Database(entities = [FilesHashes::class], version = 1)
abstract class FileHashesDatabase : androidx.room.RoomDatabase() {
    abstract fun fileHashesDao(): FileHashesDao

    companion object {

        @Volatile
        private var instance: FileHashesDatabase? = null

        fun getInstance(context: Context): FileHashesDatabase{
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, FileHashesDatabase::class.java, "file-hashes")
                .build()
    }
}