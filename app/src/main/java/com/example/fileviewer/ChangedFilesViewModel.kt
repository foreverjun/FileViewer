package com.example.fileviewer

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fileviewer.database.FileHashesDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class ChangedFilesViewModel(appContext : Context) : ViewModel() {
    private val db = FileHashesDatabase.getInstance(appContext)
    private val dao = db.fileHashesDao()

    var workerStatus by mutableStateOf(LoadingState.LOADING)
    var changedFiles by mutableStateOf(listOf<DirectoryElement>())

    fun updateWorkerStatus(status: LoadingState) {
        viewModelScope.launch (Dispatchers.IO ){
            workerStatus = status
            if (status == LoadingState.LOADED) {
                changedFiles = dao.getAllModifiedFiles().map {
                    DirectoryElement(
                        it.path.substringAfterLast("/"),
                        false,
                        it.path,
                        File(it.path).length(),
                        File(it.path).lastModified()
                    )
                }
            }
            if (status == LoadingState.ERROR) {
                changedFiles = listOf()
            }
        }
    }

    class Factory(private val appContext: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChangedFilesViewModel(appContext) as T
        }
    }
}

enum class LoadingState {
    LOADING,
    LOADED,
    ERROR
}