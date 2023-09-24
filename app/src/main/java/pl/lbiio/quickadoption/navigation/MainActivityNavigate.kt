package pl.lbiio.quickadoption.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.lbiio.quickadoption.TabbedAnnouncementsScreen

@Composable
fun MainActivityNavigate() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "start") {
        composable("start") {
            TabbedAnnouncementsScreen()
        }
    }
}