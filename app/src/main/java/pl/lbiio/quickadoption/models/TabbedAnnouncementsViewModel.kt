package pl.lbiio.quickadoption.models

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
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
import pl.lbiio.quickadoption.repositories.InternetAccessRepository
import pl.lbiio.quickadoption.repositories.TabbedAnnouncementsRepository
import javax.inject.Inject

@HiltViewModel
class TabbedAnnouncementsViewModel @Inject constructor(private val tabbedAnnouncementsRepository: TabbedAnnouncementsRepository, private val internetAccessRepository: InternetAccessRepository) :
    ViewModel() {

    private val disposables = CompositeDisposable()

    private var appNavigator: AppNavigator? = null

    val country: MutableState<String> = mutableStateOf("")
    val city: MutableState<String> = mutableStateOf("")
    val dateRange: MutableState<String> = mutableStateOf("")
    val tabIndex: MutableState<Int> = mutableIntStateOf(0)
    var ownAnnouncementsList: MutableState<List<OwnAnnouncementListItem>> =
        mutableStateOf(emptyList())
    val publicAnnouncementsList: MutableState<List<PublicAnnouncementListItem>> =
        mutableStateOf(emptyList())
    val isFinished: MutableState<Boolean> = mutableStateOf(true)

    private fun getUID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser?.uid ?: throw IllegalStateException("User not logged in")
    }

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

    fun navigateToLeaderBoard() {
        viewModelScope.launch {
            appNavigator?.tryNavigateTo(Destination.LeaderBoardScreen())
            clearViewModel()
        }
    }

    fun navigateToOwnOpinions() {
        viewModelScope.launch {
            appNavigator?.tryNavigateTo(Destination.OpinionsScreen(uid=getUID()))
            clearViewModel()
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

    fun navigateToEditingAccountScreen(){
        viewModelScope.launch {
            appNavigator?.tryNavigateTo(Destination.EditingAccountScreen())
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
            if(internetAccessRepository.isInternetAvailable()){
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
            }else{
                isFinished.value = true
                Log.e("isInternetAvailable", "no!")
                Toast.makeText(QuickAdoptionApp.getAppContext(), "Internet not available", Toast.LENGTH_SHORT).show()
            }

        }

    }

    fun populateOwnAnnouncementsList(handleInternetError:()->Unit) {
        viewModelScope.launch {
            isFinished.value = false
            if(internetAccessRepository.isInternetAvailable()){
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
            }else{
                isFinished.value = true
                Log.e("isInternetAvailable", "no!")
                handleInternetError()
            }
        }
    }

    fun populatePublicAnnouncementsList(handleInternetError:()->Unit) {
        viewModelScope.launch {
            isFinished.value = false
            if(internetAccessRepository.isInternetAvailable()){
                getPublicAnnouncements(
                    onSuccess = {
                        publicAnnouncementsList.value = it.toMutableList()
                        isFinished.value = true
                    }, onError = {
                        Log.e("error with loading content", it.toString())
                        isFinished.value = true
                    }
                )
            }else{
                isFinished.value = true
                Log.e("isInternetAvailable", "no!")
                handleInternetError()            }

        }
    }
}