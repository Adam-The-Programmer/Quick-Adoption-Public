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
import pl.lbiio.quickadoption.data.PublicAnnouncementChat
import pl.lbiio.quickadoption.navigation.AppNavigator
import pl.lbiio.quickadoption.navigation.Destination
import pl.lbiio.quickadoption.repositories.PublicChatsListRepository
import javax.inject.Inject

@HiltViewModel
class PublicChatsListViewModel @Inject constructor(private val publicChatsListRepository: PublicChatsListRepository) :
    ViewModel() {
    private var appNavigator: AppNavigator? = null
    private val disposables = CompositeDisposable()

    val isFinished: MutableState<Boolean> = mutableStateOf(true)
    val publicChats: MutableState<List<PublicAnnouncementChat>> = mutableStateOf(emptyList())


    fun initAppNavigator(appNavigator: AppNavigator) {
        this.appNavigator = appNavigator
    }

    fun navigateUp() {
        appNavigator?.tryNavigateBack()
    }

    fun navigateToChat(chatId: String) {
        appNavigator?.tryNavigateTo(
            Destination.ChatConsoleScreen(
                chatId = chatId,
                isChatOwn = false
            )
        )
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

    fun fillListOfChats() {
        isFinished.value = false
        getAllChats(
            onSuccess = {
                isFinished.value = true
                publicChats.value = it.toMutableList()
            }, onError = {
                isFinished.value = true
            })
    }
}