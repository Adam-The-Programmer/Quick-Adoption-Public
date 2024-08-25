package pl.lbiio.quickadoption.models

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.registerReceiver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import pl.lbiio.quickadoption.QuickAdoptionApp
import pl.lbiio.quickadoption.background.HandleMessagesQueueService
import pl.lbiio.quickadoption.data.ChatMessage
import pl.lbiio.quickadoption.data.ConversationMessage
import pl.lbiio.quickadoption.data.CurrentOpinion
import pl.lbiio.quickadoption.data.LastMessageDTO
import pl.lbiio.quickadoption.data.LocationData
import pl.lbiio.quickadoption.data.OpinionToInsertDTO
import pl.lbiio.quickadoption.data.OwnMessage
import pl.lbiio.quickadoption.navigation.AppNavigator
import pl.lbiio.quickadoption.navigation.Destination
import pl.lbiio.quickadoption.repositories.ChatConsoleRepository
import pl.lbiio.quickadoption.repositories.InternetAccessRepository
import javax.inject.Inject

@HiltViewModel
class ChatConsoleViewModel @Inject constructor(private val chatConsoleRepository: ChatConsoleRepository, private val internetAccessRepository: InternetAccessRepository) :
    ViewModel() {
    private var appNavigator: AppNavigator? = null
    private val disposables = CompositeDisposable()
    val chatId: MutableState<String> = mutableStateOf("")
    val announcementId: MutableState<Long> = mutableStateOf(-1L)
    val isChatOwn: MutableState<Boolean> = mutableStateOf(true)
    val potentialKeeperImage: MutableState<String> = mutableStateOf("")
    val potentialKeeperName: MutableState<String> = mutableStateOf("")
    val potentialKeeperUID: MutableState<String> = mutableStateOf("")
    //val messages: MutableState<MutableList<ConversationMessage>> = mutableStateOf(mutableListOf())
    val messages: MutableLiveData<MutableList<ConversationMessage>> = MutableLiveData(mutableListOf())
    val isFinished: MutableState<Boolean> = mutableStateOf(true)


    fun initAppNavigator(appNavigator: AppNavigator){
        this.appNavigator = appNavigator
        //QuickAdoptionApp.getAppContext().registerReceiver(sendingReceiver, sendingIntentFilter)
        Log.d("zarejestrowano", "receiver")
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

//    fun listenToMessages(){
//        viewModelScope.launch{
//            chatConsoleRepository.listenToMessages(chatId.value) {
//                Log.d("wiadomosci model", messages.toString())
//                messages.value?.clear() //value.toMutableList()
//                messages.value?.addAll(it.toMutableList())
//                chatConsoleRepository.getAllPendingMessages(chatId.value)
//                    ?.let { pendingMessages -> messages.value?.addAll(pendingMessages) }
//            //emit(messages.value)
//            }
//        }
//    }

    fun listenToMessages(){
        viewModelScope.launch{
            chatConsoleRepository.listenToMessages(chatId.value) { newMessages ->
                val updatedMessages = messages.value.orEmpty().toMutableList()
                updatedMessages.clear()
                updatedMessages.addAll(newMessages)
                chatConsoleRepository.getAllPendingMessages(chatId.value)?.let { pendingMessages ->
                    updatedMessages.addAll(pendingMessages)
                }
                messages.postValue(updatedMessages) // Ensure to post a new instance
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
            chatConsoleRepository.assignKeeperToAnnouncement(potentialKeeperUID.value, announcementId.value)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { onComplete() },
                    { error -> onError(error) }
                )
        disposables.add(disposable)
    }

    private fun makeChatAccepted(onSuccess: (response: Int) -> Unit, onError: (error: Throwable) -> Unit){
        val disposable =
            chatConsoleRepository.makeChatAccepted(announcementId.value, chatId.value)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response -> onSuccess(response) },
                    { error -> onError(error) }
                )
        disposables.add(disposable)
    }

    private fun getLocationData(onSuccess: (response: LocationData) -> Unit, onError: (error: Throwable) -> Unit){
        val disposable =
            chatConsoleRepository.getLocationData(QuickAdoptionApp.getCurrentUserId()!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response -> onSuccess(response) },
                    { error -> onError(error) }
                )
        disposables.add(disposable)
    }

    fun acceptChatAndAssignUser(handleInternetError:()->Unit){
        viewModelScope.launch {
            isFinished.value = false
            if(internetAccessRepository.isInternetAvailable()){
                makeChatAccepted({
                    isFinished.value = true
                    Log.d("tak", it.toString())
                    if(it==-1) {
                        //Log.d("tak", "jest -1")
                        Toast.makeText(QuickAdoptionApp.getAppContext(), "Your announcement is already assigned", Toast.LENGTH_SHORT).show()
                    }
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
            }else{
                isFinished.value = true
                Log.e("isInternetAvailable", "no!")
                handleInternetError()
            }
        }
    }

    fun getParsedLocationText(handleRetrievingData: (location: String) -> Unit, handleInternetError:()->Unit){
        viewModelScope.launch {
            isFinished.value = false
            if(internetAccessRepository.isInternetAvailable()){
                getLocationData({
                    isFinished.value = true
                    handleRetrievingData("City: ${it.city}\n" +
                            "Address: ${it.address}\n" +
                            "Postal Code: ${it.postalCode}")
                }, {
                    isFinished.value = true
                    handleInternetError()
                })
            }
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
            if(internetAccessRepository.isInternetAvailable()){
                chatConsoleRepository.uploadMessage(content, contentType, chatId.value, announcementId.value, isChatOwn.value, {
                    isFinished.value = true
                }, {
                    isFinished.value = true
                    Log.e("uploadMessage error", it.toString())
                })

//                processMessage(content,contentType) { newContent ->
//                    chatConsoleRepository.uploadMessage(newContent, contentType, chatId.value)
//                    if(!isChatOwn.value){
//                        setAnnouncementHaveUnreadMessage({
//                            isFinished.value = true
//                        }, {
//                            Log.d("setAnnouncementHaveUnreadMessage error", it.toString())
//                            isFinished.value = true
//                        })
//                    }
//                }
//                isFinished.value = false
//                setLastMessageForChat(LastMessageDTO(content, contentType, QuickAdoptionApp.getCurrentUserId()!!), {
//                    isFinished.value = true
//                }, {
//                    isFinished.value = true
//                    Log.d("setLastMessageForChat error", it.toString())
//                })
            }else{
                isFinished.value = true
                Log.e("isInternetAvailable", "no!")
                chatConsoleRepository.addMessageToQueue(chatId.value, content, contentType)
                HandleMessagesQueueService.initialize(chatConsoleRepository, internetAccessRepository)
                val intent = Intent(QuickAdoptionApp.getAppContext(), HandleMessagesQueueService()::class.java)
                intent.putExtra("chat_id", chatId.value)
                intent.putExtra("announcement_id", announcementId.value)
                intent.putExtra("is_chat_own", isChatOwn.value)
                ContextCompat.startForegroundService(QuickAdoptionApp.getAppContext(), intent)
                messages.value?.add(ConversationMessage.PendingMessage(content, contentType))
            }
        }
    }

    private fun getCurrentOpinion(receiver: String, author: String, onComplete: (opinion: CurrentOpinion) -> Unit, onError: (error: Throwable) -> Unit){
        val disposable =
            chatConsoleRepository.getCurrentOpinion(receiver, author)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { opinion -> onComplete(opinion) },
                    { error -> onError(error) }
                )
        disposables.add(disposable)
    }

    fun getCurrentOpinionData(receiver: String, author: String, handleRetrievingData: (opinion: CurrentOpinion) -> Unit, handleInternetError:()->Unit){
        viewModelScope.launch {
            isFinished.value = false
            if(internetAccessRepository.isInternetAvailable()){
                getCurrentOpinion(receiver, author,{
                    isFinished.value = true
                    handleRetrievingData(it)
                }, {
                    isFinished.value = true
                    handleInternetError()
                })
            }
        }
    }

    private fun insertOpinion(rate: Int, opinion: String, onComplete: () -> Unit, onError: (error: Throwable) -> Unit){
        val disposable =
            chatConsoleRepository.insertOpinion(OpinionToInsertDTO(QuickAdoptionApp.getCurrentUserId()!!, potentialKeeperUID.value, rate, opinion))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { onComplete() },
                    { error -> onError(error) }
                )
        disposables.add(disposable)
    }

    private fun updateOpinion(opinionId: Int, opinion: String, rate: Int, timestamp: Long, onComplete: () -> Unit, onError: (error: Throwable) -> Unit){
        val disposable =
            chatConsoleRepository.updateOpinion(opinionId, opinion, rate, timestamp)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { onComplete() },
                    { error -> onError(error) }
                )
        disposables.add(disposable)
    }

    fun rate(rate: Int, opinion: String, opinionID: Int, handleInternetError:()->Unit){
        viewModelScope.launch {
            isFinished.value = false
            if(internetAccessRepository.isInternetAvailable()){
                if(opinionID != -1){
                    updateOpinion(opinionID, opinion, rate, System.currentTimeMillis(),{
                        isFinished.value = true
                    }, {
                        Log.d("updating opinion  error", it.toString())
                        isFinished.value = true
                    })
                }else{
                    insertOpinion(rate, opinion, {
                        isFinished.value = true
                    }, {
                        Log.d("inserting opinion  error", it.toString())
                        isFinished.value = true
                    })
                }

            }else{
                isFinished.value = true
                Log.e("isInternetAvailable", "no!")
                handleInternetError()
            }

        }
    }
}