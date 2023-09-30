package pl.lbiio.quickadoption.models

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
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

}