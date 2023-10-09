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
import pl.lbiio.quickadoption.PublicAnnouncementDetailScreen
import pl.lbiio.quickadoption.PublicAnnouncementsChatsScreen
import pl.lbiio.quickadoption.TabbedAnnouncementsScreen
import pl.lbiio.quickadoption.models.ApplyAnnouncementViewModel
import pl.lbiio.quickadoption.models.ChatConsoleViewModel
import pl.lbiio.quickadoption.models.OwnChatsListViewModel
import pl.lbiio.quickadoption.models.PublicChatsListViewModel
import pl.lbiio.quickadoption.models.TabbedAnnouncementsViewModel

@Composable
fun MainActivityNavigate() {
    val navController = rememberNavController()
    val tabbedAnnouncementsViewModel: TabbedAnnouncementsViewModel = viewModel()
    val applyAnnouncementViewModel: ApplyAnnouncementViewModel = viewModel()
    val ownChatsListViewModel: OwnChatsListViewModel = viewModel()
    val publicChatsListViewModel: PublicChatsListViewModel = viewModel()
    val chatConsoleViewModel: ChatConsoleViewModel = viewModel()
    tabbedAnnouncementsViewModel.initNavController(navController)
    applyAnnouncementViewModel.initNavController(navController)
    ownChatsListViewModel.initNavController(navController)
    chatConsoleViewModel.initNavController(navController)
    publicChatsListViewModel.initNavController(navController)
    NavHost(navController = navController, startDestination = "start") {
        composable("start") {
            TabbedAnnouncementsScreen(tabbedAnnouncementsViewModel)
        }
        composable("announcementForm"){
            applyAnnouncementViewModel.clearViewModel()
            ApplyingAnnouncementForm(applyAnnouncementViewModel)
        }
        composable("announcementForm/{animalId}/{name}/{species}/{breed}/{dateRange}/{food}/{artwork}",
            arguments = listOf(
                navArgument("animalId"){type= NavType.LongType},
                navArgument("name"){type= NavType.StringType},
                navArgument("species"){type= NavType.StringType},
                navArgument("breed"){type= NavType.StringType},
                navArgument("dateRange"){type= NavType.StringType},
                navArgument("food"){type= NavType.StringType},
                navArgument("artwork"){type= NavType.StringType}
            )
        ){backStackEntry ->
            val animalId = backStackEntry.arguments?.getLong("animalId")
            val name = backStackEntry.arguments?.getString("name")
            val species = backStackEntry.arguments?.getString("species")
            val breed = backStackEntry.arguments?.getString("breed")
            val dateRange = backStackEntry.arguments?.getString("dateRange")
            val food = backStackEntry.arguments?.getString("food")
            val artwork = backStackEntry.arguments?.getString("artwork")
            if(animalId != null && name!=null && species!=null && breed!=null && dateRange!=null && food!=null && artwork!=null){
                applyAnnouncementViewModel.animal_name.value = name
                applyAnnouncementViewModel.breed.value = breed
                applyAnnouncementViewModel.species.value = species
                applyAnnouncementViewModel.date.value = dateRange
                applyAnnouncementViewModel.food.value = food
                applyAnnouncementViewModel.animal_image.value = artwork
                ApplyingAnnouncementForm(applyAnnouncementViewModel)
            }
        }
        composable("chats/{announcementId}/{name}",
            arguments = listOf(
                navArgument("announcementId"){type= NavType.LongType},
                navArgument("name"){type= NavType.StringType}
            )
        ){backStackEntry ->
            val announcementId = backStackEntry.arguments?.getLong("announcementId")
            val name = backStackEntry.arguments?.getString("name")
            if(announcementId != null && name != null){
                ownChatsListViewModel.announcementId.value = announcementId
                ownChatsListViewModel.animalName.value = name
                ChatsScreen(ownChatsListViewModel)
            }
        }
        composable("publicAnnouncementsChats"){
            PublicAnnouncementsChatsScreen(publicChatsListViewModel)
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
        composable("publicOffer/{animalId}",
            arguments = listOf(
                navArgument("animalId"){type= NavType.LongType}
            )
        ){backStackEntry ->
            val animalId = backStackEntry.arguments?.getLong("animalId")
            if(animalId != null){
                PublicAnnouncementDetailScreen(animalId)
            }
        }
    }
}