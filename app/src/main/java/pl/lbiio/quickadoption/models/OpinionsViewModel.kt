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
import pl.lbiio.quickadoption.data.Opinion
import pl.lbiio.quickadoption.data.PublicAnnouncementChat
import pl.lbiio.quickadoption.navigation.AppNavigator
import pl.lbiio.quickadoption.repositories.ApplyingAnnouncementRepository
import pl.lbiio.quickadoption.repositories.InternetAccessRepository
import pl.lbiio.quickadoption.repositories.OpinionsRepository
import javax.inject.Inject

@HiltViewModel
class OpinionsViewModel @Inject constructor(
    private val opinionsRepository: OpinionsRepository,
    private val internetAccessRepository: InternetAccessRepository
) :
    ViewModel() {

    private var appNavigator: AppNavigator? = null
    private val disposables = CompositeDisposable()
    val receiverID: MutableState<String> = mutableStateOf("")
    val opinions: MutableState<List<Opinion>> = mutableStateOf(emptyList())
    val rate: MutableState<Float> = mutableStateOf(0.0f)
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

    private fun clearViewModel() {
        receiverID.value = ""
        opinions.value = emptyList()
        rate.value = 0.0f
        isFinished.value = true
        disposables.clear()
    }

    fun getKeeperRate(UID: String, handleInternetError: () -> Unit) {
        viewModelScope.launch {
            isFinished.value = false
            if (internetAccessRepository.isInternetAvailable()) {
                getRateOfUser(UID, {
                    isFinished.value = true
                    rate.value = it
                }, {
                    isFinished.value = true
                    Log.d("getKeeperRate error", it.toString())
                })
            } else {
                isFinished.value = true
                Log.e("isInternetAvailable", "no!")
                handleInternetError()
            }
        }
    }

    fun fillListOfOpinions(receiverID: String, handleInternetError: () -> Unit) {
        viewModelScope.launch {
            isFinished.value = false
            if (internetAccessRepository.isInternetAvailable()) {
                getOpinions(receiverID, {
                    isFinished.value = true
                    opinions.value = it.toMutableList()
                }, {
                    isFinished.value = true
                    Log.d("fillListOfOpinions error", it.toString())
                })
            } else {
                isFinished.value = true
                Log.e("isInternetAvailable", "no!")
                handleInternetError()
            }
        }
    }

    fun inflateInterfaceWithData(handleInternetError: () -> Unit) {
        viewModelScope.launch {
            isFinished.value = false
            if (internetAccessRepository.isInternetAvailable()) {
                getOpinions(receiverID.value, {list->
                    isFinished.value = true
                    opinions.value = list.toMutableList()
                    getRateOfUser(receiverID.value, {
                        isFinished.value = true
                        rate.value = it
                    }, {
                        isFinished.value = true
                        Log.d("getKeeperRate error", it.toString())
                    })
                }, {
                    isFinished.value = true
                    Log.d("fillListOfOpinions error", it.toString())
                })
            } else {
                isFinished.value = true
                Log.e("isInternetAvailable", "no!")
                handleInternetError()
            }

        }
    }

    private fun getOpinions(
        receiverID: String,
        onSuccess: (List<Opinion>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val disposable =
            opinionsRepository.getOpinions(receiverID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { opinions -> onSuccess(opinions) },
                    { error -> onError(error) }
                )
        disposables.add(disposable)
    }

    private fun getRateOfUser(
        UID: String,
        onSuccess: (Float) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val disposable =
            opinionsRepository.getRateOfUser(UID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { rate -> onSuccess(rate) },
                    { error -> onError(error) }
                )
        disposables.add(disposable)
    }
}