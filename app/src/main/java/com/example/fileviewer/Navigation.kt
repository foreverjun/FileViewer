package com.example.fileviewer

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class Screen(val route: String, @StringRes val resourceId: Int, @DrawableRes val icon: Int) {
    object FileManager : Screen("fileManager", R.string.fileManager, R.drawable.ic_baseline_folder_24)
    object ChangedFiles : Screen("changedFiles", R.string.changedFiles, R.drawable.ic_baseline_change_circle_24)
}

val items = listOf(
    Screen.FileManager,
    Screen.ChangedFiles,
)

@Composable
fun Navigation (navController: NavHostController, fileViewViewModel: FileViewViewModel,changedFilesViewModel: ChangedFilesViewModel , rootPath : String){
    Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(painter = painterResource(screen.icon), tint = MaterialTheme.colors.primary, contentDescription = null) },
                        label = { Text(stringResource(screen.resourceId)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = Screen.FileManager.route, Modifier.padding(innerPadding)) {
            composable(Screen.FileManager.route) { FileViewerScreen(fileViewViewModel, rootPath) }
            composable(Screen.ChangedFiles.route) { ChangedFilesScreen(changedFilesViewModel) }
        }
    }
}