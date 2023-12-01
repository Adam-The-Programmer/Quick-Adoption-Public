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
import pl.lbiio.quickadoption.data.ChatMessage
import pl.lbiio.quickadoption.data.LastMessageDTO
import pl.lbiio.quickadoption.navigation.AppNavigator
import pl.lbiio.quickadoption.navigation.Destination
import pl.lbiio.quickadoption.repositories.ChatConsoleRepository
import javax.inject.Inject

@HiltViewModel
class ChatConsoleViewModel @Inject constructor(private val chatConsoleRepository: ChatConsoleRepository) :
    ViewModel() {
    private var appNavigator: AppNavigator? = null
    private val disposables = CompositeDisposable()
    val chatId: MutableState<String> = mutableStateOf("")
    val announcementId: MutableState<Long> = mutableStateOf(-1L)
    val isChatOwn: MutableState<Boolean> = mutableStateOf(true)
    val potentialKeeperImage: MutableState<String> = mutableStateOf("")
    val potentialKeeperName: MutableState<String> = mutableStateOf("")
    val potentialKeeperUID: MutableState<String> = mutableStateOf("")
    val conversation: MutableState<List<ChatMessage>> = mutableStateOf(emptyList())
    val isFinished: MutableState<Boolean> = mutableStateOf(true)


    fun initAppNavigator(appNavigator: AppNavigator){
        this.appNavigator = appNavigator
    }

    fun navigateUp() {
        viewModelScope.launch {
            appNavigator?.tryNavigateBack()
            clearViewModel()
        }
    }

    private fun clearViewModel(){
        viewModelScope.launch {
            chatId.value = ""
            announcementId.value = -1L
            isChatOwn.value = true
            potentialKeeperImage.value = ""
            potentialKeeperName.value = ""
            potentialKeeperUID.value = ""
            conversation.value = emptyList()
            isFinished.value = true
            disposables.clear()
        }
    }

    fun navigateToOpinions() {
        viewModelScope.launch{
            appNavigator?.tryNavigateTo(
                Destination.OpinionsScreen(
                    uid = potentialKeeperUID.value
                )
            )
        }
    }

    fun listenToMessages(){
        viewModelScope.launch{
            chatConsoleRepository.listenToMessages(chatId.value) {
                conversation.value = it.toMutableList()
            }
        }
    }

    private fun setAnnouncementHaveUnreadMessage(onComplete: () -> Unit, onError: (error: Throwable) -> Unit){
        val disposable =
            chatConsoleRepository.setAnnouncementHaveUnreadMessage(announcementId.value)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { onComplete() },
                    { error -> onError(error) }
                )
        disposables.add(disposable)
    }

    private fun assignKeeperToAnnouncement(onComplete: () -> Unit, onError: (error: Throwable) -> Unit){
        val disposable =
            chatConsoleRepository.assignKeeperToAnnouncement(QuickAdoptionApp.getCurrentUserId()!!, announcementId.value)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { onComplete() },
                    { error -> onError(error) }
                )
        disposables.add(disposable)
    }

    private fun makeChatAccepted(onComplete: () -> Unit, onError: (error: Throwable) -> Unit){
        val disposable =
            chatConsoleRepository.makeChatAccepted(announcementId.value, chatId.value)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { onComplete() },
                    { error -> onError(error) }
                )
        disposables.add(disposable)
    }

    fun acceptChatAndAssignUser(){
        viewModelScope.launch {
            isFinished.value = false
            makeChatAccepted({
                isFinished.value = true
            }, {
                isFinished.value = true
                Log.d("makeChatAccepted error", it.toString())
            })
            isFinished.value = false
            assignKeeperToAnnouncement({
                isFinished.value = true
            }, {
                isFinished.value = true
                Log.d("assignKeeperToAnnouncement error", it.toString())
            })
        }
    }

    private fun setLastMessageForChat(lastMessageDTO: LastMessageDTO, onComplete: () -> Unit, onError: (error: Throwable) -> Unit){
        val disposable =
            chatConsoleRepository.setLastMessageForChat(chatId.value, lastMessageDTO)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { onComplete() },
                    { error -> onError(error) }
                )
        disposables.add(disposable)
    }

    private fun processMessage(content: String, contentType: String, action: (newContent: String) -> Unit){
        if(contentType=="image"){
            chatConsoleRepository.uploadImageToFirebase("${System.currentTimeMillis()}-${chatId.value}", content){url->
                action(url)
            }
        }
        else{
            action(content)
        }
    }

    fun uploadMessage(content: String, contentType: String){
        viewModelScope.launch {
            isFinished.value = false
            processMessage(content,contentType) { newContent ->
                chatConsoleRepository.uploadMessage(newContent, contentType, chatId.value)
                if(!isChatOwn.value){
                    setAnnouncementHaveUnreadMessage({
                        isFinished.value = true
                    }, {
                        Log.d("setAnnouncementHaveUnreadMessage error", it.toString())
                        isFinished.value = true
                    })
                }
            }
            isFinished.value = false
            setLastMessageForChat(LastMessageDTO(content, contentType, QuickAdoptionApp.getCurrentUserId()!!), {
                isFinished.value = true
            }, {
                isFinished.value = true
                Log.d("setLastMessageForChat error", it.toString())
            })
        }
    }
}