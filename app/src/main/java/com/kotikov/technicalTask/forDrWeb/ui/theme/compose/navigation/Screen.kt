package com.kotikov.technicalTask.forDrWeb.ui.theme.compose.navigation


sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object WorkArea : Screen("workarea")
    object Author : Screen("author")
    object HashInfo : Screen("hashInfo/{packageName}") {
        fun createRoute(packageName: String) = "hashInfo/$packageName"
    }

    object AppCard : Screen("appcard/{packageName}") {
        fun createRoute(packageName: String) = "appcard/$packageName"
    }
}