package pl.lbiio.quickadoption.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.lbiio.quickadoption.data.OwnAnnouncement
import pl.lbiio.quickadoption.data.PublicAnnouncement
import javax.inject.Inject

@HiltViewModel
class TabbedAnnouncementsViewModel @Inject constructor() :
    ViewModel(){
    private var navController: NavController? = null

    val country: MutableState<String> = mutableStateOf("")
    val city: MutableState<String> = mutableStateOf("")
    val dateRange: MutableState<String> = mutableStateOf("")
    fun initNavController(navController: NavController) {
        this.navController = navController
    }

    fun navigateToInsertingForm() {
        navController?.navigate("announcementForm")
    }

    fun navigateToChatsList(announcementId: Long, name: String) {
        navController?.navigate("chats/$announcementId/$name")
    }

    fun navigateToEditingForm(ownAnnouncement: OwnAnnouncement){
        navController?.navigate("announcementForm/${ownAnnouncement.animalId}/${ownAnnouncement.name}/${ownAnnouncement.species}/${ownAnnouncement.breed}/${ownAnnouncement.dateRange}/${ownAnnouncement.food}/${ownAnnouncement.artwork}")
    }

    fun search(){

    }

    fun navigateToPublicOffer(animalId: Long){
        navController?.navigate("publicOffer/${animalId}")
    }

    fun navigateToPublicAnnouncementsChats(){
        navController?.navigate("publicAnnouncementsChats")
    }

    fun clearViewModel(){
        country.value = ""
        city.value = ""
        dateRange.value = ""
    }

}