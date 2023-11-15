package pl.lbiio.quickadoption.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.lbiio.quickadoption.data.ChatMessage
import pl.lbiio.quickadoption.navigation.AppNavigator
import javax.inject.Inject

@HiltViewModel
class ChatConsoleViewModel @Inject constructor() :
    ViewModel() {
    private var appNavigator: AppNavigator? = null
   // private var navController: NavController? = null
    val chatId: MutableState<String> = mutableStateOf("")
    val isChatOwn: MutableState<Boolean> = mutableStateOf(true)
    val potentialKeeperImage: MutableState<String> = mutableStateOf("")
    val potentialKeeperName: MutableState<String> = mutableStateOf("")
    val conversation: MutableState<List<ChatMessage>> = mutableStateOf(emptyList())


    fun initAppNavigator(appNavigator: AppNavigator){
        this.appNavigator = appNavigator
    }
//    fun initNavController(navController: NavController) {
//        this.navController = navController
//    }

    fun initValues(){
        conversation.value = listOf(
            ChatMessage("g903r93rn9863", "Hello Adam", "text", 1696174281515L),
            ChatMessage("6t8b9ae639113", "Hi there!", "text", 1696174281550L),
            ChatMessage("g903r93rn9863", "https://bi.im-g.pl/im/52/f5/1b/z29318482Q,WCup-World-Cup-Photo-Gallery.jpg", "image", 1696174281590L)
        )
        potentialKeeperImage.value = "https://bi.im-g.pl/im/52/f5/1b/z29318482Q,WCup-World-Cup-Photo-Gallery.jpg"
        potentialKeeperName.value = "Christiano"
    }

    fun navigateUp() {
        //navController?.navigateUp()
        appNavigator?.tryNavigateBack()
    }

    fun clearViewModel(){
        chatId.value = ""
        isChatOwn.value = true
        potentialKeeperImage.value = ""
        conversation.value = emptyList()
    }

    fun navigateToOpinions() {

    }
}