package pl.lbiio.quickadoption.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OwnChatsListViewModel @Inject constructor() :
    ViewModel() {
    private var navController: NavController? = null

    val announcementId: MutableState<Long> = mutableLongStateOf(-1L)
    val animalName: MutableState<String> = mutableStateOf("")

    fun initNavController(navController: NavController) {
        this.navController = navController
    }

    fun navigateUp(){
        navController?.navigateUp()
    }

    fun navigateToChat(chatId: Long){
        navController?.navigate("chat/${chatId}")
    }

    fun clearViewModel(){
        announcementId.value = -1L
        animalName.value = ""
    }
}