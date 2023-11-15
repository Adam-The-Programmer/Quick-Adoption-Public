package pl.lbiio.quickadoption.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import pl.lbiio.quickadoption.QuickAdoptionApp
import pl.lbiio.quickadoption.data.OwnAnnouncementChat
import pl.lbiio.quickadoption.data.OwnAnnouncementListItem
import pl.lbiio.quickadoption.navigation.AppNavigator
import pl.lbiio.quickadoption.navigation.Destination
import pl.lbiio.quickadoption.repositories.OwnChatsListRepository
import javax.inject.Inject

@HiltViewModel
class OwnChatsListViewModel @Inject constructor(private val ownChatsListRepository: OwnChatsListRepository) :
    ViewModel() {
    private val disposables = CompositeDisposable()
    private var appNavigator: AppNavigator? = null
    //private var navController: NavController? = null

    val announcementId: MutableState<Long> = mutableLongStateOf(-1L)
    val animalName: MutableState<String> = mutableStateOf("")
    val ownChats: MutableState<List<OwnAnnouncementChat>> = mutableStateOf(emptyList())
    val isFinished: MutableState<Boolean> = mutableStateOf(true)


    fun initAppNavigator(appNavigator: AppNavigator){
        this.appNavigator = appNavigator
    }
//    fun initNavController(navController: NavController) {
//        this.navController = navController
//    }

    fun navigateUp(){
        appNavigator?.tryNavigateBack()
    }

    fun navigateToChat(chatId: String){
        //navController?.navigate("chat/${chatId}")
        appNavigator?.tryNavigateTo(Destination.ChatConsoleScreen(
            chatId = chatId,
            isChatOwn = true
        ))
    }

    fun clearViewModel(){
        announcementId.value = -1L
        animalName.value = ""
    }

    private fun getOwnChatsForAnnouncement(onSuccess: (List<OwnAnnouncementChat>) -> Unit, onError: (Throwable) -> Unit){
        val disposable = ownChatsListRepository.getOwnChatsForAnnouncement(QuickAdoptionApp.getCurrentUserId()!!, announcementId.value)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { chats -> onSuccess(chats) },
                { error -> onError(error) }
            )
        disposables.add(disposable)
    }

    fun fillListOfChats(){
        isFinished.value = false
        getOwnChatsForAnnouncement(
            onSuccess = {chatsList ->
                ownChats.value = chatsList.toMutableList()
                isFinished.value = true
            },
            onError = {
                isFinished.value = true
            }
        )
    }
}