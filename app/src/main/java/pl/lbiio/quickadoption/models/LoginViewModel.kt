package pl.lbiio.quickadoption.models

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pl.lbiio.quickadoption.MainActivity
import pl.lbiio.quickadoption.QuickAdoptionApp
import pl.lbiio.quickadoption.navigation.AppNavigator
import pl.lbiio.quickadoption.navigation.Destination
import pl.lbiio.quickadoption.repositories.LoginRepository
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository) :
    ViewModel() {
    private var appNavigator: AppNavigator? = null
    val email: MutableState<String> = mutableStateOf("adam314pi@gmail.com")
    val password: MutableState<String> = mutableStateOf("Adam1234@pi")
    var isFinished: MutableState<Boolean> = mutableStateOf(true)

//    init{
//        if(QuickAdoptionApp.getCurrentUserId()!=null){
//            val mainActivityIntent = Intent(
//                QuickAdoptionApp.getAppContext(),
//                MainActivity::class.java
//            )
//            mainActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//            QuickAdoptionApp.getAppContext()
//                .startActivity(mainActivityIntent)
//        }
//    }

    fun initAppNavigator(appNavigator: AppNavigator) {
        this.appNavigator = appNavigator
    }

    fun navigateToRegistrationForm() {
        appNavigator?.tryNavigateTo(Destination.RegistrationScreen())
    }

    fun clearViewModel(){
        email.value = ""
        password.value = ""
        isFinished.value = true
    }

    private fun login(email: String, password: String, ctx: Context) {
        viewModelScope.launch {
            loginRepository.login(email, password).addOnSuccessListener {
                if (!QuickAdoptionApp.getCurrentUser()!!.isEmailVerified) {
                    QuickAdoptionApp.getCurrentUser()!!.sendEmailVerification()
                    Toast.makeText(
                        ctx,
                        "We sent verification email on your E-mail",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                Log.d("login", "logowanie")
                startListeningToEmailVerification()
                isFinished.value = false
            }
        }
    }

    fun tryLogIn() {
        viewModelScope.launch {
            isFinished.value = false
            Log.d("login data", "${email.value}, ${password.value}")
            loginRepository.canLogin(
                email.value
            ) {
                Log.d("try login", "ok")
                isFinished.value = true
                if (it) login(email.value, password.value, QuickAdoptionApp.getAppContext())
                else Toast.makeText(QuickAdoptionApp.getAppContext(), "Please create account to log in", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startListeningToEmailVerification() {
        Log.d("krok 1", "wlazl")
        QuickAdoptionApp.getAuth()?.currentUser?.reload()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = QuickAdoptionApp.getAuth()?.currentUser
                val isEmailVerified = user?.isEmailVerified ?: false
                Log.d("isVerified", isEmailVerified.toString())
                if (!isEmailVerified) {
                    startListeningToEmailVerification()
                } else {
                    isFinished.value = true
                    val mainActivityIntent = Intent(
                        QuickAdoptionApp.getAppContext(),
                        MainActivity::class.java
                    )
                    mainActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    QuickAdoptionApp.getAppContext()
                        .startActivity(mainActivityIntent)
                }
            }
            else{
                Log.d("blad", task.exception.toString())
            }
        }
    }
}