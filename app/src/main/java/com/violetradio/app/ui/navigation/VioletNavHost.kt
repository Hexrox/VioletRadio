package com.violetradio.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.violetradio.app.ui.screens.browse.BrowseScreen
import com.violetradio.app.ui.screens.home.HomeScreen
import com.violetradio.app.ui.screens.player.PlayerScreen
import com.violetradio.app.ui.screens.settings.SettingsScreen

/**
 * Main navigation host for the app
 */
@Composable
fun VioletNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.HOME,
        modifier = modifier
    ) {
        composable(NavigationRoutes.HOME) {
            HomeScreen(
                onNavigateToPlayer = {
                    navController.navigate(NavigationRoutes.PLAYER)
                },
                onNavigateToSearch = {
                    navController.navigate(NavigationRoutes.BROWSE)
                }
            )
        }

        composable(NavigationRoutes.PLAYER) {
            PlayerScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(NavigationRoutes.BROWSE) {
            BrowseScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(NavigationRoutes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
