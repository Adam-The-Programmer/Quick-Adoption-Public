package pl.lbiio.quickadoption.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.lbiio.quickadoption.navigation.AppNavigator
import pl.lbiio.quickadoption.navigation.Destination
import javax.inject.Inject

@HiltViewModel
class PublicChatsListViewModel @Inject constructor() :
    ViewModel() {
    private var appNavigator: AppNavigator? = null
    //private var navController: NavController? = null


    fun initAppNavigator(appNavigator: AppNavigator){
        this.appNavigator = appNavigator
    }

    fun navigateUp(){
        appNavigator?.tryNavigateBack()
    }

    fun navigateToChat(chatId: String){
        appNavigator?.tryNavigateTo(Destination.ChatConsoleScreen(
            chatId = chatId,
            isChatOwn = false
        ))
    }
}