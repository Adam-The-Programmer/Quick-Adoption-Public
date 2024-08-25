package pl.lbiio.quickadoption.models

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import pl.lbiio.quickadoption.data.LeaderBoardItem
import pl.lbiio.quickadoption.data.Opinion
import pl.lbiio.quickadoption.data.OwnAnnouncementChat
import pl.lbiio.quickadoption.navigation.AppNavigator
import pl.lbiio.quickadoption.repositories.ChatConsoleRepository
import pl.lbiio.quickadoption.repositories.InternetAccessRepository
import pl.lbiio.quickadoption.repositories.LeaderBoardRepository
import javax.inject.Inject

@HiltViewModel
class LeaderBoardViewModel @Inject constructor(private val leaderBoardRepository: LeaderBoardRepository, private val internetAccessRepository: InternetAccessRepository) :
    ViewModel() {

    private val disposables = CompositeDisposable()
    private var appNavigator: AppNavigator? = null
    val isFinished: MutableState<Boolean> = mutableStateOf(true)
    val leaderBoard: MutableState<List<LeaderBoardItem>> = mutableStateOf(emptyList())

    private fun getUID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser?.uid ?: throw IllegalStateException("User not logged in")
    }

    private fun getLeaderBoard(
        onSuccess: (List<LeaderBoardItem>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val disposable =
            leaderBoardRepository.getLeaderBoard(getUID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { opinions -> onSuccess(opinions) },
                    { error -> onError(error) }
                )
        disposables.add(disposable)
    }

    fun inflateInterfaceWithData(handleInternetError: () -> Unit) {
        viewModelScope.launch {
            isFinished.value = false
            if (internetAccessRepository.isInternetAvailable()) {
                getLeaderBoard({list->
                    isFinished.value = true
                    leaderBoard.value = list.toMutableList()
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

    fun initAppNavigator(appNavigator: AppNavigator){
        this.appNavigator = appNavigator
    }

    fun navigateUp(){
        viewModelScope.launch {
            appNavigator?.tryNavigateBack()
            clearViewModel()
        }
    }

    fun clearViewModel(){
        viewModelScope.launch {
            leaderBoard.value = emptyList()
            isFinished.value = true
            disposables.clear()
        }
    }
}