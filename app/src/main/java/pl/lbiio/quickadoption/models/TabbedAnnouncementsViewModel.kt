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
    var ownAnnouncementsList: MutableState<List<OwnAnnouncementListItem>> = mutableStateOf(emptyList())
    val publicAnnouncementsList: MutableState<List<PublicAnnouncementListItem>> = mutableStateOf(emptyList())



    fun initAppNavigator(appNavigator: AppNavigator) {
        this.appNavigator = appNavigator
    }

    fun clearViewModel() {
        country.value = ""
        city.value = ""
        dateRange.value = ""
    }

    fun navigateToInsertingForm() {
        appNavigator?.tryNavigateTo(Destination.AnnouncementFormScreen())
    }

    fun navigateToChatsList(announcementId: Long, name: String) {
        appNavigator?.tryNavigateTo(
            Destination.ChatsScreen(
                announcementId = announcementId,
                animalName = name
            )
        )
    }

    fun navigateToEditingForm(announcementId: Long) {
        appNavigator?.tryNavigateTo(
            Destination.AnnouncementEditScreen(
                announcementId = announcementId
            )
        )
    }

    fun navigateToPublicOffer(announcementId: Long) {
        appNavigator?.tryNavigateTo(
            Destination.PublicOfferDetailsScreen(
                announcementId = announcementId
            )
        )
    }

    fun navigateToPublicAnnouncementsChats() {
        appNavigator?.tryNavigateTo(Destination.PublicAnnouncementsChatsScreen())
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

    fun populateOwnAnnouncementsList() {
        viewModelScope.launch {
            getOwnAnnouncements(
                onSuccess = {
                    ownAnnouncementsList.value = it.toMutableStateList()
                },
                onError = {
                    Log.e("error with loading content", it.toString())
                }
            )
        }
    }

    fun populatePublicAnnouncementsList() {
        viewModelScope.launch {
            getPublicAnnouncements(
                onSuccess = {
                    publicAnnouncementsList.value = it.toMutableList()
                }, onError = {
                    Log.e("error with loading content", it.toString())
                }
            )
        }
    }



}