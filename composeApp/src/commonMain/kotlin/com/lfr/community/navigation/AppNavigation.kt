package com.lfr.community.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.lfr.community.data.repository.CommunityRepository
import com.lfr.community.ui.screens.*
import kotlinx.coroutines.launch

enum class Screen(val route: String, val label: String, val icon: String) {
    Login("login", "登录", "🔐"),
    Feed("feed", "广场", "🏛️"),
    Chats("chats", "群聊", "💬"),
    Members("members", "居民", "👥"),
    Settings("settings", "设置", "⚙️"),
}

@Composable
fun AppNavigation(repository: CommunityRepository) {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val scope = rememberCoroutineScope()

    var isLoggedIn by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(Unit) {
        scope.launch {
            isLoggedIn = repository.isLoggedIn()
        }
    }

    if (isLoggedIn == null) {
        // 加载中，显示占位
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val startDestination = if (isLoggedIn == true) Screen.Feed.route else Screen.Login.route

    Scaffold(
        bottomBar = {
            if (currentRoute != Screen.Login.route) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    Screen.entries.filter { it != Screen.Login }.forEach { screen ->
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
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(padding),
        ) {
            composable(Screen.Login.route) {
                LoginScreen(repository) {
                    // 登录成功，跳转到主页
                    navController.navigate(Screen.Feed.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }
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
            composable(Screen.Settings.route) { SettingsScreen(repository, navController) }
        }
    }
}
