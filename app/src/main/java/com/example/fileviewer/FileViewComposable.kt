package com.example.fileviewer

import android.content.Intent
import android.webkit.MimeTypeMap
import android.webkit.MimeTypeMap.getFileExtensionFromUrl
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FileViewerScreen(viewModel: FileViewViewModel, rootPath: String) {

    Column {
        var showMenu by remember { mutableStateOf(false) }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LazyRow(modifier = Modifier.weight(0.85f).align(Alignment.CenterVertically)) {
                item {
                    Text(
                        text = rootPath,
                        color = MaterialTheme.colors.onBackground,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(horizontal = 2.dp)
                            .clickable { viewModel.setCurrentDirectory(rootPath) },
                        maxLines = 1,
                        overflow = TextOverflow.Visible
                    )
                }
                val rootPathSize = rootPath.split("/").size
                items(viewModel.currentDirectoryPath.split("/").size - rootPathSize) { num ->
                    Text(
                        text = ">>" + viewModel.currentDirectoryPath.split("/")[num + rootPathSize],
                        color = MaterialTheme.colors.onBackground,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(horizontal = 2.dp).clickable {
                            viewModel.setCurrentDirectory(
                                viewModel.currentDirectoryPath.split("/")
                                    .joinToString(limit = num + rootPathSize + 1, separator = "/", truncated = "")
                            )
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Visible
                    )
                }

            }
            Box(modifier = Modifier.weight(0.15f).align(Alignment.CenterVertically)) {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_more_vert_24),
                        tint = MaterialTheme.colors.onBackground,
                        contentDescription = "More"
                    )
                }
                DropdownMenu(expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(onClick = {
                        viewModel.setSortType(SortType.NAME_ASC)
                        showMenu = false
                    }) {
                        Text(text = "Имя, по возрастанию")
                    }
                    DropdownMenuItem(onClick = {
                        viewModel.setSortType(SortType.NAME_DESC)
                        showMenu = false
                    }) {
                        Text(text = "Имя, по убыванию")
                    }
                    DropdownMenuItem(onClick = {
                        viewModel.setSortType(SortType.DATE_ASC)
                        showMenu = false
                    }) {
                        Text(text = "Дата, по возрастанию")
                    }
                    DropdownMenuItem(onClick = {
                        viewModel.setSortType(SortType.DATE_DESC)
                        showMenu = false
                    }) {
                        Text(text = "Дата, по убыванию")
                    }
                    DropdownMenuItem(onClick = {
                        viewModel.setSortType(SortType.SIZE_ASC)
                        showMenu = false
                    }) {
                        Text(text = "Размер, по возрастанию")
                    }
                    DropdownMenuItem(onClick = {
                        viewModel.setSortType(SortType.SIZE_DESC)
                        showMenu = false
                    }) {
                        Text(text = "Размер, по убыванию")
                    }
                    DropdownMenuItem(onClick = {
                        viewModel.setSortType(SortType.EXT_ASC)
                        showMenu = false
                    }) {
                        Text(text = "Расширение, по возрастанию")
                    }
                    DropdownMenuItem(onClick = {
                        viewModel.setSortType(SortType.EXT_DESC)
                        showMenu = false
                    }) {
                        Text(text = "Расширение, по убыванию")
                    }
                }
            }


        }
        LazyColumn {
            viewModel.currentDirectoryFiles.forEach { elem ->
                if (elem.isDirectory) {
                    item {
                        DirectoryCard(name = elem.name, onClick = { viewModel.setCurrentDirectory(elem.path) })
                    }
                } else {
                    item {
                        FileCard(
                            name = elem.name,
                            size = (elem.size / (1024)).toString() + " KB",
                            path = elem.path,
                            creationDate = SimpleDateFormat(
                                "dd/MM/yyyy",
                                Locale.getDefault()
                            ).format(elem.lastModified)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ChangedFilesScreen(viewModel: ChangedFilesViewModel) {
    if (viewModel.workerStatus == LoadingState.LOADING) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colors.onBackground)
        }
    } else if (viewModel.changedFiles.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Нет изменений",
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.h6
            )
        }
    } else {
        LazyColumn {
            viewModel.changedFiles.forEach { elem ->
                item {
                    FileCard(
                        name = elem.name,
                        size = (elem.size / (1024)).toString() + " KB",
                        path = elem.path,
                        creationDate = SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        ).format(elem.lastModified)
                    )
                }
            }
        }
    }

}


@Composable
fun DirectoryCard(name: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max).clickable { onClick() },
        backgroundColor = MaterialTheme.colors.background
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_baseline_folder_24),
                contentDescription = "Folder",
                tint = Color.Yellow,
                modifier = Modifier.size(48.dp).padding(horizontal = 8.dp)
            )
            Text(
                text = name,
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(horizontal = 8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun FileCard(name: String, path: String, size: String, creationDate: String) {
    val appContext = LocalContext.current.applicationContext
    //set the icon depends on the file type
    var extension = name.split(".").last()
    if (extension == name) extension = ""
    val icon = when (extension) {
        "pdf" -> R.drawable.icons8_pdf_48
        "jpg" -> R.drawable.icons8_jpg_48
        "png" -> R.drawable.icons8_png_48
        "txt" -> R.drawable.icons8_txt_48
        "doc" -> R.drawable.icons8_word_48
        "docx" -> R.drawable.icons8_word_48
        else -> R.drawable.icons8_file_48
    }
    Card(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max).clickable {
            val uri =
                FileProvider.getUriForFile(appContext, BuildConfig.APPLICATION_ID + ".fileprovider", File(path))
            val  newIntent = Intent(Intent.ACTION_VIEW)
            val  mimeType = getFileExtensionFromUrl(uri.toString())
            if (mimeType != null){
                val type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                newIntent.setDataAndType(uri,type)
                newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                appContext.startActivity(newIntent)
            }
             },
        backgroundColor = MaterialTheme.colors.background
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                tint = Color.Unspecified,
                painter = painterResource(icon),
                contentDescription = "File",
                modifier = Modifier.padding(horizontal = 8.dp).size(48.dp).weight(0.15f)
            )
            Column(modifier = Modifier.padding(horizontal = 8.dp).weight(0.7f)) {
                Text(
                    text = name,
                    color = MaterialTheme.colors.onBackground,
                    style = MaterialTheme.typography.h6,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = size, color = MaterialTheme.colors.onBackground, style = MaterialTheme.typography.body1)
                    Text(
                        text = creationDate,
                        color = MaterialTheme.colors.onBackground,
                        style = MaterialTheme.typography.body1
                    )
                }
            }

            IconButton(
                onClick = {
                        val file = File(path)
                        if (file.exists()) {
                            val uri =
                                FileProvider.getUriForFile(appContext, BuildConfig.APPLICATION_ID + ".fileprovider", file)
                            val intent = Intent(Intent.ACTION_SEND)
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            intent.type = "*/*"
                            intent.putExtra(Intent.EXTRA_STREAM, uri)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            appContext.startActivity(intent)
                        }
                },
                modifier = Modifier.align(Alignment.CenterVertically).weight(0.15f)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_share_24),
                    tint = MaterialTheme.colors.onBackground,
                    contentDescription = "Share",
                    modifier = Modifier.size(32.dp)
                )
            }

        }
    }

}
