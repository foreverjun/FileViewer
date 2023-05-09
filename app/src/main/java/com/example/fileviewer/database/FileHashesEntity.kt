package com.example.fileviewer.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "file_hashes")
data class FilesHashes (
    @PrimaryKey val path: String,
    @ColumnInfo(name = "old_hash") val firstName: Long?,
    @ColumnInfo(name = "new_hash") val lastName: Long?,
    @ColumnInfo(name = "is_modified") val isModified: Boolean?,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean?
)