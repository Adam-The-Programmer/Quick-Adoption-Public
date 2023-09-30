package pl.lbiio.quickadoption.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.lbiio.quickadoption.ApplyingAnnouncementForm
import pl.lbiio.quickadoption.TabbedAnnouncementsScreen
import pl.lbiio.quickadoption.models.ApplyAnnouncementViewModel
import pl.lbiio.quickadoption.models.TabbedAnnouncementsViewModel

@Composable
fun MainActivityNavigate() {
    val navController = rememberNavController()
    val tabbedAnnouncementsViewModel: TabbedAnnouncementsViewModel = viewModel()
    val applyAnnouncementViewModel: ApplyAnnouncementViewModel = viewModel()
    tabbedAnnouncementsViewModel.initNavController(navController)
    applyAnnouncementViewModel.initNavController(navController)
    NavHost(navController = navController, startDestination = "start") {
        composable("start") {
            TabbedAnnouncementsScreen(tabbedAnnouncementsViewModel)
        }
        composable("add"){
            ApplyingAnnouncementForm(applyAnnouncementViewModel)
        }
    }
}