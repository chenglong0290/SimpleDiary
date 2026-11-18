package com.simplediary.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.simplediary.app.SimpleDiaryApp
import com.simplediary.app.ui.diary.DiaryEditorScreen
import com.simplediary.app.ui.diary.DiaryEditorViewModel
import com.simplediary.app.ui.diary.DiaryListScreen
import com.simplediary.app.ui.diary.DiaryListViewModel
import com.simplediary.app.ui.onthisday.OnThisDayScreen
import com.simplediary.app.ui.onthisday.OnThisDayViewModel

private sealed class Dest(val route: String) {
    data object Diary : Dest("diary")
    data object OnThisDay : Dest("on_this_day")
    data object Editor : Dest("editor/{entryId}") {
        fun create(entryId: Long) = "editor/$entryId"
    }
}

private data class TabItem(
    val dest: Dest,
    val label: String,
    val icon: ImageVector
)

@Composable
fun SimpleDiaryAppRoot(app: SimpleDiaryApp) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBottomBar = currentRoute == Dest.Diary.route || currentRoute == Dest.OnThisDay.route

    val tabs = listOf(
        TabItem(Dest.Diary, "日记", Icons.Outlined.Book),
        TabItem(Dest.OnThisDay, "回顾", Icons.Outlined.History)
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    tabs.forEach { tab ->
                        val selected = currentRoute == tab.dest.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.dest.route) {
                                    popUpTo(Dest.Diary.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Dest.Diary.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Dest.Diary.route) {
                val vm: DiaryListViewModel = viewModel(
                    factory = DiaryListViewModel.Factory(app.repository)
                )
                DiaryListScreen(
                    viewModel = vm,
                    onOpenEditor = { id ->
                        navController.navigate(Dest.Editor.create(id))
                    }
                )
            }
            composable(Dest.OnThisDay.route) {
                val vm: OnThisDayViewModel = viewModel(
                    factory = OnThisDayViewModel.Factory(app.repository)
                )
                OnThisDayScreen(
                    viewModel = vm,
                    onOpenEditor = { id ->
                        navController.navigate(Dest.Editor.create(id))
                    }
                )
            }
            composable(
                route = Dest.Editor.route,
                arguments = listOf(navArgument("entryId") { type = NavType.LongType })
            ) { entry ->
                val entryId = entry.arguments?.getLong("entryId") ?: return@composable
                val vm: DiaryEditorViewModel = viewModel(
                    factory = DiaryEditorViewModel.Factory(entryId, app.repository)
                )
                DiaryEditorScreen(
                    viewModel = vm,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
