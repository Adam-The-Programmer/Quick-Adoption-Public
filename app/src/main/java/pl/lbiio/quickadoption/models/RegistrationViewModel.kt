package pl.lbiio.quickadoption.models

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import pl.lbiio.quickadoption.MainActivity
import pl.lbiio.quickadoption.QuickAdoptionApp
import pl.lbiio.quickadoption.data.User
import pl.lbiio.quickadoption.navigation.AppNavigator
import pl.lbiio.quickadoption.repositories.RegistrationRepository
import javax.inject.Inject


@HiltViewModel
class RegistrationViewModel @Inject constructor(private val registrationRepository: RegistrationRepository) :
    ViewModel() {
    private var appNavigator: AppNavigator? = null
    private val disposables = io.reactivex.disposables.CompositeDisposable()



    val registrationStep: MutableState<Int> = mutableIntStateOf(1)
    val name: MutableState<String> = mutableStateOf("")
    val surname: MutableState<String> = mutableStateOf("")
    val phone: MutableState<String> = mutableStateOf("")
    val country: MutableState<String> = mutableStateOf("")
    val city: MutableState<String> = mutableStateOf("")
    val address: MutableState<String> = mutableStateOf("")
    val postal: MutableState<String> = mutableStateOf("")
    val email: MutableState<String> = mutableStateOf("")
    val password: MutableState<String> = mutableStateOf("")
    val retypedPassword: MutableState<String> = mutableStateOf("")
    val description: MutableState<String> = mutableStateOf("")
    val path: MutableState<String> = mutableStateOf("")
    var isFinished: MutableState<Boolean> = mutableStateOf(true)


    fun initAppNavigator(appNavigator: AppNavigator) {
        this.appNavigator = appNavigator
    }

    fun navigateUp(){
        viewModelScope.launch{
            appNavigator?.tryNavigateBack()
            clearViewModel()
        }
    }

   private fun clearViewModel(){
        viewModelScope.launch{
            registrationStep.value = 1
            name.value = ""
            surname.value = ""
            phone.value = ""
            country.value = ""
            city.value = ""
            address.value = ""
            postal.value = ""
            email.value = ""
            password.value = ""
            retypedPassword.value = ""
            description.value = ""
            path.value = ""
            isFinished.value = true
            disposables.clear()
        }
    }

    fun moveToNextStep() {
        viewModelScope.launch{
            when (registrationStep.value) {
                1 -> {
                    if (email.value.isNotEmpty() &&
                        password.value.isNotEmpty() &&
                        retypedPassword.value.isNotEmpty()
                    ) {
                        if (password.value == retypedPassword.value) {
                            if (password.value.length >= 6) {
                                tryRegister()
                            } else {
                                Toast.makeText(
                                    QuickAdoptionApp.getAppContext(),
                                    "password mus be minimum 6 characters",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                QuickAdoptionApp.getAppContext(),
                                "passwords not matching",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            QuickAdoptionApp.getAppContext(),
                            "Fill all inputs",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                2 -> {
                    registrationStep.value = 3
                    if (name.value.isNotEmpty() &&
                        surname.value.isNotEmpty() &&
                        phone.value.isNotEmpty() &&
                        country.value.isNotEmpty() &&
                        city.value.isNotEmpty() &&
                        address.value.isNotEmpty() &&
                        postal.value.isNotEmpty()
                    ) {
                        if (isPhoneValid(phone.value)) {
                            if (isPostalCodeValid(postal.value)) {
                                registrationStep.value = 3
                            } else {
                                Toast.makeText(
                                    QuickAdoptionApp.getAppContext(),
                                    "postal code should look like xx-xxx",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                QuickAdoptionApp.getAppContext(),
                                "phone number must contain country code",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            QuickAdoptionApp.getAppContext(),
                            "Fill all inputs",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    fun moveToPreviousStep() {
        viewModelScope.launch{
            when (registrationStep.value) {
                2 -> {
                    registrationStep.value = 1
                }

                3 -> {
                    registrationStep.value = 2
                }
            }
        }
    }

    private fun isPostalCodeValid(text: String): Boolean {
        val postalCodePattern = Regex("\\d{2}-\\d{3}")
        return postalCodePattern.matches(text)
    }

    private fun isPhoneValid(phoneNumber: String): Boolean {
        val countryCodePattern = Regex("^\\+\\d{1,3}-?\\d{9}$")
        return countryCodePattern.matches(phoneNumber)
    }

    private fun tryRegister() {
        isFinished.value = false
        viewModelScope.launch {
            Log.d("logowanie", "tak")
            registrationRepository.canRegister(
                email.value
            ) {
                if (it) registrationStep.value = 2
                else Toast.makeText(
                    QuickAdoptionApp.getAppContext(),
                    "Your account exists. Please log in",
                    Toast.LENGTH_SHORT
                ).show()
                isFinished.value = true
            }
        }
    }



    private fun startListeningToEmailVerification() {
        QuickAdoptionApp.getAuth()?.currentUser?.reload()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = QuickAdoptionApp.getAuth()?.currentUser
                val isEmailVerified = user?.isEmailVerified ?: false
                Log.d("isVerified", isEmailVerified.toString())
                if (!isEmailVerified) {
                    startListeningToEmailVerification()
                } else {
                    isFinished.value = true
                    QuickAdoptionApp.getCurrentUserId()?.let { userID ->
                        registrationRepository.uploadProfileImage("$userID-profile", path.value) {
                            insertUser(
                                User(
                                    userID,
                                    email.value,
                                    phone.value,
                                    name.value,
                                    surname.value,
                                    country.value,
                                    city.value,
                                    address.value,
                                    postal.value,
                                    description.value,
                                    it,
                                    "0",
                                    "0"
                                ),
                                onComplete = {
                                    viewModelScope.launch {
                                        registrationRepository.uploadUserToFirebase(email.value)
                                    }
                                    val mainActivityIntent = Intent(
                                        QuickAdoptionApp.getAppContext(),
                                        MainActivity::class.java
                                    )
                                    mainActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    QuickAdoptionApp.getAppContext()
                                        .startActivity(mainActivityIntent)
                                },
                                onError = {

                                }
                            )
                        }

                    }
                }
            } else {
                // Handle the task failure if needed
            }
        }

    }

    fun finishRegistration() {
        viewModelScope.launch{
            registrationRepository.register(email.value, password.value).addOnSuccessListener {
                QuickAdoptionApp.getCurrentUser()!!.sendEmailVerification()
                Toast.makeText(QuickAdoptionApp.getAppContext(), "We sent verification email on your E-mail", Toast.LENGTH_SHORT).show()
                isFinished.value = false
                startListeningToEmailVerification()
            }
        }
    }

    private fun insertUser(user: User, onComplete: () -> Unit, onError: (Throwable) -> Unit) {
        val disposable = registrationRepository.insertUser(user)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { onComplete() },
                { error -> onError(error) }
            )
        disposables.add(disposable)
    }
}