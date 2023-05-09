package com.example.fileviewer

import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer

import androidx.navigation.compose.rememberNavController
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.fileviewer.ui.theme.FileViewerTheme
import com.google.accompanist.permissions.*
import com.google.accompanist.permissions.rememberPermissionState

class MainActivity : ComponentActivity() {
    private val viewModel: FileViewViewModel by viewModels()
    private val changedFilesViewModel: ChangedFilesViewModel by viewModels { ChangedFilesViewModel.Factory(this.applicationContext) }

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val workRequestId = (application as FilesManagerApplication).workRequestId
        if (workRequestId != null) {
            WorkManager.getInstance(this)
                .getWorkInfoByIdLiveData(workRequestId)
                .observe(this, Observer { workInfo ->
                    if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                        val outputData = workInfo.outputData
                        changedFilesViewModel.updateWorkerStatus(LoadingState.LOADED)
                    }
                })
        }

        setContent {
            FileViewerTheme {
                val storagePermission = rememberPermissionState(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                if (storagePermission.status.isGranted) {
                    if (viewModel.currentDirectoryPath == "") {
                        viewModel.setCurrentDirectory(Environment.getExternalStorageDirectory().absolutePath)
                    }
                    val navController = rememberNavController()
                    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                        Navigation(
                            navController = navController,
                            fileViewViewModel = viewModel,
                            changedFilesViewModel = changedFilesViewModel,
                            rootPath = Environment.getExternalStorageDirectory().absolutePath
                        )
                    }
                } else {
                    Column {
                        Text("Access to storage is required to read files")
                        Spacer(modifier = Modifier.height(8.dp))

                        LaunchedEffect(key1 = true) {
                            storagePermission.launchPermissionRequest()
                        }

                    }
                }

            }
        }
    }
}

