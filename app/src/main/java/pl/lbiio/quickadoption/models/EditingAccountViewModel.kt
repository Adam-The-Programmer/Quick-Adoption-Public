package pl.lbiio.quickadoption.models

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import pl.lbiio.quickadoption.QuickAdoptionApp
import pl.lbiio.quickadoption.data.Opinion
import pl.lbiio.quickadoption.data.UserCurrentData
import pl.lbiio.quickadoption.navigation.AppNavigator
import pl.lbiio.quickadoption.repositories.EditingAccountRepository
import pl.lbiio.quickadoption.repositories.InternetAccessRepository
import javax.inject.Inject

@HiltViewModel
data class EditingAccountViewModel @Inject constructor(private val editingAccountRepository: EditingAccountRepository, private val internetAccessRepository: InternetAccessRepository) :
    ViewModel() {
    private var appNavigator: AppNavigator? = null
    private val disposables = io.reactivex.disposables.CompositeDisposable()
    val name: MutableState<String> = mutableStateOf("")
    val surname: MutableState<String> = mutableStateOf("")
    val phone: MutableState<String> = mutableStateOf("")
    val country: MutableState<String> = mutableStateOf("")
    val city: MutableState<String> = mutableStateOf("")
    val address: MutableState<String> = mutableStateOf("")
    val postal: MutableState<String> = mutableStateOf("")
    val description: MutableState<String> = mutableStateOf("")
    val path: MutableState<String> = mutableStateOf("")
    var isFinished: MutableState<Boolean> = mutableStateOf(true)


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
        viewModelScope.launch {
            name.value = ""
            surname.value = ""
            phone.value = ""
            country.value = ""
            city.value = ""
            address.value = ""
            postal.value = ""
            description.value = ""
            path.value = ""
            isFinished.value = true
            disposables.clear()
        }
    }

    fun inflateInterfaceWithData(handleInternetError: () -> Unit) {
        viewModelScope.launch {
            isFinished.value = false
            if (internetAccessRepository.isInternetAvailable()) {
                getCurrentUserData(QuickAdoptionApp.getCurrentUserId(),{user->
                    isFinished.value = true
                    name.value = user.name
                    surname.value = user.surname
                    phone.value = user.phone
                    country.value = user.country
                    city.value = user.city
                    address.value = user.address
                    postal.value = user.postalCode
                    description.value = user.userDescription
                    path.value = user.profileImage
                }, {
                    isFinished.value = true
                    Log.d("getCurentUserData error", it.toString())
                })
            } else {
                isFinished.value = true
                Log.e("isInternetAvailable", "no!")
                handleInternetError()
            }

        }
    }

    private fun getCurrentUserData(UID: String, onComplete: (user: UserCurrentData) -> Unit, onError: (error: Throwable) -> Unit){
        val disposable =
            editingAccountRepository.getUserCurrentData(UID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { user -> onComplete(user) },
                    { error -> onError(error) }
                )
        disposables.add(disposable)
    }

    private fun updateUser(
        user: UserCurrentData,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val disposable =
            editingAccountRepository.updateUser(QuickAdoptionApp.getCurrentUserId(), user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { onSuccess() },
                    { error -> onError(error) }
                )
        disposables.add(disposable)
    }

    fun applyAccountData(onSuccess: ()->Unit, handleInternetError: ()->Unit){
        viewModelScope.launch {
            isFinished.value = false
            if (internetAccessRepository.isInternetAvailable()) {
                updateUser(UserCurrentData(phone.value, name.value, surname.value, country.value, city.value, address.value, postal.value, description.value, path.value),{
                    isFinished.value = true
                    onSuccess()
                }, {
                    isFinished.value = true
                    Log.d("getCurentUserData error", it.toString())
                })
            } else {
                isFinished.value = true
                Log.e("isInternetAvailable", "no!")
                handleInternetError()
            }

        }
    }

}
