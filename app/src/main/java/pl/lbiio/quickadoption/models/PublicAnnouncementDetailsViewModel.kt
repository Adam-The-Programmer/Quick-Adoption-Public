package pl.lbiio.quickadoption.models

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import pl.lbiio.quickadoption.QuickAdoptionApp
import pl.lbiio.quickadoption.data.ApplicationForAdoptionDTO
import pl.lbiio.quickadoption.data.OwnAnnouncementListItem
import pl.lbiio.quickadoption.data.PublicAnnouncementDetails
import pl.lbiio.quickadoption.navigation.AppNavigator
import pl.lbiio.quickadoption.repositories.PublicAnnouncementDetailsRepository
import javax.inject.Inject

@HiltViewModel
class PublicAnnouncementDetailsViewModel @Inject constructor(private val publicAnnouncementDetailsRepository: PublicAnnouncementDetailsRepository) :
    ViewModel() {
    private var appNavigator: AppNavigator? = null
    private val disposables = CompositeDisposable()

    val announcementID: MutableState<Long> = mutableStateOf(-1L)
    val isFinished: MutableState<Boolean> = mutableStateOf(true)


    val announcementDetails: MutableState<PublicAnnouncementDetails> =
        mutableStateOf(PublicAnnouncementDetails())
    val message: MutableState<String> = mutableStateOf("I want to adopt your pet")

    fun initAppNavigator(appNavigator: AppNavigator) {
        this.appNavigator = appNavigator
    }


    fun navigateUp() {
        appNavigator?.tryNavigateBack()
    }

    fun clearViewModel() {
        message.value = "I want to adopt your pet"
        announcementDetails.value.clearObject()
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

    fun applyForAdoption(
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
                QuickAdoptionApp.getCurrentUserId()!!,
                lastMessageContent,
                "text",
                QuickAdoptionApp.getCurrentUserId()!!

            )
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { onComplete() },
                { error -> onError(error) }
            )
        disposables.add(disposable)
    }

    fun fillDetailsObject() {
        isFinished.value = false
        getDetailsOfOffer(
            onSuccess = {
                isFinished.value = true
                announcementDetails.value = it
                Log.d("dane", announcementDetails.value.dateRange.toString())
            },
            onError = {

            }
        )
    }

    fun initConversation() {
        isFinished.value = false
        publicAnnouncementDetailsRepository.createDocumentAndGetID(message.value,
            {chatID->
                applyForAdoption(
                    message.value,
                    chatID,
                    announcementDetails.value.ownerID,
                    {
                        isFinished.value = true
                    },
                    {

                    }
                )
            }, {

            })
    }


}