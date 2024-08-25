package pl.lbiio.quickadoption.models

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import pl.lbiio.quickadoption.QuickAdoptionApp
import pl.lbiio.quickadoption.data.OwnAnnouncement
import pl.lbiio.quickadoption.navigation.AppNavigator
import pl.lbiio.quickadoption.repositories.ApplyingAnnouncementRepository
import pl.lbiio.quickadoption.repositories.InternetAccessRepository
import javax.inject.Inject

@HiltViewModel
class ApplyAnnouncementViewModel @Inject constructor(private val applyingAnnouncementRepository: ApplyingAnnouncementRepository, private val internetAccessRepository: InternetAccessRepository) :
    ViewModel() {
    private val disposables = CompositeDisposable()
    private var appNavigator: AppNavigator? = null


    val isFinished: MutableState<Boolean> = mutableStateOf(true)
    val announcementId: MutableState<Long> = mutableStateOf(-1L)
    val species: MutableState<String> = mutableStateOf("")
    val breed: MutableState<String> = mutableStateOf("")
    val animalName: MutableState<String> = mutableStateOf("")
    val dateRange: MutableState<String> = mutableStateOf("")
    val food: MutableState<String> = mutableStateOf("")
    val animalImage: MutableState<String> = mutableStateOf("")
    val animalDescription: MutableState<String> = mutableStateOf("")

    fun initAppNavigator(appNavigator: AppNavigator) {
        this.appNavigator = appNavigator
    }

    fun getAnnouncementById(handleInternetError:()->Unit) {
        viewModelScope.launch {
            isFinished.value = false
            if(internetAccessRepository.isInternetAvailable()){
                getParticularOwnAnnouncement(
                    announcementId.value,
                    onSuccess = { announcement ->
                        species.value = announcement.species
                        breed.value = announcement.breed
                        animalName.value = announcement.animalName
                        dateRange.value = announcement.dateRange
                        food.value = announcement.food
                        animalImage.value = announcement.animalImage
                        animalDescription.value = announcement.animalDescription

                        isFinished.value = true
                        Log.d("getting announcement", "Successful")
                    }, onError = {
                        isFinished.value = true
                        Log.d("getting announcement", "Error")
                    }
                )
            }else{
                isFinished.value = true
                Log.e("isInternetAvailable", "no!")
                handleInternetError()
            }

        }


    }


    fun navigateUp() {
        viewModelScope.launch {
            clearViewModel()
            appNavigator?.tryNavigateBack()
        }
    }

    fun clearViewModel() {
        viewModelScope.launch {
            isFinished.value = true
            announcementId.value = -1L
            species.value = ""
            breed.value = ""
            animalName.value = ""
            dateRange.value = ""
            food.value = ""
            animalImage.value = ""
            animalDescription.value = ""
            disposables.clear()
        }
    }

    fun applyAnnouncement(handleInternetError:()->Unit) {
        viewModelScope.launch {
            isFinished.value = false
            if(internetAccessRepository.isInternetAvailable()){
                QuickAdoptionApp.getCurrentUserId()?.let { userID ->
                    applyingAnnouncementRepository.uploadAnimalImage(
                        "$userID-${System.currentTimeMillis()}",
                        animalImage.value
                    ) { url ->
                        if (announcementId.value == -1L) {
                            addAnnouncement(QuickAdoptionApp.getCurrentUserId()!!,
                                OwnAnnouncement(
                                    -1L,
                                    animalName.value,
                                    species.value,
                                    breed.value,
                                    dateRange.value,
                                    food.value,
                                    url,
                                    animalDescription.value
                                ),
                                onComplete = {
                                    Log.d("Inserting", "Successful")
                                    isFinished.value = true
                                    navigateUp()
                                },
                                onError = {
                                    isFinished.value = true
                                    Log.d("Inserting", "Error")
                                    it.printStackTrace()
                                }
                            )

                        } else {
                            updateAnnouncement(OwnAnnouncement(
                                announcementId.value,
                                animalName.value,
                                species.value,
                                breed.value,
                                dateRange.value,
                                food.value,
                                url,
                                animalDescription.value
                            ),
                                onComplete = {
                                    isFinished.value = true
                                    Log.d("updating", "Successful")
                                    navigateUp()
                                },
                                onError = {
                                    isFinished.value = true
                                    Log.d("Inserting", "Error")
                                    it.printStackTrace()
                                })
                        }
                    }
                }
            }else{
                isFinished.value = true
                Log.e("isInternetAvailable", "no!")
                handleInternetError()
            }

        }
    }

    private fun addAnnouncement(
        UID: String, announcement: OwnAnnouncement,
        onComplete: () -> Unit, onError: (Throwable) -> Unit
    ) {
        val disposable = applyingAnnouncementRepository.addAnnouncement(UID, announcement)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { onComplete() },
                { error -> onError(error) }
            )
        disposables.add(disposable)
    }

    private fun updateAnnouncement(
        announcement: OwnAnnouncement,
        onComplete: () -> Unit, onError: (Throwable) -> Unit
    ) {
        val disposable = applyingAnnouncementRepository.updateAnnouncement(announcement)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { onComplete() },
                { error -> onError(error) }
            )
        disposables.add(disposable)
    }

    private fun getParticularOwnAnnouncement(
        announcementID: Long,
        onSuccess: (OwnAnnouncement) -> Unit, onError: (Throwable) -> Unit
    ) {
        val disposable = applyingAnnouncementRepository.getParticularOwnAnnouncement(announcementID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { announcement -> onSuccess(announcement) },
                { error -> onError(error) }
            )
        disposables.add(disposable)
    }
}