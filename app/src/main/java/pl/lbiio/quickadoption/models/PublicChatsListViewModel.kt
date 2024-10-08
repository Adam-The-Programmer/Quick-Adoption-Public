package pl.lbiio.quickadoption.models

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import pl.lbiio.quickadoption.QuickAdoptionApp
import pl.lbiio.quickadoption.data.PublicAnnouncementChat
import pl.lbiio.quickadoption.navigation.AppNavigator
import pl.lbiio.quickadoption.navigation.Destination
import pl.lbiio.quickadoption.repositories.InternetAccessRepository
import pl.lbiio.quickadoption.repositories.PublicChatsListRepository
import javax.inject.Inject

@HiltViewModel
class PublicChatsListViewModel @Inject constructor(private val publicChatsListRepository: PublicChatsListRepository, private val internetAccessRepository: InternetAccessRepository) :
    ViewModel() {
    private var appNavigator: AppNavigator? = null
    private val disposables = CompositeDisposable()

    val isFinished: MutableState<Boolean> = mutableStateOf(true)
    val publicChats: MutableState<List<PublicAnnouncementChat>> = mutableStateOf(emptyList())


    fun initAppNavigator(appNavigator: AppNavigator) {
        this.appNavigator = appNavigator
    }

    fun navigateUp() {
        viewModelScope.launch {
            appNavigator?.tryNavigateBack()
            clearViewModel()
        }
    }

    fun clearViewModel(){
        viewModelScope.launch {
            isFinished.value = true
            publicChats.value = emptyList()
            disposables.clear()
        }
    }

    fun navigateToChat(chatId: String) {
        viewModelScope.launch {
            appNavigator?.tryNavigateTo(
                Destination.ChatConsoleScreen(
                    chatId = chatId,
                    isChatOwn = false,
                    partnerName = publicChats.value.find { it.chatID==chatId }!!.name,
                    partnerImage = QuickAdoptionApp.encodePathFile(publicChats.value.find { it.chatID==chatId }!!.profileImage),
                    partnerUID = publicChats.value.find { it.chatID==chatId }!!.ownerID,
                    announcementId = publicChats.value.find { it.chatID==chatId }!!.announcementID
                )
            )
            Log.d("profile image", QuickAdoptionApp.encodePathFile(publicChats.value.find { it.chatID==chatId }!!.profileImage))
        }
    }

    private fun getAllChats(
        onSuccess: (List<PublicAnnouncementChat>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val disposable =
            publicChatsListRepository.getPublicChatsForUser(QuickAdoptionApp.getCurrentUserId()!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { chats -> onSuccess(chats) },
                    { error -> onError(error) }
                )
        disposables.add(disposable)
    }

    fun fillListOfChats(handleInternetError:()->Unit) {
        viewModelScope.launch {
            isFinished.value = false
            if(internetAccessRepository.isInternetAvailable()){
                getAllChats(
                    onSuccess = {
                        isFinished.value = true
                        publicChats.value = it.toMutableList()
                    }, onError = {
                        Log.d("getAllChats error", it.toString())
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