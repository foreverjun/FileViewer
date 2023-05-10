package com.example.fileviewer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.io.File

class FileViewViewModel : ViewModel() {
    var currentDirectoryPath by mutableStateOf("")
    var currentDirectoryFiles by mutableStateOf(listOf<DirectoryElement>())
    var currentSortType by mutableStateOf(SortType.NAME_ASC)

    fun setSortType(sortType: SortType) {
        currentSortType = sortType
        currentDirectoryFiles = when (sortType) {
            SortType.NAME_ASC -> currentDirectoryFiles.sortedBy { it.name }
            SortType.NAME_DESC -> currentDirectoryFiles.sortedByDescending { it.name }
            SortType.SIZE_ASC -> currentDirectoryFiles.sortedBy { it.size }
            SortType.SIZE_DESC -> currentDirectoryFiles.sortedByDescending { it.size }
            SortType.DATE_ASC -> currentDirectoryFiles.sortedBy { it.lastModified }
            SortType.DATE_DESC -> currentDirectoryFiles.sortedByDescending { it.lastModified }
            SortType.EXT_ASC -> currentDirectoryFiles.sortedBy { it.name.substringAfterLast(".") }
            SortType.EXT_DESC -> currentDirectoryFiles.sortedByDescending { it.name.substringAfterLast(".") }
        }
    }

    fun setCurrentDirectory(path: String) {
        currentDirectoryPath = path
        currentDirectoryFiles = getDirectoryFiles(path)
        setSortType(currentSortType)
    }

    private fun getDirectoryFiles (path : String) : List<DirectoryElement> {
        val directory = File(path)
        val files = directory.listFiles()
        val result = mutableListOf<DirectoryElement>()
        files?.forEach {
            if (it.isDirectory) {
                result.add(
                    DirectoryElement(
                        it.name,
                        true,
                        it.path
                    )
                )
            } else {
                result.add(
                    DirectoryElement(
                        it.name,
                        false,
                        it.path,
                        it.length(),
                        it.lastModified()
                    )
                )
            }
        }
        return result
    }




}

enum class SortType {
    NAME_ASC,
    NAME_DESC,
    SIZE_ASC,
    SIZE_DESC,
    DATE_ASC,
    DATE_DESC,
    EXT_ASC,
    EXT_DESC
}

data class DirectoryElement(
    val name: String,
    val isDirectory: Boolean,
    val path: String,
    val size: Long = 0,
    val lastModified: Long = 0L
)