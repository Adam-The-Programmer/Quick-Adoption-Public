package pl.lbiio.quickadoption.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import pl.lbiio.quickadoption.ApplyingAnnouncementForm
import pl.lbiio.quickadoption.ChatConsole
import pl.lbiio.quickadoption.OwnAnnouncementChatsScreen
import pl.lbiio.quickadoption.PublicAnnouncementDetailScreen
import pl.lbiio.quickadoption.PublicAnnouncementsChatsScreen
import pl.lbiio.quickadoption.TabbedAnnouncementsScreen
import pl.lbiio.quickadoption.models.ApplyAnnouncementViewModel
import pl.lbiio.quickadoption.models.ChatConsoleViewModel
import pl.lbiio.quickadoption.models.OwnChatsListViewModel
import pl.lbiio.quickadoption.models.PublicAnnouncementDetailsViewModel
import pl.lbiio.quickadoption.models.PublicChatsListViewModel
import pl.lbiio.quickadoption.models.TabbedAnnouncementsViewModel

@Composable
fun MainActivityNavigate() {

    val navController = rememberNavController()
    val appNavigator = AppNavigatorImpl()
    val tabbedAnnouncementsViewModel: TabbedAnnouncementsViewModel = hiltViewModel()
    tabbedAnnouncementsViewModel.initAppNavigator(appNavigator)
    val applyAnnouncementViewModel: ApplyAnnouncementViewModel = hiltViewModel()
    applyAnnouncementViewModel.initAppNavigator(appNavigator)
    val ownChatsListViewModel: OwnChatsListViewModel = hiltViewModel()
    ownChatsListViewModel.initAppNavigator(appNavigator)
    val publicChatsListViewModel: PublicChatsListViewModel = hiltViewModel()
    publicChatsListViewModel.initAppNavigator(appNavigator)
    val publicAnnouncementDetailsViewModel: PublicAnnouncementDetailsViewModel = hiltViewModel()
    publicAnnouncementDetailsViewModel.initAppNavigator(appNavigator)
    val chatConsoleViewModel: ChatConsoleViewModel = hiltViewModel()
    chatConsoleViewModel.initAppNavigator(appNavigator)
    NavigationEffects(
        navigationChannel = appNavigator.navigationChannel,
        navHostController = navController
    )
    NavHost(
        navController = navController,
        startDestination = Destination.TabbedScreen
    ) {
        composable(destination = Destination.TabbedScreen) {
            TabbedAnnouncementsScreen(tabbedAnnouncementsViewModel)
        }
        composable(destination = Destination.AnnouncementFormScreen) {
            applyAnnouncementViewModel.clearViewModel()
            ApplyingAnnouncementForm(applyAnnouncementViewModel)
        }
        composable(destination = Destination.AnnouncementEditScreen) { backStackEntry ->
            //applyAnnouncementViewModel.clearViewModel()
            backStackEntry.arguments?.apply {
                val announcementId = getString(Destination.AnnouncementEditScreen.ANNOUNCEMENT_ID_KEY)
                applyAnnouncementViewModel.announcementId.value = announcementId!!.toLong()
//                LaunchedEffect(Unit){
//                    applyAnnouncementViewModel.getAnnouncementById()
//                }
//                if(applyAnnouncementViewModel.isFinished.value){
//
//                }
//                Log.d("announcement.species Nav", applyAnnouncementViewModel.species.value)
//                Log.d("announcement.breed", applyAnnouncementViewModel.breed.value)
//                Log.d("announcement.animalName", applyAnnouncementViewModel.animalName.value)
//                Log.d("announcement.dateRange", applyAnnouncementViewModel.dateRange.value)
//                Log.d("announcement.food", applyAnnouncementViewModel.food.value)
//                Log.d("announcement.animalImage", applyAnnouncementViewModel.animalImage.value)
//                Log.d("announcement.animalDescription", applyAnnouncementViewModel.animalDescription.value)
                ApplyingAnnouncementForm(applyAnnouncementViewModel)
            }
        }

        composable(destination = Destination.ChatsScreen) { backStackEntry ->
            ownChatsListViewModel.clearViewModel()
            backStackEntry.arguments?.apply {
                val announcementId = getString(Destination.ChatsScreen.ANNOUNCEMENT_ID_KEY)
                val animalName = getString(Destination.ChatsScreen.ANIMAL_NAME_KEY)
                val nullables = listOf(announcementId, animalName)
                if(nullables.all { it != null }){
                    ownChatsListViewModel.apply {
                        this.announcementId.value = announcementId!!.toLong()
                        this.animalName.value = animalName!!
                    }
                    OwnAnnouncementChatsScreen(ownChatsListViewModel)
                }
            }
        }
        composable(destination = Destination.PublicAnnouncementsChatsScreen) {
            PublicAnnouncementsChatsScreen(publicChatsListViewModel)
        }
        composable(destination = Destination.ChatConsoleScreen) { backStackEntry ->
            chatConsoleViewModel.clearViewModel()
            backStackEntry.arguments?.apply {
                val chatId = getString(Destination.ChatConsoleScreen.CHAT_ID_KEY)
                val isChatOwn = getString(Destination.ChatConsoleScreen.IS_CHAT_OWN_KEY)
                val nullables = listOf(chatId, isChatOwn)
                if(nullables.all { it != null }){
                    chatConsoleViewModel.chatId.value = chatId!!
                    chatConsoleViewModel.isChatOwn.value = isChatOwn.toBoolean()
                    //Log.d("wartosc", isChatOwn.toBoolean().toString())
                    chatConsoleViewModel.initValues()
                    ChatConsole(chatConsoleViewModel)
                }
            }
        }
        composable(destination = Destination.PublicOfferDetailsScreen) {backStackEntry ->
            publicAnnouncementDetailsViewModel.clearViewModel()
            backStackEntry.arguments?.apply {
                val announcementId = getString(Destination.PublicOfferDetailsScreen.ANNOUNCEMENT_ID_KEY)
                publicAnnouncementDetailsViewModel.announcementID.value = announcementId!!.toLong()
                publicAnnouncementDetailsViewModel.initValues()
                PublicAnnouncementDetailScreen(publicAnnouncementDetailsViewModel)
            }

        }
    }

//    val navController = rememberNavController()
//    val tabbedAnnouncementsViewModel: TabbedAnnouncementsViewModel = viewModel()
//    val applyAnnouncementViewModel: ApplyAnnouncementViewModel = viewModel()
//    val ownChatsListViewModel: OwnChatsListViewModel = viewModel()
//    val publicChatsListViewModel: PublicChatsListViewModel = viewModel()
//    val chatConsoleViewModel: ChatConsoleViewModel = viewModel()
//    val publicAnnouncementDetailsViewModel: PublicAnnouncementDetailsViewModel = viewModel()
//    tabbedAnnouncementsViewModel.initNavController(navController)
//    applyAnnouncementViewModel.initNavController(navController)
//    ownChatsListViewModel.initNavController(navController)
//    chatConsoleViewModel.initNavController(navController)
//    publicChatsListViewModel.initNavController(navController)
//    publicAnnouncementDetailsViewModel.initNavController(navController)
//    NavHost(navController = navController, startDestination = "start") {
//        composable("start") {
//            TabbedAnnouncementsScreen(tabbedAnnouncementsViewModel)
//        }
//        composable("announcementForm"){
//            applyAnnouncementViewModel.clearViewModel()
//            ApplyingAnnouncementForm(applyAnnouncementViewModel)
//        }
//        composable("announcementForm/{animalId}/{name}/{species}/{breed}/{dateRange}/{food}/{artwork}",
//            arguments = listOf(
//                navArgument("animalId"){type= NavType.LongType},
//                navArgument("name"){type= NavType.StringType},
//                navArgument("species"){type= NavType.StringType},
//                navArgument("breed"){type= NavType.StringType},
//                navArgument("dateRange"){type= NavType.StringType},
//                navArgument("food"){type= NavType.StringType},
//                navArgument("artwork"){type= NavType.StringType}
//            )
//        ){backStackEntry ->
//            val animalId = backStackEntry.arguments?.getLong("animalId")
//            val name = backStackEntry.arguments?.getString("name")
//            val species = backStackEntry.arguments?.getString("species")
//            val breed = backStackEntry.arguments?.getString("breed")
//            val dateRange = backStackEntry.arguments?.getString("dateRange")
//            val food = backStackEntry.arguments?.getString("food")
//            val artwork = backStackEntry.arguments?.getString("artwork")
//            if(animalId != null && name!=null && species!=null && breed!=null && dateRange!=null && food!=null && artwork!=null){
//                applyAnnouncementViewModel.animalName.value = name
//                applyAnnouncementViewModel.breed.value = breed
//                applyAnnouncementViewModel.species.value = species
//                applyAnnouncementViewModel.date.value = dateRange
//                applyAnnouncementViewModel.food.value = food
//                applyAnnouncementViewModel.animalImage.value = artwork
//                ApplyingAnnouncementForm(applyAnnouncementViewModel)
//            }
//        }
//        composable("chats/{announcementId}/{name}",
//            arguments = listOf(
//                navArgument("announcementId"){type= NavType.LongType},
//                navArgument("name"){type= NavType.StringType}
//            )
//        ){backStackEntry ->
//            val announcementId = backStackEntry.arguments?.getLong("announcementId")
//            val name = backStackEntry.arguments?.getString("name")
//            if(announcementId != null && name != null){
//                ownChatsListViewModel.announcementId.value = announcementId
//                ownChatsListViewModel.animalName.value = name
//                ChatsScreen(ownChatsListViewModel)
//            }
//        }
//        composable("publicAnnouncementsChats"){
//            PublicAnnouncementsChatsScreen(publicChatsListViewModel)
//        }
//        composable("chat/{chatId}",
//            arguments = listOf(
//                navArgument("chatId"){type= NavType.LongType}
//            )
//        ){backStackEntry ->
//            val chatId = backStackEntry.arguments?.getLong("chatId")
//            if(chatId != null){
//                chatConsoleViewModel.chatId.value = chatId
//                chatConsoleViewModel.initValues()
//                ChatConsole(chatConsoleViewModel)
//            }
//        }
//        composable("publicOffer/{animalId}",
//            arguments = listOf(
//                navArgument("animalId"){type= NavType.LongType}
//            )
//        ){backStackEntry ->
//            val animalId = backStackEntry.arguments?.getLong("animalId")
//            if(animalId != null){
//                publicAnnouncementDetailsViewModel.announcementID.value = animalId
//                publicAnnouncementDetailsViewModel.initValues()
//                PublicAnnouncementDetailScreen(publicAnnouncementDetailsViewModel)
//            }
//        }
//    }


}

//@Composable
//private fun NavigationEffects(
//    navigationChannel: Channel<NavigationIntent>,
//    navHostController: NavHostController
//) {
//    val activity = (LocalContext.current as? Activity)
//    LaunchedEffect(activity, navHostController, navigationChannel) {
//        navigationChannel.receiveAsFlow().collect { intent ->
//            if (activity?.isFinishing == true) {
//                return@collect
//            }
//            when (intent) {
//                is NavigationIntent.NavigateBack -> {
//                    if (intent.route != null) {
//                        navHostController.popBackStack(intent.route, intent.inclusive)
//                    } else {
//                        navHostController.popBackStack()
//                    }
//                }
//
//                is NavigationIntent.NavigateTo -> {
//                    navHostController.navigate(intent.route) {
//                        launchSingleTop = intent.isSingleTop
//                        intent.popUpToRoute?.let { popUpToRoute ->
//                            popUpTo(popUpToRoute) { inclusive = intent.inclusive }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}