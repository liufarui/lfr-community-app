package com.lfr.community

import androidx.compose.runtime.Composable
import com.lfr.community.data.repository.CommunityRepository
import com.lfr.community.navigation.AppNavigation
import com.lfr.community.ui.theme.CommunityTheme

private val repository = CommunityRepository()

@Composable
fun App() {
    CommunityTheme {
        AppNavigation(repository)
    }
}
