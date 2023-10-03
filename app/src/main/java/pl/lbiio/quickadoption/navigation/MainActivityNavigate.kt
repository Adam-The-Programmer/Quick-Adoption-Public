package pl.lbiio.quickadoption.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pl.lbiio.quickadoption.ApplyingAnnouncementForm
import pl.lbiio.quickadoption.ChatConsole
import pl.lbiio.quickadoption.ChatsScreen
import pl.lbiio.quickadoption.TabbedAnnouncementsScreen
import pl.lbiio.quickadoption.models.ApplyAnnouncementViewModel
import pl.lbiio.quickadoption.models.ChatConsoleViewModel
import pl.lbiio.quickadoption.models.ChatsListViewModel
import pl.lbiio.quickadoption.models.TabbedAnnouncementsViewModel

@Composable
fun MainActivityNavigate() {
    val navController = rememberNavController()
    val tabbedAnnouncementsViewModel: TabbedAnnouncementsViewModel = viewModel()
    val applyAnnouncementViewModel: ApplyAnnouncementViewModel = viewModel()
    val chatsListViewModel: ChatsListViewModel = viewModel()
    val chatConsoleViewModel: ChatConsoleViewModel = viewModel()
    tabbedAnnouncementsViewModel.initNavController(navController)
    applyAnnouncementViewModel.initNavController(navController)
    chatsListViewModel.initNavController(navController)
    chatConsoleViewModel.initNavController(navController)
    NavHost(navController = navController, startDestination = "start") {
        composable("start") {
            TabbedAnnouncementsScreen(tabbedAnnouncementsViewModel)
        }
        composable("add"){
            ApplyingAnnouncementForm(applyAnnouncementViewModel)
        }
        composable("chats/{announcementId}",
            arguments = listOf(
                navArgument("announcementId"){type= NavType.LongType}
            )
        ){backStackEntry ->
            val announcementId = backStackEntry.arguments?.getLong("announcementId")
            if(announcementId != null){
                chatsListViewModel.announcementId.value = announcementId
                ChatsScreen(chatsListViewModel)
            }
        }
        composable("chat/{chatId}",
            arguments = listOf(
                navArgument("chatId"){type= NavType.LongType}
            )
        ){backStackEntry ->
            val chatId = backStackEntry.arguments?.getLong("chatId")
            if(chatId != null){
                chatConsoleViewModel.chatId.value = chatId
                ChatConsole(chatConsoleViewModel)
            }
        }
    }
}