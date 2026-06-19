package com.lfr.community.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.lfr.community.data.repository.CommunityRepository
import com.lfr.community.ui.screens.*

enum class Screen(val route: String, val label: String, val icon: String) {
    Feed("feed", "广场", "🏛️"),
    Chats("chats", "群聊", "💬"),
    Members("members", "居民", "👥"),
    Settings("settings", "设置", "⚙️"),
}

@Composable
fun AppNavigation(repository: CommunityRepository) {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                Screen.entries.forEach { screen ->
                    NavigationBarItem(
                        selected = currentRoute?.startsWith(screen.route) == true,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(Screen.Feed.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Text(screen.icon, style = MaterialTheme.typography.titleMedium) },
                        label = { Text(screen.label, style = MaterialTheme.typography.labelSmall) },
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Feed.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(Screen.Feed.route) { FeedScreen(repository) }
            composable(Screen.Chats.route) { ChatListScreen(repository, navController) }
            composable("chats/{chatId}") { backStackEntry ->
                val chatId = backStackEntry.arguments?.getString("chatId") ?: return@composable
                ChatScreen(repository, chatId, navController)
            }
            composable("chats/{chatId}/settings") { backStackEntry ->
                val chatId = backStackEntry.arguments?.getString("chatId") ?: return@composable
                ChatSettingsScreen(repository, chatId, navController)
            }
            composable(Screen.Members.route) { MembersScreen(repository) }
            composable(Screen.Settings.route) { SettingsScreen(repository) }
        }
    }
}
