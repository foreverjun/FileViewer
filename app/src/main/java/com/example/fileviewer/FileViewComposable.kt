package com.example.fileviewer

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
                            onClick = { },
                            size = (elem.size / (1024)).toString() + " KB",
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
                        onClick = { },
                        size = (elem.size / (1024)).toString() + " KB",
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
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
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
fun FileCard(name: String, size: String, creationDate: String, onClick: () -> Unit) {
    //set the icon depends on the file type
    val extension = name.split(".").last()
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
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max).clickable { onClick() },
        backgroundColor = MaterialTheme.colors.background
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Icon(
                tint = Color.Unspecified,
                painter = painterResource(icon),
                contentDescription = "File",
                modifier = Modifier.padding(horizontal = 8.dp).size(48.dp)
            )
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
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
        }
    }

}
