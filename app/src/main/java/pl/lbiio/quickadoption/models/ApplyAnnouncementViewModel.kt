package pl.lbiio.quickadoption.models

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ApplyAnnouncementViewModel @Inject constructor() :
    ViewModel(){
    private var navController: NavController? = null
    val species: MutableState<String> = mutableStateOf("")
    val breed: MutableState<String> = mutableStateOf("")
    val animal_name: MutableState<String> = mutableStateOf("")
    val date: MutableState<String> = mutableStateOf("")
    val food: MutableState<String> = mutableStateOf("")
    val animal_image: MutableState<String> = mutableStateOf("")

    fun initNavController(navController: NavController) {
        this.navController = navController
    }

//    fun navigateToRegistrationForm() {
//        navController?.navigate("register")
//    }

    fun navigateUp(){
        navController?.navigateUp()
    }

}