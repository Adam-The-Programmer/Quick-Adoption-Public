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
import pl.lbiio.quickadoption.data.ApplicationForAdoptionDTO
import pl.lbiio.quickadoption.data.PublicAnnouncementDetails
import pl.lbiio.quickadoption.navigation.AppNavigator
import pl.lbiio.quickadoption.navigation.Destination
import pl.lbiio.quickadoption.repositories.InternetAccessRepository
import pl.lbiio.quickadoption.repositories.PublicAnnouncementDetailsRepository
import javax.inject.Inject

@HiltViewModel
class PublicAnnouncementDetailsViewModel @Inject constructor(
    private val publicAnnouncementDetailsRepository: PublicAnnouncementDetailsRepository,
    private val internetAccessRepository: InternetAccessRepository
) :
    ViewModel() {
    private var appNavigator: AppNavigator? = null
    private val disposables = CompositeDisposable()

    val announcementID: MutableState<Long> = mutableStateOf(-1L)
    val announcementDetails: MutableState<PublicAnnouncementDetails> =
        mutableStateOf(PublicAnnouncementDetails())
    val message: MutableState<String> = mutableStateOf("I want to adopt your pet")
    val isFinished: MutableState<Boolean> = mutableStateOf(true)

    fun initAppNavigator(appNavigator: AppNavigator) {
        this.appNavigator = appNavigator
    }


    fun navigateUp() {
        viewModelScope.launch {
            appNavigator?.tryNavigateBack()
            clearViewModel()
        }
    }

    private fun getBackToMainScreen() {
        viewModelScope.launch {
            appNavigator?.tryNavigateTo(Destination.TabbedScreen())
            clearViewModel()
        }
    }

    private fun clearViewModel() {
        viewModelScope.launch {
            message.value = "I want to adopt your pet"
            announcementDetails.value.clearObject()
            announcementID.value = -1L
            isFinished.value = true
            disposables.clear()
        }
    }

    private fun getDetailsOfOffer(
        onSuccess: (details: PublicAnnouncementDetails) -> Unit,
        onError: (error: Throwable) -> Unit
    ) {
        val disposable =
            publicAnnouncementDetailsRepository.getParticularPublicAnnouncement(announcementID.value)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { details -> onSuccess(details) },
                    { error -> onError(error) }
                )
        disposables.add(disposable)
    }

    private fun setAnnouncementHaveUnreadMessage(
        onComplete: () -> Unit,
        onError: (error: Throwable) -> Unit
    ) {
        val disposable =
            publicAnnouncementDetailsRepository.setAnnouncementHaveUnreadMessage(announcementID.value)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { onComplete() },
                    { error -> onError(error) }
                )
        disposables.add(disposable)
    }

    private fun applyForAdoption(
        lastMessageContent: String,
        chatID: String,
        ownerID: String,
        onComplete: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val disposable = publicAnnouncementDetailsRepository.applyForAdoption(
            ApplicationForAdoptionDTO(
                announcementID.value,
                chatID,
                ownerID,
                QuickAdoptionApp.getCurrentUserId(),
                lastMessageContent,
                "text",
                QuickAdoptionApp.getCurrentUserId()

            )
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    onComplete()
                    getBackToMainScreen()
                },
                { error -> onError(error) }
            )
        disposables.add(disposable)
    }

    fun fillDetailsObject(handleInternetError: () -> Unit) {
        viewModelScope.launch {
            isFinished.value = false
            if (internetAccessRepository.isInternetAvailable()) {
                getDetailsOfOffer(
                    onSuccess = {
                        isFinished.value = true
                        announcementDetails.value = it
                        Log.d("dane", announcementDetails.value.dateRange)
                    },
                    onError = {
                        isFinished.value = true
                        Log.d("getDetailsOfOffer error", it.toString())
                    }
                )
            } else {
                isFinished.value = true
                Log.e("isInternetAvailable", "no!")
                handleInternetError()
            }

        }
    }

    fun initConversation(handleInternetError: () -> Unit) {
        viewModelScope.launch {
            isFinished.value = false
            if (internetAccessRepository.isInternetAvailable()) {
                publicAnnouncementDetailsRepository.createDocumentAndGetID(message.value,
                    { chatID ->
                        applyForAdoption(
                            message.value,
                            chatID,
                            announcementDetails.value.ownerID,
                            {
                                setAnnouncementHaveUnreadMessage(
                                    {
                                        isFinished.value = true
                                    }, {
                                        isFinished.value = true
                                        Log.d(
                                            "setAnnouncementHaveUnreadMessage error",
                                            it.toString()
                                        )
                                    })


                            },
                            {
                                isFinished.value = true
                                Log.d("applyForAdoption error", it.toString())
                            }
                        )
                    }, {
                        isFinished.value = true
                        Log.d("createDocumentAndGetID error", it.toString())
                    })
            } else {
                isFinished.value = true
                Log.e("isInternetAvailable", "no!")
                handleInternetError()
            }
        }
    }

}