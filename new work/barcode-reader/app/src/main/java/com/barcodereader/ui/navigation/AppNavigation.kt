package com.barcodereader.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.barcodereader.data.HistoryStorage
import com.barcodereader.ui.generate.GenerateScreen
import com.barcodereader.ui.history.HistoryScreen
import com.barcodereader.ui.scan.ScanScreen

@Composable
fun AppNavigation(historyStorage: HistoryStorage) {
    val navController = rememberNavController()
    var selectedTab by remember { mutableStateOf(TabItem.Scan.route) }
    val isDarkTheme = isSystemInDarkTheme()
    
    val tabs = listOf(
        TabItem.Scan,
        TabItem.History,
        TabItem.Generate
    )

    Scaffold(
        bottomBar = {
            IOSTabBar(
                tabs = tabs,
                selectedTab = selectedTab,
                onTabSelected = { route ->
                    selectedTab = route
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                isDarkTheme = isDarkTheme
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NavHost(
                navController = navController,
                startDestination = TabItem.Scan.route,
                enterTransition = {
                    fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    )
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(300)) + slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    )
                },
                popEnterTransition = {
                    fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(300)
                    )
                },
                popExitTransition = {
                    fadeOut(animationSpec = tween(300)) + slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(300)
                    )
                }
            ) {
                composable(TabItem.Scan.route) {
                    ScanScreen(
                        onGalleryClick = { /* Handled internally */ },
                        onPermissionRequest = { /* Handled internally */ }
                    )
                }
                
                composable(TabItem.History.route) {
                    HistoryScreen(
                        storage = historyStorage,
                        onNavigateToScan = {
                            selectedTab = TabItem.Scan.route
                            navController.navigate(TabItem.Scan.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
                
                composable(TabItem.Generate.route) {
                    GenerateScreen()
                }
            }
        }
    }
}
