package pl.lbiio.quickadoption.models

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch


import pl.lbiio.quickadoption.QuickAdoptionApp
import pl.lbiio.quickadoption.data.OwnAnnouncementListItem
import pl.lbiio.quickadoption.data.PublicAnnouncementListItem
import pl.lbiio.quickadoption.navigation.AppNavigator
import pl.lbiio.quickadoption.navigation.Destination
import pl.lbiio.quickadoption.repositories.TabbedAnnouncementsRepository
import javax.inject.Inject

@HiltViewModel
class TabbedAnnouncementsViewModel @Inject constructor(private val tabbedAnnouncementsRepository: TabbedAnnouncementsRepository) :
    ViewModel() {

    private val disposables = CompositeDisposable()

    private var appNavigator: AppNavigator? = null

    val country: MutableState<String> = mutableStateOf("Poland")
    val city: MutableState<String> = mutableStateOf("Warsaw")
    val dateRange: MutableState<String> = mutableStateOf("04.10.2023-01.01.2026")
    var ownAnnouncementsList: MutableState<List<OwnAnnouncementListItem>> =
        mutableStateOf(emptyList())
    val publicAnnouncementsList: MutableState<List<PublicAnnouncementListItem>> =
        mutableStateOf(emptyList())
    val isFinished: MutableState<Boolean> = mutableStateOf(true)


    fun initAppNavigator(appNavigator: AppNavigator) {
        this.appNavigator = appNavigator
    }

    fun clearViewModel() {
        viewModelScope.launch {
            country.value = ""
            city.value = ""
            dateRange.value = ""
            ownAnnouncementsList.value = emptyList()
            publicAnnouncementsList.value = emptyList()
            isFinished.value = true
            disposables.clear()
        }
    }

    fun navigateToInsertingForm() {
        viewModelScope.launch {
            appNavigator?.tryNavigateTo(Destination.AnnouncementFormScreen())
            clearViewModel()
        }
    }

    fun navigateToChatsList(announcementId: Long, name: String) {
        viewModelScope.launch {
            appNavigator?.tryNavigateTo(
                Destination.ChatsScreen(
                    announcementId = announcementId,
                    animalName = name
                )
            )
        }
    }

    fun navigateToEditingForm(announcementId: Long) {
        viewModelScope.launch {
            appNavigator?.tryNavigateTo(
                Destination.AnnouncementEditScreen(
                    announcementId = announcementId
                )
            )
        }
    }

    fun navigateToPublicOffer(announcementId: Long) {
        viewModelScope.launch {
            appNavigator?.tryNavigateTo(
                Destination.PublicOfferDetailsScreen(
                    announcementId = announcementId,
                )
            )
        }
    }

    fun navigateToPublicAnnouncementsChats() {
        viewModelScope.launch {
            appNavigator?.tryNavigateTo(Destination.PublicAnnouncementsChatsScreen())
        }
    }

    private fun getOwnAnnouncements(
        onSuccess: (List<OwnAnnouncementListItem>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val disposable =
            tabbedAnnouncementsRepository.getAllOwnAnnouncementList(QuickAdoptionApp.getCurrentUserId()!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { announcements -> onSuccess(announcements) },
                    { error -> onError(error) }
                )
        disposables.add(disposable)
    }

    private fun getPublicAnnouncements(
        onSuccess: (List<PublicAnnouncementListItem>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val disposable = tabbedAnnouncementsRepository.getAllPublicAnnouncementListItems(
            country.value,
            city.value,
            dateRange.value,
            QuickAdoptionApp.getCurrentUserId()!!
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { announcements -> onSuccess(announcements) },
                { error -> onError(error) }
            )
        disposables.add(disposable)
    }

    private fun deleteAnnouncement(
        announcementID: Long,
        onComplete: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val disposable = tabbedAnnouncementsRepository.deleteAnnouncement(announcementID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { onComplete() },
                { error -> onError(error) }
            )
        disposables.add(disposable)
    }

    fun deleteAnnouncementAndRefresh(announcementID: Long) {
        viewModelScope.launch {
            isFinished.value = false
            deleteAnnouncement(
                announcementID,
                {
                    getOwnAnnouncements(
                        onSuccess = {
                            ownAnnouncementsList.value = it.toMutableStateList()
                            isFinished.value = true
                        },
                        onError = {
                            Log.e("error with loading content", it.toString())
                            isFinished.value = true
                        }
                    )
                }, {
                    isFinished.value = true
                    Log.e("error deleting element", it.toString())
                })
        }

    }

    fun populateOwnAnnouncementsList() {
        viewModelScope.launch {
            isFinished.value = false
            getOwnAnnouncements(
                onSuccess = {
                    ownAnnouncementsList.value = it.toMutableStateList()
                    isFinished.value = true
                },
                onError = {
                    Log.e("error with loading content", it.toString())
                    isFinished.value = true
                }
            )
        }
    }

    fun populatePublicAnnouncementsList() {
        viewModelScope.launch {
            isFinished.value = false
            getPublicAnnouncements(
                onSuccess = {
                    publicAnnouncementsList.value = it.toMutableList()
                    isFinished.value = true
                }, onError = {
                    Log.e("error with loading content", it.toString())
                    isFinished.value = true
                }
            )
        }
    }
}