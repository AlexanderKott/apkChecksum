package com.kotikov.technicalTask.forDrWeb.ui.theme.compose.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.AppCardScreen.AppCardScreen
import com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.AuthorScreen
import com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.HashInfoScreen
import com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.SplashScreen
import com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.WorkAreaScreen.WorkAreaScreen


@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen {
                navController.navigate(Screen.WorkArea.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
        }
        composable(Screen.WorkArea.route) {
            WorkAreaScreen(
                onAppClick = { packageName ->
                    navController.navigate(Screen.AppCard.createRoute(packageName))
                },
                onAuthorClick = {
                    navController.navigate(Screen.Author.route)
                },

                onHashClick = { packageName ->
                    navController.navigate(Screen.HashInfo.createRoute(packageName))
                }
            )
        }
        composable(
            route = Screen.AppCard.route,
            arguments = listOf(
                navArgument("packageName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            AppCardScreen(navController)
        }
        composable(Screen.Author.route) {
            AuthorScreen(navController)
        }

        composable(
            Screen.HashInfo.route,
            arguments = listOf(
                navArgument("packageName") { type = NavType.StringType }
            )
        ) {
            HashInfoScreen(navController)
        }
    }
}