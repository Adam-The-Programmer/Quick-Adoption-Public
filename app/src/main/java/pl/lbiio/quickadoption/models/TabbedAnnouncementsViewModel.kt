package pl.lbiio.quickadoption.models

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.lbiio.quickadoption.data.Announcement
import javax.inject.Inject

@HiltViewModel
class TabbedAnnouncementsViewModel @Inject constructor() :
    ViewModel(){
    private var navController: NavController? = null

    fun initNavController(navController: NavController) {
        this.navController = navController
    }

    fun navigateToInsertingForm() {
        navController?.navigate("add")
    }

    fun navigateToChatsList(announcementId: Long) {
        navController?.navigate("chats/$announcementId")
    }

    fun navigateToEditingForm(announcement: Announcement){
        navController?.navigate("chats/${announcement.announcementId}/${announcement.name}/${announcement.species}/${announcement.breed}/${announcement.dateRange}/${announcement.food}/${announcement.artwork}")
    }

}