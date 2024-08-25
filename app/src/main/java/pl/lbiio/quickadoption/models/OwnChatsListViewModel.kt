package pl.lbiio.quickadoption.models

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import pl.lbiio.quickadoption.QuickAdoptionApp
import pl.lbiio.quickadoption.data.OwnAnnouncementChat
import pl.lbiio.quickadoption.navigation.AppNavigator
import pl.lbiio.quickadoption.navigation.Destination
import pl.lbiio.quickadoption.repositories.InternetAccessRepository
import pl.lbiio.quickadoption.repositories.OwnChatsListRepository
import javax.inject.Inject

@HiltViewModel
class OwnChatsListViewModel @Inject constructor(private val ownChatsListRepository: OwnChatsListRepository, private val internetAccessRepository: InternetAccessRepository) :
    ViewModel() {
    private val disposables = CompositeDisposable()
    private var appNavigator: AppNavigator? = null
    val announcementId: MutableState<Long> = mutableLongStateOf(-1L)
    val animalName: MutableState<String> = mutableStateOf("")
    val ownChats: MutableState<List<OwnAnnouncementChat>> = mutableStateOf(emptyList())
    val isFinished: MutableState<Boolean> = mutableStateOf(true)


    fun initAppNavigator(appNavigator: AppNavigator){
        this.appNavigator = appNavigator
    }

    fun navigateUp(){
        viewModelScope.launch {
            appNavigator?.tryNavigateBack()
            clearViewModel()
        }
    }

    fun navigateToChat(chatId: String){
        viewModelScope.launch {
            appNavigator?.tryNavigateTo(Destination.ChatConsoleScreen(
                chatId = chatId,
                isChatOwn = true,
                partnerName = ownChats.value.find { it.chatID==chatId }!!.name,
                partnerImage = QuickAdoptionApp.encodePathFile(ownChats.value.find { it.chatID==chatId }!!.profileImage),
                partnerUID = ownChats.value.find { it.chatID==chatId }!!.potentialKeeperID,
                announcementId = announcementId.value
            ))
        }
    }

    fun clearViewModel(){
        viewModelScope.launch {
            announcementId.value = -1L
            animalName.value = ""
            ownChats.value = emptyList()
            isFinished.value = true
            disposables.clear()
        }
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
    private fun setAnnouncementDontHaveUnreadMessage(onComplete: () -> Unit, onError: (Throwable) -> Unit){
        val disposable = ownChatsListRepository.setAnnouncementDontHaveUnreadMessage(announcementId.value)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { onComplete() },
                { error -> onError(error) }
            )
        disposables.add(disposable)
    }

    fun fillListOfChats(handleInternetError:()->Unit){
        viewModelScope.launch {
            isFinished.value = false
            if(internetAccessRepository.isInternetAvailable()){
                getOwnChatsForAnnouncement(
                    onSuccess = {chatsList ->
                        ownChats.value = chatsList.toMutableList()
                        isFinished.value = true
                    },
                    onError = {
                        Log.d("getOwnChatsForAnnouncement error", it.toString())
                        isFinished.value = true
                    }
                )
                isFinished.value = false
                setAnnouncementDontHaveUnreadMessage({
                    isFinished.value = true
                }, {
                    Log.d("setAnnouncementDontHaveUnreadMessage error", it.toString())
                    isFinished.value = true
                })
            }else{
                isFinished.value = true
                Log.e("isInternetAvailable", "no!")
                handleInternetError()
            }

        }
    }
}