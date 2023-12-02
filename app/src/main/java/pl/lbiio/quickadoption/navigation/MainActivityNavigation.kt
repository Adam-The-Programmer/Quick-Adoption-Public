package pl.lbiio.quickadoption.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import pl.lbiio.quickadoption.ApplyingAnnouncementForm
import pl.lbiio.quickadoption.ChatConsole
import pl.lbiio.quickadoption.OpinionsScreen
import pl.lbiio.quickadoption.OwnAnnouncementChatsScreen
import pl.lbiio.quickadoption.PublicAnnouncementDetailScreen
import pl.lbiio.quickadoption.PublicAnnouncementsChatsScreen
import pl.lbiio.quickadoption.QuickAdoptionApp
import pl.lbiio.quickadoption.TabbedAnnouncementsScreen
import pl.lbiio.quickadoption.models.ApplyAnnouncementViewModel
import pl.lbiio.quickadoption.models.ChatConsoleViewModel
import pl.lbiio.quickadoption.models.OpinionsViewModel
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
    val opinionsViewModel: OpinionsViewModel = hiltViewModel()
    opinionsViewModel.initAppNavigator(appNavigator)
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
                ApplyingAnnouncementForm(applyAnnouncementViewModel)
            }
        }

        composable(destination = Destination.ChatsScreen) { backStackEntry ->
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
            //chatConsoleViewModel.clearViewModel()
            backStackEntry.arguments?.apply {
                val chatId = getString(Destination.ChatConsoleScreen.CHAT_ID_KEY)
                val announcementId = getString(Destination.ChatConsoleScreen.ANNOUNCEMENT_ID_KEY)
                val isChatOwn = getString(Destination.ChatConsoleScreen.IS_CHAT_OWN_KEY)
                val partnerName = getString(Destination.ChatConsoleScreen.PARTNER_NAME_KEY)
                val partnerImage = getString(Destination.ChatConsoleScreen.PARTNER_IMAGE_KEY)
                val partnerUID = getString(Destination.ChatConsoleScreen.PARTNER_UID_KEY)
                val nullables = listOf(chatId, announcementId, isChatOwn, partnerName, partnerImage, partnerUID)
                if(nullables.all { it != null }){
                    chatConsoleViewModel.chatId.value = chatId.toString()
                    chatConsoleViewModel.isChatOwn.value = isChatOwn.toBoolean()
                    chatConsoleViewModel.potentialKeeperName.value = partnerName.toString()
                    chatConsoleViewModel.potentialKeeperImage.value = QuickAdoptionApp.decodePathFile(partnerImage.toString())
                    Log.d("profile image view model", chatConsoleViewModel.potentialKeeperImage.value)
                    chatConsoleViewModel.potentialKeeperUID.value = partnerUID.toString()
                    chatConsoleViewModel.announcementId.value = announcementId.toString().toLong()
                    //Log.d("wartosc", isChatOwn.toBoolean().toString())
                    //chatConsoleViewModel.initValues()
                    LaunchedEffect(Unit){
                        chatConsoleViewModel.listenToMessages()
                    }
                    ChatConsole(chatConsoleViewModel)
                }
            }
        }
        composable(destination = Destination.PublicOfferDetailsScreen) {backStackEntry ->
            //publicAnnouncementDetailsViewModel.clearViewModel()
            backStackEntry.arguments?.apply {
                val announcementId = getString(Destination.PublicOfferDetailsScreen.ANNOUNCEMENT_ID_KEY)
                publicAnnouncementDetailsViewModel.announcementID.value = announcementId!!.toLong()
                PublicAnnouncementDetailScreen(publicAnnouncementDetailsViewModel)
            }

        }

        composable(destination = Destination.OpinionsScreen) {backStackEntry ->
            backStackEntry.arguments?.apply {
                val receiverID = getString(Destination.OpinionsScreen.UID_KEY)
                opinionsViewModel.receiverID.value = receiverID!!
                OpinionsScreen(opinionsViewModel)
            }

        }
    }
}
