package com.example.fileviewer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FileHashesDao {
    //insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(fileHashes: FilesHashes)

    //set all isDeleted to true
    @Query("UPDATE file_hashes SET is_deleted = 1")
    fun setAllIsDeletedToTrue()

    //set all isModified to false
    @Query("UPDATE file_hashes SET is_modified = 0")
    fun setAllIsModifiedToFalse()

    //get by path
    @Query("SELECT * FROM file_hashes WHERE path = :path")
    fun getByPath(path: String): FilesHashes?

    //set isDeleted to false
    @Query("UPDATE file_hashes SET is_deleted = 0 WHERE path = :path")
    fun setIsDeletedToFalse(path: String)

    //get and set isDeleted to false transaction
    fun getAndSetIsDeletedToFalse(path: String): FilesHashes? {
        val fileHashes = getByPath(path)
        if (fileHashes != null) {
            setIsDeletedToFalse(path)
        }
        return fileHashes
    }

    //update hash and set old hash from new hash. if they are not equal then set isModified to true
    @Query("UPDATE file_hashes SET hash = :newHash, is_modified = :isModified, is_deleted = 0 WHERE path = :path")
    fun updateHash(path: String, newHash: Long, isModified : Boolean)

    //delete isDeleted files
    @Query("DELETE FROM file_hashes WHERE is_deleted = 1")
    fun deleteIsDeletedFiles()

    //get all modified files
    @Query("SELECT * FROM file_hashes WHERE is_modified = 1")
    fun getAllModifiedFiles(): List<FilesHashes>
}