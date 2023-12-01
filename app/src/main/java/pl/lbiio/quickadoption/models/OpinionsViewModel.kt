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
import pl.lbiio.quickadoption.repositories.OpinionsRepository
import javax.inject.Inject

@HiltViewModel
class OpinionsViewModel @Inject constructor(private val opinionsRepository: OpinionsRepository) :
    ViewModel() {

    private var appNavigator: AppNavigator? = null
    private val disposables = CompositeDisposable()
    val receiverID: MutableState<String> = mutableStateOf("")
    val opinions: MutableState<List<Opinion>> = mutableStateOf(emptyList())
    val rate: MutableState<Float> = mutableStateOf(0.0f)

    fun initAppNavigator(appNavigator: AppNavigator) {
        this.appNavigator = appNavigator
    }

    fun navigateUp() {
        viewModelScope.launch {
            appNavigator?.tryNavigateBack()
        }
    }

    fun getKeeperRate(UID: String) {
        viewModelScope.launch {
            getRateOfUser(UID, {
                rate.value = it
            }, {
                Log.d("getKeeperRate error", it.toString())
            })
        }
    }

    fun fillListOfOpinions(receiverID :String) {
        viewModelScope.launch {
            getOpinions(receiverID, {
                opinions.value = it.toMutableList()
            }, {
                Log.d("fillListOfOpinions error", it.toString())
            })
        }
    }

    private fun getOpinions(receiverID :String, onSuccess: (List<Opinion>) -> Unit, onError: (Throwable) -> Unit) {
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

    private fun getRateOfUser(UID: String, onSuccess: (Float) -> Unit, onError: (Throwable) -> Unit) {
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